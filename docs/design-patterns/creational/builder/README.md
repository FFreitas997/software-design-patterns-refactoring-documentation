# Builder Pattern

## Intent

Separate the **construction** of a complex object from its **representation**, allowing the same construction process to create different representations.

---

## Also Known As

- Fluent Builder

---

## Motivation

Consider building a complex object like a `House`. A house might have walls, a roof, windows, doors, a garage, a garden, and a swimming pool. The constructor of such a class would have an enormous number of parameters — many of which would be optional.

One solution is telescoping constructors (multiple overloaded constructors), but they become unreadable very quickly. Another is a setter-based approach, but that leaves the object in an inconsistent state during construction.

The Builder pattern addresses this by:
1. Extracting the construction steps into a separate `Builder` object
2. Making each step a fluent method call that returns the builder itself
3. Providing a `build()` method that returns the fully constructed object
4. Optionally using a `Director` to define common construction sequences

---

## Applicability

Use Builder when:

- ✅ The algorithm for creating a complex object should be independent of the parts that make up the object and how they're assembled
- ✅ The construction process must allow different representations for the object being constructed
- ✅ You want to construct a complex object step by step — specifically useful when many parameters are optional
- ✅ You need to build an **immutable** object but want a readable way to set its properties

---

## Structure

```
+-------------+         +-------------------+         +---------------+
|  Director   |-------->|  Builder          |         |   Product     |
+-------------+         +-------------------+         +---------------+
| +construct()|         | +buildPartA()     |         | parts: List   |
+-------------+         | +buildPartB()     |         +---------------+
                         | +getResult()     |
                         +-------------------+
                                  ^
                                  |
                        +---------+---------+
                        | ConcreteBuilder   |
                        +-------------------+
                        | +buildPartA()     |
                        | +buildPartB()     |
                        | +getResult()      |
                        +-------------------+
```

---

## Participants

- **Builder** — Specifies an abstract interface for creating parts of a Product object
- **ConcreteBuilder** — Constructs and assembles parts of the product by implementing the Builder interface; defines and keeps track of the representation it creates; provides an interface for retrieving the product
- **Director** — Constructs an object using the Builder interface; defines the order in which construction steps are called
- **Product** — Represents the complex object under construction

---

## Collaborations

1. Client creates the Director object and configures it with the desired Builder
2. Director notifies the Builder whenever a part of the product should be built
3. Builder handles requests from the Director and adds parts to the product
4. Client retrieves the product from the builder

---

## Consequences

### Benefits
- ✅ **Varies product's internal representation** — Because the product is constructed through an abstract interface, changing the product's representation is easy
- ✅ **Isolates construction and representation code** — Improves modularity
- ✅ **Finer control over the construction process** — Unlike Creational patterns that construct in one shot, Builder constructs step by step
- ✅ **Readable, fluent API** — Chaining method calls reads like natural language: `new HouseBuilder().withRooms(3).withGarage(true).build()`
- ✅ **Immutable objects** — Builder is the recommended way to create complex immutable objects

### Drawbacks
- ❌ **Duplicated code** — Each concrete builder must repeat the same building interface
- ❌ **Requires a separate Builder class** — More classes to maintain
- ❌ **Builder must be created before the product** — Slightly more ceremony than direct construction

---

## Builder vs. Other Creational Patterns

| Pattern | Focus | Returns |
|---|---|---|
| Abstract Factory | Creates families of objects | Several related objects |
| Builder | Constructs one complex object step by step | One object after all steps |
| Factory Method | Delegates creation to subclasses | One object |

---

## Related Patterns

- **Abstract Factory** and Builder are similar but Abstract Factory emphasizes families of product objects; Builder focuses on constructing a complex object step by step
- **Composite** objects are often built using Builder

---

## Example

See [Builder.java](Builder.java) for a complete implementation building a customizable `House` and `Pizza`.
