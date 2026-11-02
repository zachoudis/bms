---
name: commit-push
description: Commit the current changes with a clear message and push the branch to GitHub. Run only when the user calls /commit-push.
argument-hint: "[commit message] [include <new files>]"
disable-model-invocation: true
---

Commit the current changes and push them to GitHub.

## Steps

1. **Look at what changed.** Run `git status --short` and `git diff --stat` (and `git diff --cached --stat`).
   If there is nothing to commit and nothing to push, say so and stop.

2. **Check the build.** Run `mvn verify` (CLAUDE.md requires it). If it fails, show the failing
   output and stop. Do not commit, and do not change any test to make it pass.

3. **Choose the files.** Stage only files git already tracks, with `git add -u` (modified and
   deleted files). Also keep anything the user had already staged.

   **Never add untracked files** (`??` in `git status`), unless the user names them in the
   arguments, for example `/commit-push include WHAT_IF.md`. Don't add them because they look
   related, and don't ask about them. Just list them in the report as "left untracked".

   Even when named, never stage:
   - editor lock and backup files (`.LCK*`, `*~`, `*.swp`)
   - anything under `target/`
   - files that look like secrets (`.env`, keys, credentials)

4. **Write the message.** If the user passed a message (`$ARGUMENTS`, apart from any
   `include …` part), use it as the subject.
   Otherwise write one from the diff: a short subject line in the imperative ("Add …", "Fix …"),
   under 72 characters, then a blank line and a few bullet points if the change needs explaining.
   End the message with the attribution line from the session's git instructions, if there is one.

5. **Commit.** `git commit` with that message. Never use `--no-verify` or `--amend`.

6. **Push.** Push the current branch with `git push`, or `git push -u origin <branch>` if it has no
   upstream yet. Never force-push. If the push is rejected because the remote is ahead, stop and
   tell the user instead of pulling or rebasing on your own.

7. **Report.** Show the commit hash and subject, the files committed, the branch, its GitHub link
   (`https://github.com/zachoudis/bms/tree/<branch>`), and any files left untracked.
