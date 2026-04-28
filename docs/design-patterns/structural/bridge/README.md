# Bridge Pattern

## Intent

**Decouple an abstraction from its implementation** so that the two can vary independently.

---

## Also Known As

- Handle/Body

---

## Motivation

When using inheritance to extend an abstraction in multiple dimensions, you can end up with a class explosion. Consider a `Shape` class with subclasses `Circle` and `Square`. You also want shapes in different colors (Red, Blue). Using only inheritance, you need `RedCircle`, `BlueCircle`, `RedSquare`, `BlueSquare` — four classes for two dimensions.

With Bridge, you separate the *shape* (abstraction) from the *rendering method* (implementation) into two separate hierarchies. A `Shape` holds a reference to a `Renderer`, and both can be extended independently. You need only `Circle` + `Square` + `RasterRenderer` + `VectorRenderer` = 4 classes for unlimited combinations.

---

## Applicability

Use Bridge when:

- ✅ You want to avoid a permanent binding between an abstraction and its implementation
- ✅ Both the abstractions and their implementations should be extensible via subclassing
- ✅ Changes in the implementation of an abstraction should have no impact on clients
- ✅ You have a **proliferating class hierarchy** that indicates you need to split a class into two dimensions
- ✅ You want to share an implementation among multiple objects (using reference counting, for example)

---

## Structure

```
+---------------------+          +---------------------+
|     Abstraction     |◆-------->|   Implementor       |
+---------------------+          +---------------------+
| - impl: Implementor |          | +operationImpl()    |
| +operation()        |          +---------------------+
+---------------------+                    ^
         ^                                 |
         |                      +----------+----------+
+--------+-------+              |                     |
|RefinedAbstract |    +----------+-------+   +--------+---------+
+----------------+    |ConcreteImplA     |   |ConcreteImplB     |
| +operation()   |    +------------------+   +------------------+
+----------------+    |+operationImpl()  |   |+operationImpl()  |
                      +------------------+   +------------------+
```

---

## Participants

- **Abstraction** (`Shape`) — Defines the abstraction's interface; maintains a reference to an object of type Implementor
- **RefinedAbstraction** (`Circle`, `Square`) — Extends the interface defined by Abstraction
- **Implementor** (`Renderer`) — Defines the interface for implementation classes (doesn't have to correspond exactly to Abstraction's interface)
- **ConcreteImplementor** (`RasterRenderer`, `VectorRenderer`) — Implements the Implementor interface and defines its concrete implementation

---

## Collaborations

Abstraction forwards client requests to its Implementor object. The Abstraction and Implementor decide how to collaborate at runtime — the specific implementation object is set either at construction or can be swapped later.

---

## Consequences

### Benefits
- ✅ **Decoupling interface and implementation** — Implementation can be selected or switched at runtime
- ✅ **Improved extensibility** — Abstraction and Implementor hierarchies can be extended independently
- ✅ **Hiding implementation details** — Clients only see the Abstraction
- ✅ **Single Responsibility** — Abstract logic and platform-specific logic are in separate classes

### Drawbacks
- ❌ **More complexity** — Introduces additional indirection; can be overkill for simple cases

---

## Bridge vs. Adapter

| | Adapter | Bridge |
|---|---|---|
| Intent | Makes two *existing* things work together | Designed *upfront* to let two hierarchies vary independently |
| When applied | After the fact (retrofit) | Before the fact (design-time decision) |
| Changes interface? | Yes | No |

---

## Related Patterns

- **Abstract Factory** can create and configure a particular Bridge
- **Adapter** is geared toward making unrelated classes work together; Bridge is used up front to let abstractions and implementations vary independently
- **Strategy** is similar to Bridge but Strategy's intent is behavioral, not structural

---

## Example

See [Bridge.java](Bridge.java) for a complete implementation with shapes and rendering engines.
