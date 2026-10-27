# How I Built This with Claude Code

This file records the main prompts I used to build the triangle classifier with Claude
Code, and why I wrote each one the way I did. The prompts are quoted exactly as I sent
them.

My approach: **I make the decisions, and Claude does the legwork.** I wrote the
requirements and approved the design. Claude wrote the tests before the code, and an
automated check keeps the code honest.

| Step | Stage                     | Claude Code feature used        |
|------|---------------------------|---------------------------------|
| 1    | Project skeleton          | `CLAUDE.md` project memory      |
| 2    | Requirements              | `AskUserQuestion` interview     |
| 3    | Design                    | Plan mode, `@` file references  |
| 4    | Tests first               | Test-driven prompting           |
| 5    | Implementation            | Iterating until the build passes |
| 6    | Guardrails                | `Stop` hook                     |
| 7    | Spec review               | Custom subagent (`spec-reviewer`) |
| 8    | Code review               | `/code-review`                  |

---

## 1. Project skeleton and `CLAUDE.md`

> Create a minimal Maven project for a Java 21 command-line application: groupId
> com.bms, artifactId triangle-classifier, JUnit 5 and AssertJ for tests, and an
> executable jar. No frameworks: no Spring, no other dependencies. Don't write any
> business logic yet.

**Why:** I wanted a small, dependency-free base before any logic existed.

I then wrote [`CLAUDE.md`](CLAUDE.md) myself. It gives the build and test commands, the
code style (Java 21 records and enums, clear domain names), and the workflow rules:

- `SPEC.md` is the only source of requirements.
- Tests come before the implementation.
- Never change a test just to make it pass.
- Run `mvn verify` before saying a task is done.

## 2. Clarifying the requirements

> I need to build a CLI that classifies a triangle as Equilateral, Isosceles or Scalene
> from three side lengths. Interview me using the AskUserQuestion tool about the
> requirements and edge cases I may not have considered: input format, invalid input,
> number types, error handling, exit codes. Don't ask obvious questions. When we're
> done, write SPEC.md. Don't write any code.

**Why:** The brief left a lot open: degenerate triangles, `4.0`, `0`, negative values,
very large numbers, and what to do when several inputs are wrong. I used Claude to find
those gaps. **Every decision in [`SPEC.md`](SPEC.md) is mine.**

## 3. Designing in Plan mode

> Read @SPEC.md and @CLAUDE.md. Propose an implementation plan: classes and their
> responsibilities, how input parsing, validation and classification are separated, and
> which tests you'll write. Keep it simple; this is a small app. Don't edit anything.

**Why:** I spent the most time on this step. I pushed back on Claude's first plan and
added my own ideas until the design was one I would defend in a code review. Plan mode
meant no files changed until I approved the plan.

<!-- TODO: add one concrete example of a change you asked for in the plan. -->

## 4. Tests first

> Following the approved plan and SPEC.md, write the unit tests only. No production code
> beyond empty stubs needed to compile. Use parameterized tests where it makes the cases
> clearer. Then run the tests and show me they fail.

**Why:** The tests encode the spec. Seeing them fail first proves they check something
real.

## 5. Implementation

> Implement the production code to make the tests pass. Don't modify the tests. Run mvn
> verify and iterate until it's green. Show me the final test output.

**Why:** The tests stayed fixed, so Claude could only get the build to pass by writing
correct code.

## 6. A hook that runs the tests

> Write a project-level hook in .claude/settings.json that runs mvn -q test when you
> finish a turn and reports failures back to you, so a turn can't end with failing tests.

**Why:** `CLAUDE.md` gives Claude instructions, but Claude can still miss one. A hook is
different: Claude Code itself runs it, every time Claude finishes a turn.

