# Triangle Classifier: Specification

## Purpose

A command-line tool that takes three side lengths and reports whether they form an
**Equilateral**, **Isosceles** or **Scalene** triangle.

## Invocation

```
java -jar target/triangle-classifier.jar <a> <b> <c>
```

- Exactly three arguments. Each one is a side length.
- No flags or options.
- Input comes only from the arguments. Stdin is not read.

## Side length format

Each side is expected to be either a whole number written in plain digits, such as `5`
or `120`, or other text, such as `abc`. Numbers are assumed never to have surrounding
whitespace. Leading zeros are allowed and do not change the value: `05` is read as `5`.

A side is valid only if it matches all of these rules:

1. **Syntax:** the value must match `-?[0-9]+`. Anything else, such as `abc` or `4.0`,
   is invalid.
2. **Positivity:** the value must be greater than zero. `0` and negative numbers fail
   this rule.
3. **Size:** there is no upper limit. Integers of any size are accepted, and every
   comparison and sum is exact, with no overflow.

## Triangle validity

The three sides form a triangle only if the **strict** triangle inequality holds for
every pair:

- a + b > c
- a + c > b
- b + c > a

Degenerate inputs, where two sides add up to exactly the third (e.g. `1 2 3`), are
**not** triangles.

This check runs only when all three sides are valid.

## Classification

| Condition                        | Output        |
|----------------------------------|---------------|
| All three sides equal            | `Equilateral` |
| Exactly two sides equal          | `Isosceles`   |
| No two sides equal               | `Scalene`     |

An equilateral triangle is reported only as `Equilateral`, never as `Isosceles`. The
order of the arguments does not affect the result.

## Output

**On success:** print the classification word followed by a newline to **stdout**,
and nothing else. Exit code `0`. Nothing is written to stderr.

**On error:** write the message(s) to **stderr** and nothing to stdout.

## Errors and exit codes

| Exit code | Category         | When                                              |
|-----------|------------------|---------------------------------------------------|
| 0         | Success          | Valid triangle, classified                        |
| 1         | Invalid triangle | At least one side is not a valid positive integer, or all sides are valid but break the triangle inequality |
| 2         | Usage error      | The argument count is not exactly 3               |

Checks run in this order: argument count, then every side, then the triangle
inequality. Each stage runs only if the one before it passed.

### Usage error (exit 2)

```
Usage: triangle-classifier <a> <b> <c>
```

### Invalid sides (exit 1)

- **Every** invalid side is reported, one line each, in argument order (side 1, 2, 3).
- Each message gives the side's argument value, in double quotes.
- There are two distinct messages:

| Problem                                   | Message                                            |
|-------------------------------------------|----------------------------------------------------|
| Fails the format rules                    | `Error: "<value>" is not a valid integer` |
| Valid format but zero or negative         | `Error: "<value>" must be positive`      |

Both messages are part of the same category, so the exit code is `1` whatever mix of
problems occurs.

### Not a triangle (exit 1)

```
Error: sides <a>, <b>, <c> do not form a triangle
```

The sides appear in argument order.

## Examples

| Command args        | stdout        | stderr                                                  | Exit |
|---------------------|---------------|---------------------------------------------------------|------|
| `3 3 3`             | `Equilateral` |                                                         | 0    |
| `3 3 5`             | `Isosceles`   |                                                         | 0    |
| `5 3 3`             | `Isosceles`   |                                                         | 0    |
| `3 4 5`             | `Scalene`     |                                                         | 0    |
| `99999999999999999999 99999999999999999999 1` | `Isosceles` |                                 | 0    |
| `3 4`               |               | `Usage: triangle-classifier <a> <b> <c>`                | 2    |
| `3 4 5 6`           |               | `Usage: triangle-classifier <a> <b> <c>`                | 2    |
| `abc 4 5`           |               | `Error: "abc" is not a valid integer`          | 1    |
| `3 4.0 5`           |               | `Error: "4.0" is not a valid integer`          | 1    |
| `0 4 5`             |               | `Error: "0" must be positive`                  | 1    |
| `abc -1 0`          |               | three lines: `"abc"` not a valid integer; `"-1"` must be positive; `"0"` must be positive | 1 |
| `1 2 3`             |               | `Error: sides 1, 2, 3 do not form a triangle`           | 1    |
| `1 1 10`            |               | `Error: sides 1, 1, 10 do not form a triangle`          | 1    |
