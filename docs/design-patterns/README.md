# Design Patterns

## What is a Design Pattern?

A **design pattern** is a general, reusable solution to a commonly occurring problem within a given context in software design. It is not a finished design that can be transformed directly into code; rather, it is a description or template for how to solve a problem that can be used in many different situations.

Design patterns were popularized by the "Gang of Four" (GoF) — Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides — in their landmark book *Design Patterns: Elements of Reusable Object-Oriented Software* (1994).

---

## What Does a Design Pattern Consist Of?

Each pattern is described using a consistent format to make it easy to learn and apply:

| Element | Description |
|---|---|
| **Name** | A short, memorable identifier for the pattern |
| **Intent** | A brief statement of what the pattern does and its rationale |
| **Also Known As** | Other names for the pattern, if any |
| **Motivation** | A scenario that illustrates the problem and how the pattern solves it |
| **Applicability** | Situations where the pattern can be applied |
| **Structure** | A graphical representation (class/object diagram) of the pattern |
| **Participants** | The classes and objects involved and their responsibilities |
| **Collaborations** | How participants collaborate to carry out responsibilities |
| **Consequences** | Trade-offs and results of using the pattern |
| **Implementation** | Hints, techniques, and pitfalls for implementing the pattern |
| **Sample Code** | Code fragments illustrating an implementation |
| **Known Uses** | Examples of the pattern found in real systems |
| **Related Patterns** | Other patterns that are closely related |

---

## Why Are Design Patterns Important?

1. **Proven Solutions** — Patterns represent the best practices evolved over time by experienced developers. They provide tried-and-tested solutions to recurring problems.

2. **Common Vocabulary** — Patterns give developers a shared language. Saying "use a Singleton here" communicates a rich set of design information instantly.

3. **Higher-Level Thinking** — Patterns let you think about design at a higher level of abstraction, focusing on architecture rather than implementation details.

4. **Improved Code Readability & Maintainability** — Code structured around known patterns is easier for others to understand and maintain.

5. **Faster Development** — Rather than solving every problem from scratch, developers can apply known patterns and focus on the unique aspects of their application.

6. **Facilitate Refactoring** — Recognizing patterns in existing code helps identify opportunities for improvement and refactoring.

---

## The Three Categories of Design Patterns

The Gang of Four organized the 23 classic patterns into three categories based on their purpose:

### 🏗️ [Creational Patterns](creational/README.md)

Creational patterns deal with **object creation mechanisms**, aiming to create objects in a manner suitable to the situation. They abstract the instantiation process and help make a system independent of how its objects are created, composed, and represented.

| Pattern | Purpose |
|---|---|
| [Singleton](creational/singleton/README.md) | Ensure a class has only one instance |
| [Factory Method](creational/factory-method/README.md) | Let subclasses decide which class to instantiate |
| [Abstract Factory](creational/abstract-factory/README.md) | Create families of related objects |
| [Builder](creational/builder/README.md) | Separate construction from representation |
| [Prototype](creational/prototype/README.md) | Create objects by cloning an existing object |

### 🔗 [Structural Patterns](structural/README.md)

Structural patterns deal with **object composition**, creating relationships between objects to form larger structures. They help ensure that when parts change, the entire structure doesn't need to.

| Pattern | Purpose |
|---|---|
| [Adapter](structural/adapter/README.md) | Convert an interface into another interface |
| [Bridge](structural/bridge/README.md) | Decouple abstraction from implementation |
| [Composite](structural/composite/README.md) | Compose objects into tree structures |
| [Decorator](structural/decorator/README.md) | Add responsibilities to objects dynamically |
| [Facade](structural/facade/README.md) | Provide a simplified interface to a subsystem |
| [Flyweight](structural/flyweight/README.md) | Share fine-grained objects efficiently |
| [Proxy](structural/proxy/README.md) | Provide a surrogate or placeholder for another object |

### 🔄 [Behavioral Patterns](behavioral/README.md)

Behavioral patterns deal with **communication and responsibility** between objects. They characterize the ways in which classes or objects interact and distribute responsibility.

| Pattern | Purpose |
|---|---|
| [Chain of Responsibility](behavioral/chain-of-responsibility/README.md) | Pass requests along a chain of handlers |
| [Command](behavioral/command/README.md) | Encapsulate a request as an object |
| [Iterator](behavioral/iterator/README.md) | Access elements of a collection sequentially |
| [Mediator](behavioral/mediator/README.md) | Define simplified communication between classes |
| [Memento](behavioral/memento/README.md) | Capture and restore an object's state |
| [Observer](behavioral/observer/README.md) | Notify dependents when an object changes |
| [State](behavioral/state/README.md) | Alter an object's behavior when its state changes |
| [Strategy](behavioral/strategy/README.md) | Define a family of interchangeable algorithms |
| [Template Method](behavioral/template-method/README.md) | Define a skeleton of an algorithm in a base class |
| [Visitor](behavioral/visitor/README.md) | Separate an algorithm from an object structure |

---

## How to Use This Documentation

Each pattern folder contains:
- A `README.md` with full documentation (Intent, Motivation, Applicability, Structure, Consequences)
- A `.java` file with a complete, compilable example

Start with the category that matches your current design challenge, then dive into the specific pattern.

> 💡 **Tip:** Don't try to memorize all patterns at once. Learn them as you encounter problems they solve — that's the most effective way to internalize them.
