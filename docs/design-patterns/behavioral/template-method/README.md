# Template Method Pattern

## Intent

Define the **skeleton of an algorithm** in an operation, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithm's structure.

---

## Also Known As

- Template, Hook Method

---

## Motivation

Consider a data mining application that processes several types of documents (PDF, Word, CSV). For each document type, the workflow is similar:
1. Open the document
2. Extract raw data
3. Parse the raw data
4. Analyze the parsed data
5. Generate a report
6. Close the document

Steps 1, 3, 4, 5, 6 are the same for all document types; only step 2 (extraction) differs per format. Without Template Method, you'd either duplicate the common steps or put complex conditionals in the base class.

Template Method defines the fixed workflow in a base class method (`mine()`), and declares the varying step as an abstract method (`extractData()`) that subclasses must implement. The algorithm's structure stays in one place; the variable parts are cleanly isolated.

---

## Applicability

Use Template Method when:

- ✅ You want to implement the **invariant parts** of an algorithm once and leave the variable parts for subclasses to implement
- ✅ You want to control subclass extensions — subclasses can extend only specific steps
- ✅ You need to eliminate **code duplication**: common behavior is moved to the base class
- ✅ You see **invariant code sequences** (always in the same order) with varying sub-operations

---

## Structure

```
+---------------------+
|   AbstractClass     |
+---------------------+
| +templateMethod()   |  <- defines the skeleton; calls primitive operations
| #primitiveOp1()     |  <- abstract: subclass must implement
| #primitiveOp2()     |  <- abstract: subclass must implement
| #hook()             |  <- optional hook: subclass may override
+---------------------+
          ^
     +----+-----+
     |            |
+----+-----+ +----+-----+
|ConcreteA | |ConcreteB |
+----------+ +----------+
|#primitiveOp1()        |
|#primitiveOp2()        |
+----------+ +----------+
```

---

## Participants

- **AbstractClass** — Defines abstract primitive operations that concrete subclasses define; implements a template method defining the skeleton of an algorithm
- **ConcreteClass** — Implements the primitive operations to carry out subclass-specific steps of the algorithm

---

## Collaborations

ConcreteClass relies on AbstractClass to implement the invariant steps of the algorithm.

---

## Hook Methods

Hooks are optional operations with a default (often empty) implementation. They allow subclasses to "hook into" the algorithm at specific points without being required to:

```java
// In AbstractClass:
protected void hook() {} // optional hook — subclasses may override

// In templateMethod():
step1();
hook();  // only called if subclass overrides it
step2();
```

---

## Consequences

### Benefits
- ✅ **Code reuse** — Common algorithm steps live in one place
- ✅ **Hollywood Principle** — "Don't call us, we'll call you" — the base class calls subclass methods, not the other way around
- ✅ **Easy to extend** — Subclasses only need to implement the variable parts
- ✅ **Controls extension points** — Precisely defines which operations can be customized

### Drawbacks
- ❌ **Rigid structure** — The algorithm skeleton is fixed; hard to change the order of steps
- ❌ **Client must subclass** — Using the pattern requires inheritance
- ❌ **Liskov Substitution Principle** — Subclasses can suppress steps by providing no-op implementations, which may violate expectations

---

## Template Method vs. Strategy

| | Template Method | Strategy |
|---|---|---|
| Mechanism | Inheritance | Composition |
| Granularity | Hook into specific steps | Entire algorithm is replaced |
| Runtime change | No (fixed by subclass) | Yes (swap strategy object) |

---

## Related Patterns

- **Strategy** uses delegation rather than inheritance to vary the algorithm; more flexible but more objects
- **Factory Method** is often called by a template method
- **Hook** methods are often Template Methods with a default do-nothing implementation

---

## Example

See [TemplateMethod.java](TemplateMethod.java) for a complete data mining and report generation example.
