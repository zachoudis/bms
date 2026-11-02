# Design Alternatives Considered

## 1. Splitting into `core` and `cli` (my own decision; the brief didn't ask for it)

- **Split into two packages:** `core` holds the triangle rules and knows nothing about the terminal, and `cli` is a thin adapter that turns arguments into a call to `core` and the result into output and an exit code. This keeps each side testable on its own and lets `core` be reused behind another interface, such as a web API.
- **Rejected keeping everything in one class or package:** it's less code for a small app, but the rules, the output text and the exit codes would be mixed together and harder to test.
- **Rejected separate Maven modules:** packages give the same separation without extra build setup. I'd split into modules if `core` had to be published as a library.

## 2. `Triangle` record vs classifying inside the classifier

- **Kept the `Triangle` record:** it checks the triangle inequality in its constructor, so an invalid triangle can't exist, and the validator only has to parse text.
- **Rejected doing everything in the classifier:** it has one fewer class, but the triangle rule would hold only because `validate` happens to run first.
- **Deciding the type in `Triangle.type()`, not in the classifier:** the type depends only on the sides, and keeping it next to the constructor check means it can never run on an impossible triangle like `1 2 3`.
- **Rejected a data-only `Triangle` with the type decided in the classifier:** it would split the geometry across two classes (an "anemic model"). I'd choose it only if there were several classification schemes, such as by sides and by angles.
- **Rejected a class that computes the type in its constructor and stores it in a field:** a record is shorter and guaranteed immutable, and it can't hold a `type` field without callers passing it in. The type is also cheap to derive from the sides, so storing it would only duplicate information. I'd store it if it were expensive and read often, such as a high-precision area.

## 3. Architecture

- **Exceptions instead of a result type:** a constructor can only report failure by throwing, and a throw skips the later checks without extra `if` statements.
- **Rejected a sealed result type (`Classified | InvalidSides | NotATriangle`):** the compiler would force callers to handle every outcome, but with one fully tested caller that adds types for little gain. I'd choose it for a library.
- **Rejected a sealed `Triangle` hierarchy with a factory (`Equilateral`, `Isosceles`, `Scalene`):** the types don't behave differently, so an enum is enough. I'd switch if each type needed its own logic, such as area.

## 4. Other choices

- **`BigInteger` instead of `long` or `double`:** the spec has no upper limit, `long` can overflow on `a + b`, and `double` loses precision, so only `BigInteger` is exact. The cost is speed, which doesn't matter for one triangle.
- **A regex check before parsing instead of catching `NumberFormatException`:** it separates "not an integer" from "not positive" without using exceptions for control flow, and `[0-9]` rejects non-ASCII digits like `٣` that `BigInteger` would accept.
- **Injected `PrintStream`s instead of writing to `System.out` directly:** the CLI can be tested in-process and fast; the trade-off is that `Main` and `System.exit` are never tested through the real jar.
- **Three explicit parameters instead of a `List` or varargs:** the compiler makes a wrong number of sides impossible, so the core needs no count check.
- **Packages by kind (`enums`, `model`, `exception`) instead of by feature:** easy to scan at this size, with only one feature; I'd switch to packaging by feature if more shapes or features were added.
