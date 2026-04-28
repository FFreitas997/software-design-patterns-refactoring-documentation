# The Refactoring Process

## When to Refactor

### The Rule of Three
The first time you write something, just write it. The second time you do something similar, wince at the duplication but write it anyway. The third time you write something similar, refactor. The cost of the duplication is now certain, and the abstraction is clear enough to get right.

### When Adding a Feature
Before adding a new feature, refactor the surrounding code to make the feature easy to add. *"Make the change easy, then make the easy change"* — Kent Beck. If you have to fight the existing structure to add your feature, that is a signal to clean up first.

### When Fixing a Bug
When you find a bug, take a moment to understand why the bug was possible. If the code was cleaner, would this bug have been obvious? Often, a targeted refactoring that clarifies the surrounding logic reveals the root cause and makes similar bugs impossible.

### During Code Review
Code review is the perfect moment for collective refactoring. When a reviewer finds code hard to follow, the author can refactor before merge. This keeps the refactoring cost low (changes are small and fresh) and spreads clean code knowledge across the team.

### The Boy Scout Rule
Apply the Boy Scout Rule every time you open a file: *"Always leave the campground cleaner than you found it."* You don't need to fix everything — just make one small improvement each visit. Over time this compounds dramatically.

---

## When NOT to Refactor

### When You Don't Have Tests
Refactoring without a safety net is gambling. If the code has no automated tests, write the tests first. If writing the tests is impossible (because the code is too coupled), use careful characterization tests to pin down current behaviour before touching anything.

### When You're Close to a Deadline
Refactoring takes time upfront. If a deadline is imminent, ship the feature and schedule the refactoring as explicit technical debt. Document it — don't pretend the debt doesn't exist.

### When the Code Needs to Be Rewritten
If the code is so broken that it cannot be salvaged incrementally, a targeted rewrite of that module (with a clean interface and proper tests) may be more productive. This is rare — most code can be improved incrementally — but it does happen.

### When You Don't Understand the Code Yet
Understand before you change. Refactoring code you don't fully understand risks breaking subtle invariants. Read it, write tests that describe its behaviour, then refactor.

---

## How to Refactor Safely

### Step 1 — Establish a Test Safety Net
Before changing anything:
- Identify the automated tests that cover the code
- If they are missing or weak, write characterization tests that document current behaviour
- All tests must be green before you begin

### Step 2 — Make One Small Change at a Time
Each refactoring step should be:
- **Mechanical**: the transformation is predictable and reversible
- **Small**: changes a single structural element (rename, extract, move)
- **Behaviour-preserving**: the observable output of the system does not change

Never mix refactoring with feature changes in the same commit. Keep them strictly separate.

### Step 3 — Run Tests After Every Step
After each small transformation, run the full test suite. A red test immediately tells you which step broke something, so you can revert and try again. The longer you wait between test runs, the harder it is to find the cause of failure.

### Step 4 — Commit Frequently
Commit each green step as its own atomic commit:
```
refactor: extract calculateDiscount() from processOrder()
refactor: rename 'x' to 'basePrice' in OrderCalculator
refactor: introduce ORDER_THRESHOLD constant
```
This creates a clean history where each change is understandable in isolation.

### Step 5 — Review the Result
After the refactoring session, re-read the code as if you were seeing it for the first time. Ask:
- Is the intent immediately clear?
- Is there any remaining duplication?
- Are there any names that still feel wrong?
- Are all tests still green?

---

## Refactoring Workflow

```
┌─────────────────────────────────────┐
│  Identify smell or improvement area  │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│  Ensure tests exist and are green   │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│  Apply ONE refactoring technique    │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│  Run all tests                      │
│  Green? → Commit → repeat           │
│  Red?   → Revert → investigate      │
└─────────────────────────────────────┘
```

---

## Tools That Help

### IDE Refactoring Support
Modern IDEs automate the mechanical parts of refactoring, making transformations safe even on large codebases:

| IDE | Key Refactoring Actions |
|-----|------------------------|
| **IntelliJ IDEA** | Rename, Extract Method/Variable/Constant, Inline, Move, Change Signature, Pull Up/Push Down |
| **Eclipse** | Rename, Extract Method, Extract Variable, Inline, Move, Introduce Parameter |
| **VS Code** | Rename, Extract Method (via language extensions), Extract Variable |

Use `Ctrl+Alt+Shift+T` (IntelliJ) or `Alt+Shift+T` (Eclipse) to open the full refactoring menu on a selection.

### Static Analysis Tools
These tools identify code smells automatically, giving you a prioritised refactoring backlog:

- **SonarQube / SonarLint** — detects complexity, duplication, smells, and security issues; integrates into IDEs and CI pipelines
- **Checkstyle** — enforces coding conventions (naming, formatting, structure)
- **PMD** — detects unused variables, empty catch blocks, complex methods, and more
- **SpotBugs** — finds potential bugs: null dereferences, resource leaks, incorrect equals implementations

### Test Frameworks
Tests are the foundation without which safe refactoring is impossible:

- **JUnit 5** — the standard unit testing framework for Java
- **Mockito** — mocking framework that isolates units from their dependencies
- **AssertJ** — fluent assertion library that makes test failures descriptive
- **JaCoCo** — code coverage tool that reveals untested paths before you refactor them
