# Visitor Pattern

## Intent

Represent an **operation to be performed on elements of an object structure**. Visitor lets you define a new operation without changing the classes of the elements on which it operates.

---

## Also Known As

- Double Dispatch

---

## Motivation

Consider a compiler that represents programs as abstract syntax trees (ASTs). You might need many operations on these nodes: type checking, code generation, pretty printing, etc. If you add these operations directly to the AST node classes, each new operation changes all node classes. With Visitor, you extract each operation into its own Visitor class, and all AST nodes remain unchanged.

The same logic applies to shape libraries: shapes like `Circle`, `Rectangle`, and `Triangle` may need area calculation, perimeter calculation, XML export, JSON export — all without modifying the shape classes themselves.

---

## Applicability

Use Visitor when:

- ✅ An object structure contains many classes of objects with differing interfaces, and you want to perform operations that depend on their concrete classes
- ✅ Many distinct and **unrelated operations** need to be performed on objects in an object structure, and you want to avoid "polluting" their classes with these operations
- ✅ The classes defining the object structure rarely change, but you often want to **define new operations** over the structure
- ✅ You want to gather related behavior into a single Visitor class rather than spreading it across many classes

---

## Structure

```
  «interface»           «interface»
  Visitor               Element
  +visitA(ConcreteA)    +accept(Visitor)
  +visitB(ConcreteB)          ^
       ^                 +----+------+
  +----+------+          |           |
  |           |     ConcreteA   ConcreteB
Visitor1  Visitor2  +accept(v):    +accept(v):
                      v.visitA(this)  v.visitB(this)
```

---

## Participants

- **Visitor** — Declares a `visit` method for each concrete element class
- **ConcreteVisitor** — Implements each operation declared by Visitor; maintains local state accumulated as elements are visited
- **Element** — Defines an `accept` method that takes a visitor as argument
- **ConcreteElement** — Implements `accept` by calling the visitor method that corresponds to its class
- **ObjectStructure** — Can enumerate its elements; provides a high-level interface to allow visitors to visit its elements

---

## Collaborations

1. A client creates a ConcreteVisitor and traverses the object structure
2. When an element is visited, it calls the Visitor method that corresponds to its class
3. This is **double dispatch** — the operation to execute depends on both the visitor type *and* the element type

---

## Double Dispatch Explained

In Java, method dispatch is based on the **static type** of the object. Without Visitor, a `calculate(Shape s)` method can't easily dispatch to different logic for `Circle` vs `Rectangle` without `instanceof`.

With Visitor:
```java
// Element calls the RIGHT visitor method based on its own concrete type
// Visitor runs the RIGHT implementation based on its own concrete type
shape.accept(visitor);   // dispatch #1: calls Circle.accept() or Rectangle.accept()
// inside accept: visitor.visitCircle(this)   // dispatch #2: calls the correct visit method
```

---

## Consequences

### Benefits
- ✅ **Open/Closed Principle** — Add new operations easily by creating new Visitor classes
- ✅ **Single Responsibility** — Related operations are grouped in one Visitor class
- ✅ **Accumulate state** — Visitors can accumulate state as they visit elements (e.g., building a report)
- ✅ **Cross-class operations** — A visitor can work across many unrelated classes

### Drawbacks
- ❌ **Adding new element types is hard** — Adding a new ConcreteElement requires updating all Visitor interfaces
- ❌ **Breaks encapsulation** — Visitors often need access to element internals; elements may expose internal state
- ❌ **Complexity** — Double dispatch is harder to understand than normal method calls

---

## When NOT to Use Visitor

- When the element class hierarchy changes frequently (adding new element types is costly)
- When the operation is closely tied to the element (better put it in the element class)
- When only one or two operations exist (simpler to just add methods to elements)

---

## Related Patterns

- **Composite** — Visitors can be used to apply an operation to all elements in a Composite tree
- **Interpreter** — Visitor can be used to interpret an abstract syntax tree
- **Iterator** — Can be used to traverse the object structure that a Visitor operates on

---

## Example

See [Visitor.java](Visitor.java) for a complete shape calculation and export example demonstrating double dispatch.
