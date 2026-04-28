# Clean Code

## What is Clean Code?

**Clean code** is code that is easy to read, easy to understand, easy to change, and easy to test. It expresses its intent clearly, has no surprises, and does exactly what you expect it to do. Clean code is not about being clever — it is about being *clear*.

Different thought leaders define it in slightly different ways:

> *"Clean code is simple and direct. Clean code reads like well-written prose."*
> — Grady Booch

> *"Clean code always looks like it was written by someone who cares."*
> — Robert C. Martin (Uncle Bob)

> *"Clean code can be read, and enhanced by a developer other than its original author."*
> — Dave Thomas

---

## Core Principles

### KISS — Keep It Simple, Stupid
The simplest solution that correctly solves the problem is almost always the best. Complexity is not a sign of sophistication — it is a sign of unclear thinking. Every time you introduce an abstraction, a design pattern, or a clever algorithm, ask: *"Is this actually necessary?"*

- Prefer flat code over deeply nested code
- Prefer simple data structures over complex hierarchies when the problem is simple
- Write the obvious solution first; optimize only when profiling proves you must

### DRY — Don't Repeat Yourself
*"Every piece of knowledge must have a single, unambiguous, authoritative representation within a system."* — The Pragmatic Programmer

Duplication is the root of many maintenance problems. When logic exists in two places, a change to the requirement requires finding and updating both — and missing one creates a bug. Extract duplicated logic into a single method, class, or module.

### SOLID Principles
A set of five object-oriented design principles that, followed together, make systems flexible, maintainable, and testable:

| Letter | Principle | Meaning |
|--------|-----------|---------|
| **S** | Single Responsibility | A class should have one, and only one, reason to change |
| **O** | Open/Closed | Open for extension, closed for modification |
| **L** | Liskov Substitution | Subtypes must be substitutable for their base types |
| **I** | Interface Segregation | Clients should not be forced to depend on interfaces they don't use |
| **D** | Dependency Inversion | Depend on abstractions, not concretions |

### YAGNI — You Aren't Gonna Need It
Do not add functionality until it is actually required. Speculative features add complexity, require maintenance, and often end up unused. Build what you need today; refactor when tomorrow's requirements are known.

---

## Naming Conventions

Good names are the single most impactful form of documentation:

- **Classes**: Nouns that describe what the object *is* — `OrderCalculator`, `CustomerRepository`, `InvoiceService`
- **Methods**: Verbs that describe what the method *does* — `calculateTotal()`, `findByEmail()`, `applyDiscount()`
- **Variables**: Nouns that describe what the value *represents* — `discountRate`, `customerType`, `taxAmount`
- **Booleans**: Predicates that read naturally — `isActive`, `hasPermission`, `shouldApplyTax`
- **Constants**: UPPER_SNAKE_CASE that names the concept — `MAX_RETRY_ATTEMPTS`, `TAX_RATE`, `PREMIUM_DISCOUNT`

**Rules of thumb:**
- If you need a comment to explain what a name means, the name is wrong
- A longer, clearer name is almost always better than a short, cryptic abbreviation
- Avoid generic names: `Manager`, `Helper`, `Processor`, `Data`, `Info`

---

## Function / Method Size

Clean methods:
- **Do one thing** — if you can describe a method with "and", split it
- **Are short** — typically 5–15 lines; rarely more than 20–30
- **Have descriptive names** — the name should make the body nearly predictable
- **Have few parameters** — ideally 0–2; more than 3 is a signal to introduce a parameter object
- **Have no side effects** — a method named `getX()` should not modify state

---

## Comments Philosophy

> *"The best comment is a good name."*

Comments should explain **why** something is done, not **what** is done (the code itself shows what). Signs you need a comment:

- A non-obvious algorithm or business rule
- A workaround for an external system's quirk
- A performance optimization that looks unidiomatic

Signs your comment is dirty:
- It restates what the code already says (`i++ // increment i`)
- It is outdated and no longer matches the code
- It is used to apologize for bad code (`// this is a mess, TODO fix someday`)

---

## How Clean Code Differs from Dirty Code

| Aspect | Dirty Code | Clean Code |
|--------|-----------|------------|
| Naming | `a`, `tmp`, `doStuff()` | `discountRate`, `calculateOrderTotal()` |
| Method length | 100+ lines | 5–20 lines |
| Responsibilities | One class does everything | Single Responsibility Principle |
| Duplication | Copy-paste throughout | DRY — single authoritative source |
| Magic values | `if (x > 500)` | `if (points > PREMIUM_THRESHOLD)` |
| Nesting | 5+ levels deep | Guard clauses flatten to 1–2 levels |
| Testability | Impossible without mocking everything | Easily unit-testable in isolation |
| Comments | Explain what (redundant) or apologize | Explain why (purposeful) |

---

## Example

See [CleanCodeExample.java](./CleanCodeExample.java) for a refactored version of the dirty code in [DirtyCodeExample.java](../dirty-code/DirtyCodeExample.java).
