# Triangle Classifier

A command-line tool that takes three side lengths and prints whether they form an
`Equilateral`, `Isosceles` or `Scalene` triangle. The full specification is in
[SPEC.md](SPEC.md).

## Build, test and run

Requires Java 21 and Maven.

```bash
mvn verify                                   # build, run all tests, produce the jar
mvn test -Dtest=TriangleClassifierTest       # run one test class
java -jar target/triangle-classifier.jar 3 4 5
# Scalene
```

## Input rules

- Exactly three arguments, each one a side length. There are no flags (`--help` is
  treated as a side value). Stdin is not read.
- Each side must be an integer matching `-?[0-9]+`. Values like `abc` or `4.0` are
  rejected.
- Each side must be greater than zero.
- There is no upper limit. Arithmetic is exact, with no overflow.
- The sides must satisfy the strict triangle inequality: `1 2 3` is not a triangle.

The result goes to stdout. Errors go to stderr, and every invalid side gets its own
line.

## Exit codes

| Code | Meaning                                                                  |
|------|--------------------------------------------------------------------------|
| 0    | Valid triangle, classification printed                                   |
| 1    | A side is not a valid positive integer, or the sides don't form a triangle |
| 2    | Usage error: the argument count is not exactly 3                          |
