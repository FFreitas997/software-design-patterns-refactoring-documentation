# Abstract Factory Pattern

## Intent

Provide an interface for creating **families of related or dependent objects** without specifying their concrete classes.

---

## Also Known As

- Kit

---

## Motivation

Consider a UI toolkit that supports multiple look-and-feel standards — Windows, macOS, and Linux. Each standard defines a different appearance for widgets like `Button`, `Checkbox`, and `TextField`. The application needs to use a consistent family of widgets throughout, so mixing Windows buttons with macOS checkboxes would look wrong.

Abstract Factory solves this by defining a `UIFactory` interface with methods like `createButton()`, `createCheckbox()`, etc. Concrete factories (`WindowsFactory`, `MacOSFactory`) implement this interface to create the right widgets for each platform. The application only ever uses the `UIFactory` interface, so it's completely decoupled from the concrete widget classes.

---

## Applicability

Use Abstract Factory when:

- ✅ A system must be independent of how its products are created, composed, and represented
- ✅ A system needs to work with **multiple families** of products, and all families should be used together
- ✅ You want to enforce **constraints among products** (e.g., only mix products from the same family)
- ✅ You want to provide a class library of products, revealing only their interfaces

---

## Structure

```
+---------------------+       +---------------------+
|   AbstractFactory   |       |   AbstractProductA  |
+---------------------+       +---------------------+
| +createProductA()   |       | +operationA()       |
| +createProductB()   |       +---------------------+
+---------------------+                ^
         ^                   +---------+---------+
         |                   |                   |
+--------+-------+   +-------+-------+   +-------+-------+
|ConcreteFactory1|   |ConcreteFactory2|   |ProductA1     |
+----------------+   +----------------+   +---------------+
|+createProductA()|  |+createProductA()|  |+operationA() |
|+createProductB()|  |+createProductB()|  +---------------+
+----------------+   +----------------+
         |                    |
+--------+-------+   +--------+-------+
|  ProductA1     |   |  ProductA2     |
+----------------+   +----------------+
```

---

## Participants

- **AbstractFactory** — Declares creation operations for each distinct product type
- **ConcreteFactory** — Implements the creation operations to produce products of a specific family
- **AbstractProduct** — Declares an interface for a type of product
- **ConcreteProduct** — Defines a product produced by the corresponding concrete factory; implements the AbstractProduct interface
- **Client** — Uses only the interfaces declared by AbstractFactory and AbstractProduct

---

## Collaborations

- At runtime, a single `ConcreteFactory` object is created. It's typically created once — often using the Singleton pattern.
- The `Client` creates objects only through the `AbstractFactory` interface. It never references concrete product classes directly.
- The `ConcreteFactory` ensures that all products from one family work together correctly.

---

## Consequences

### Benefits
- ✅ **Isolates concrete classes** — Client code is isolated from implementation classes
- ✅ **Enforces product family consistency** — Makes it easy to exchange product families
- ✅ **Supports Open/Closed Principle** — Adding new product variants doesn't break existing code
- ✅ **Single Responsibility** — Product creation is in one place

### Drawbacks
- ❌ **Hard to add new product types** — Adding a new product to the factory requires changing `AbstractFactory` and all its subclasses
- ❌ **Many classes** — Each family requires a separate ConcreteFactory class

---

## Abstract Factory vs. Factory Method

| Aspect | Factory Method | Abstract Factory |
|---|---|---|
| Creates | One product | A family of products |
| Mechanism | Inheritance (subclass overrides method) | Composition (factory object injected) |
| Use case | Don't know the type beforehand | Must use consistent product families |

---

## Related Patterns

- **Factory Method** — Abstract Factory classes are often implemented using Factory Methods
- **Singleton** — Concrete Factory classes are often Singletons
- **Prototype** — Concrete Factory can also be implemented using Prototypes
- **Bridge** — Abstract Factory can be used with Bridge to hide which concrete classes implement the bridge

---

## Example

See [AbstractFactory.java](AbstractFactory.java) for a complete implementation with a cross-platform UI component factory.
