# OnTrack Submission Validator (SIT707 Pass Task)

Test-driven implementation of a submission validator for an OnTrack-style
learning platform. Built for SIT707 Pass Task (TDD + CI).

## Stack

- Java 17
- Maven
- JUnit 5 (Jupiter)
- GitHub Actions (CI)

## Run tests

```
mvn test
```

## Project layout

```
src/
  main/java/com/onTrack/      production code
  test/java/com/onTrack/      JUnit 5 tests
.github/workflows/ci.yml      build pipeline
REQUIREMENT.md                user story and validation rules
```

## TDD log

Each commit corresponds to one half of a red-green-refactor cycle. See
`git log --oneline` for the full sequence.
