# CLAUDE.md


# Build & test
- Build and run all checks: `mvn verify`
- Run one test class: `mvn test -Dtest=ClassName`
- Run the app: `java -jar target/triangle-classifier.jar <a> <b> <c>`

# Code style
- Java 21. Use records for immutable values, enums for fixed sets.
- Clear domain names; no abbreviations, no "Util"/"Manager" classes.
- No new dependencies without asking me first.

# Workflow
- Requirements live in SPEC.md. Do not invent requirements.
- Write or update tests before implementation.
- Never change a test just to make it pass. Tell me instead.
- Run `mvn verify` before saying a task is done, and show the output.
