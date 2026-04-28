# Software Design Patterns & Refactoring Documentation

A comprehensive reference covering **software design patterns** (Creational, Structural, Behavioral) and **refactoring** (code smells, techniques, clean code), with Java examples throughout.

---

## 📚 Contents

### 🔷 [Design Patterns](docs/design-patterns/README.md)

> Design patterns are reusable solutions to commonly occurring problems in software design.

| Category | Description | Patterns |
|---|---|---|
| [Creational](docs/design-patterns/creational/README.md) | Object creation mechanisms | Singleton, Factory Method, Abstract Factory, Builder, Prototype |
| [Structural](docs/design-patterns/structural/README.md) | Assembling objects into larger structures | Adapter, Bridge, Composite, Decorator, Facade, Flyweight, Proxy |
| [Behavioral](docs/design-patterns/behavioral/README.md) | Algorithms and responsibilities between objects | Chain of Responsibility, Command, Iterator, Mediator, Memento, Observer, State, Strategy, Template Method, Visitor |

#### Creational Patterns
- [Singleton](docs/design-patterns/creational/singleton/README.md) — Ensure a class has only one instance
- [Factory Method](docs/design-patterns/creational/factory-method/README.md) — Define an interface for creating objects, let subclasses decide which class to instantiate
- [Abstract Factory](docs/design-patterns/creational/abstract-factory/README.md) — Produce families of related objects without specifying concrete classes
- [Builder](docs/design-patterns/creational/builder/README.md) — Construct complex objects step by step
- [Prototype](docs/design-patterns/creational/prototype/README.md) — Copy existing objects without coupling to their classes

#### Structural Patterns
- [Adapter](docs/design-patterns/structural/adapter/README.md) — Allow incompatible interfaces to work together
- [Bridge](docs/design-patterns/structural/bridge/README.md) — Separate an abstraction from its implementation
- [Composite](docs/design-patterns/structural/composite/README.md) — Compose objects into tree structures
- [Decorator](docs/design-patterns/structural/decorator/README.md) — Attach new behaviors to objects by wrapping them
- [Facade](docs/design-patterns/structural/facade/README.md) — Provide a simplified interface to a complex subsystem
- [Flyweight](docs/design-patterns/structural/flyweight/README.md) — Share common state to support large numbers of fine-grained objects
- [Proxy](docs/design-patterns/structural/proxy/README.md) — Provide a surrogate or placeholder for another object

#### Behavioral Patterns
- [Chain of Responsibility](docs/design-patterns/behavioral/chain-of-responsibility/README.md) — Pass requests along a chain of handlers
- [Command](docs/design-patterns/behavioral/command/README.md) — Turn requests into stand-alone objects
- [Iterator](docs/design-patterns/behavioral/iterator/README.md) — Traverse elements of a collection without exposing its structure
- [Mediator](docs/design-patterns/behavioral/mediator/README.md) — Reduce chaotic dependencies between objects
- [Memento](docs/design-patterns/behavioral/memento/README.md) — Save and restore an object's previous state
- [Observer](docs/design-patterns/behavioral/observer/README.md) — Notify multiple objects about events
- [State](docs/design-patterns/behavioral/state/README.md) — Alter an object's behavior when its internal state changes
- [Strategy](docs/design-patterns/behavioral/strategy/README.md) — Define a family of algorithms and make them interchangeable
- [Template Method](docs/design-patterns/behavioral/template-method/README.md) — Define the skeleton of an algorithm, deferring steps to subclasses
- [Visitor](docs/design-patterns/behavioral/visitor/README.md) — Separate algorithms from the objects they operate on

---

### 🔶 [Refactoring](docs/refactoring/README.md)

> Refactoring is the process of restructuring existing code without changing its external behavior.

| Topic | Description |
|---|---|
| [Dirty Code](docs/refactoring/dirty-code/README.md) | Signs, consequences and examples of poorly written code |
| [Clean Code](docs/refactoring/clean-code/README.md) | Principles and practices for writing maintainable code |
| [Refactoring Process](docs/refactoring/refactoring-process/README.md) | When, why, and how to refactor safely |
| [Code Smells](docs/refactoring/code-smells/README.md) | Recognizing patterns that signal the need for refactoring |
| [Refactoring Techniques](docs/refactoring/refactoring-techniques/README.md) | Catalog of proven transformations |

#### Code Smells
- [Bloaters](docs/refactoring/code-smells/bloaters/README.md) — Long Method, Large Class, Primitive Obsession, Long Parameter List, Data Clumps
- [Object-Orientation Abusers](docs/refactoring/code-smells/object-orientation-abusers/README.md) — Switch Statements, Temporary Field, Refused Bequest, Alternative Classes
- [Change Preventers](docs/refactoring/code-smells/change-preventers/README.md) — Divergent Change, Shotgun Surgery, Parallel Inheritance Hierarchies
- [Dispensables](docs/refactoring/code-smells/dispensables/README.md) — Comments, Duplicate Code, Lazy Class, Data Class, Dead Code, Speculative Generality
- [Couplers](docs/refactoring/code-smells/couplers/README.md) — Feature Envy, Inappropriate Intimacy, Message Chains, Middle Man

#### Refactoring Techniques
- [Composing Methods](docs/refactoring/refactoring-techniques/composing-methods/README.md) — Extract Method, Inline Method, Replace Temp with Query, and more
- [Moving Features between Objects](docs/refactoring/refactoring-techniques/moving-features/README.md) — Move Method, Extract Class, Hide Delegate, and more
- [Organizing Data](docs/refactoring/refactoring-techniques/organizing-data/README.md) — Replace Magic Number, Encapsulate Field, Replace Type Code, and more
- [Simplifying Conditional Expressions](docs/refactoring/refactoring-techniques/simplifying-conditional-expressions/README.md) — Decompose Conditional, Replace Nested Conditional with Guard Clauses, Introduce Null Object, and more
- [Simplifying Method Calls](docs/refactoring/refactoring-techniques/simplifying-method-calls/README.md) — Rename Method, Introduce Parameter Object, Replace Constructor with Factory Method, and more
- [Dealing with Generalization](docs/refactoring/refactoring-techniques/dealing-with-generalization/README.md) — Pull Up/Push Down Method, Extract Superclass/Interface, Replace Inheritance with Delegation, and more

---

## 🛠️ Java Examples

Every pattern and refactoring technique includes a complete, compilable Java example demonstrating the concept in practice. Examples are located alongside their respective `README.md` files.

To compile and run any example (requires JDK 8+):

```bash
# Example: run the Strategy pattern demo
cd docs/design-patterns/behavioral/strategy
javac Strategy.java
java Strategy

# Example: run the Bloaters code smell demo
cd docs/refactoring/code-smells/bloaters
javac BloatersExample.java
java BloatersExample
```

---

## 📖 References

- *Design Patterns: Elements of Reusable Object-Oriented Software* — Gamma, Helm, Johnson, Vlissides (Gang of Four)
- *Refactoring: Improving the Design of Existing Code* — Martin Fowler
- *Clean Code: A Handbook of Agile Software Craftsmanship* — Robert C. Martin
- [Refactoring Guru](https://refactoring.guru) — Patterns & Refactoring catalog
