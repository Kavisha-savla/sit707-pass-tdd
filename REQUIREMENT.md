# Requirement

## User story

> As a student using OnTrack, when I upload a task submission I want the system
> to immediately tell me whether my submission is acceptable, so I do not waste
> time waiting for a tutor only to be told my file was the wrong type or my
> student ID was wrong.

## Why this function is needed

Tutors currently spend time rejecting submissions for trivial reasons: wrong
file type, missing student ID, empty content, oversized files, or task IDs that
do not match the unit's task list. A validator that runs at submission time
catches these failures up front, gives the student a clear list of what to fix,
and stops malformed submissions from clogging the tutor's review queue.

## How it should behave

`SubmissionValidator.validate(Submission)` returns a `ValidationResult` with:

- `isValid`: true if every check passed, false otherwise.
- `errors`: a list of human-readable error messages, one per failed check.

Validation rules:

| Field           | Rule                                                                |
|-----------------|---------------------------------------------------------------------|
| submission      | must not be null                                                    |
| studentId       | non-null, exactly 9 digits (Deakin format)                          |
| taskId          | non-null, matches pattern `^[1-9]\\.[1-9][PCDH]$` (e.g. `1.1P`)     |
| content         | non-null, non-blank, length between 1 and 100000 characters         |
| fileType        | must be one of `PDF`, `DOC`, `DOCX`, `ZIP`, `TXT`                   |
| submittedAt     | non-null, not in the future (>5 min skew rejected)                  |

The validator collects every error in a single pass and returns them all,
rather than failing fast on the first problem, so the student sees the full
list and can fix everything at once.