[`.claude/hooks/run-tests.sh`](.claude/hooks/run-tests.sh) runs the test suite. If any
test fails, it sends a short summary of the failures back to Claude, which then has to
fix them. To avoid an endless loop, Claude gets **one** retry. If the tests still fail
after that, the turn ends and I see a warning. The hook also repeats the
`CLAUDE.md` rule: if a test looks wrong, Claude must tell me instead of editing it.

## 7. Reviewing against the spec with a custom subagent

> Use a subagent to review the full codebase against SPEC.md. Check that every
> requirement and edge case is implemented and tested, that names are clear, and that
> nothing outside the spec was added. Report only issues affecting correctness,
> requirements or readability, not style preferences.

**Why:** `/code-review` looks for bugs in a diff. I also wanted a check against the
requirements: every rule in `SPEC.md` and every row of its Examples table, both
implemented and tested. Running it as a subagent kept its long report out of my main
session. It was also read-only, so it couldn't "fix" anything on its own.

The reviewer ran the test suite and then ran the real jar on edge cases: very large
numbers, `-0` and non-ASCII digits. It found no bugs and no scope creep. It did flag
tests that were misleading:

- A test value contained an invisible non-breaking space, so it looked like a plain space.
- A test case couldn't prove what its test name claimed.

The review was useful, so I saved it as a reusable project subagent,
[`.claude/agents/spec-reviewer.md`](.claude/agents/spec-reviewer.md). It has:

- **Limited tools:** Read, Grep, Glob and Bash, with no Edit or Write. Bash lets it run
  the tests and the jar.
- **A checklist** that goes through the spec section by section, then checks the
  real-process behaviour, scope creep and naming.
- **A "what not to report" list:** style preferences, other architectures, and defensive
  code for cases the spec never mentions.
- **A fixed report format:** severity and `file:line` for each finding, with what it
  **verified** by running kept separate from what it **reasoned** from the code.

When `SPEC.md` changed later, I updated the agent's edge-case list to match the new rules.
To run it, ask Claude to "use the spec-reviewer agent to review the code".

## 8. Code review with `/code-review`

I ran `/code-review` after each round of changes: four times in all. None of the reviews
found a correctness bug. Each one ran `mvn verify` and tried the jar, then reported
weak spots. I treated the findings as suggestions, not orders: I fixed some, changed
some, and rejected some with a reason.

**Fixed**

- **Exit codes came from the order of the enum constants.** `Main` used
  `ExitCode.ordinal()`, so reordering the constants would have silently changed the exit
  codes in the spec. No test checked the numbers 0, 1 and 2, and `ExitCodeTest.java`
  was an empty file. I wrote the test first, then gave each constant an explicit code:
  `SUCCESS(0)`, `INVALID_TRIANGLE(1)`, `USAGE_ERROR(2)`.
- **The "not a triangle" message was built in two places.** The CLI used the raw
  arguments, while `NotATriangleException` had its own message in a different format.
  I asked the reviewer whether the CLI should just print the exception's message. It
  explained why not: the wording users see would then live in a core class. I made the
  exception expose its sides through `sides()`, so the CLI formats both error types the
  same way.

**Rejected, with my reasons**

- **Escaping control characters in error output.** The spec says to show the value as
  given, so changing it would be adding a requirement.
- **Handling a failed write to stdout.** The spec doesn't cover it.
- **Exposing the sides as named fields instead of a `List`.** The second review raised
  this about my `sides()` change. I kept the list: it's a small app, and the sides are
  only read in one place.
- **Keeping the `Triangle` record.** When I removed the record and moved its logic into
  `classify`, the review argued for keeping it, citing the "use records" rule in
  `CLAUDE.md`. I asked Claude to argue both sides, then kept my simpler design, because
  nothing else used the record.

**The guardrails working**

During the `Triangle` refactor, one existing test started failing because its inputs
were no longer a valid triangle. Claude didn't change the test. Following `CLAUDE.md`,
it stopped and asked me, and the `Stop` hook kept reporting the failure at the end of
each turn until I decided. The review also caught that the git index was half staged,
which would have produced a commit that doesn't compile.
