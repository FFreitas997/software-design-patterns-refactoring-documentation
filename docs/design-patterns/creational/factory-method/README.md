# Factory Method Pattern

## Intent

Define an interface for creating an object, but **let subclasses decide which class to instantiate**. Factory Method lets a class defer instantiation to subclasses.

---

## Also Known As

- Virtual Constructor

---

## Motivation

Consider a document editor framework. The framework defines an abstract `Application` class and an abstract `Document` class. `Application` is responsible for managing documents — opening, creating, saving. But it can't know in advance what kind of `Document` to create (Word, PDF, Spreadsheet, etc.) — that's the job of the concrete application subclass.

The Factory Method pattern solves this by:
- Defining a `createDocument()` method in `Application` (the **factory method**)
- Letting each concrete subclass (`WordApp`, `SpreadsheetApp`) override it to return the appropriate `Document` type

This way the framework (`Application`) relies only on the `Document` interface, while subclasses determine which concrete product to instantiate.

---

## Applicability

Use Factory Method when:

- ✅ A class **can't anticipate** the class of objects it must create
- ✅ A class wants its **subclasses to specify** the objects it creates
- ✅ Classes **delegate responsibility** to one of several helper subclasses, and you want to localize the knowledge of which helper subclass is the delegate
- ✅ You want to provide users of your library/framework a way to **extend its internal components**

---

## Structure

```
+-------------------+          +-------------------+
|    Creator        |          |    Product        |
+-------------------+          +-------------------+
| +factoryMethod()  |--------> | +operation()      |
| +someOperation()  |          +-------------------+
+-------------------+                    ^
         ^                               |
         |                     +---------+---------+
+-------------------+          | ConcreteProduct   |
| ConcreteCreator   |          +-------------------+
+-------------------+          | +operation()      |
| +factoryMethod()  |--------> +-------------------+
+-------------------+
```

---

## Participants

- **Product** (`Document`) — Defines the interface for objects the factory method creates
- **ConcreteProduct** (`WordDocument`, `PDFDocument`) — Implements the Product interface
- **Creator** (`Application`) — Declares the factory method, which returns a Product object. May also define a default implementation.
- **ConcreteCreator** (`WordApp`, `PDFApp`) — Overrides the factory method to return an instance of the appropriate ConcreteProduct

---

## Collaborations

`Creator` relies on its subclasses to define the factory method so that it returns an instance of the appropriate `ConcreteProduct`. The `Creator`'s `someOperation()` calls the factory method to obtain a `Product` — it never references a `ConcreteProduct` directly.

---

## Consequences

### Benefits
- ✅ **Open/Closed Principle** — You can introduce new product types without breaking existing creator code
- ✅ **Single Responsibility Principle** — Product creation code is in one place
- ✅ **Loose coupling** — Creator is decoupled from concrete products
- ✅ **Hooks for subclasses** — Subclasses can override the factory method to provide different default products

### Drawbacks
- ❌ May introduce many subclasses to just change the class of the product
- ❌ The parallel class hierarchy (Creator/Product) can become complex

---

## Implementation Notes

- The factory method can provide a **default implementation** that creates a sensible default ConcreteProduct, which subclasses may optionally override
- **Parameterized factory methods** take a parameter identifying the kind of object to create, allowing one factory method to create multiple kinds of products:
  ```java
  public Product createProduct(String type) {
      if ("A".equals(type)) return new ConcreteProductA();
      if ("B".equals(type)) return new ConcreteProductB();
      throw new IllegalArgumentException("Unknown type: " + type);
  }
  ```

---

## Related Patterns

- **Abstract Factory** is often implemented using Factory Methods
- **Template Method** calls Factory Methods — the factory method is a type of template method hook
- **Prototype** doesn't require subclassing Creator but requires initialization; Factory Method requires subclassing but not initialization

---

## Example

See [FactoryMethod.java](FactoryMethod.java) for a complete implementation with a logistics application that creates different transport vehicles.
