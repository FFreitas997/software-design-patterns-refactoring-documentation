# Adapter Pattern

## Intent

Convert the interface of a class into another interface that clients expect. Adapter lets classes work together that couldn't otherwise because of incompatible interfaces.

---

## Also Known As

- Wrapper

---

## Motivation

Consider a drawing application that uses `Shape` objects with a `draw()` method. You want to reuse a third-party `LegacyRectangle` class that has a `display(x1, y1, x2, y2)` method instead. You can't modify the third-party class, and creating a new class from scratch throws away its existing implementation.

The Adapter pattern wraps the `LegacyRectangle` in a class that implements the `Shape` interface, translating `draw()` calls into `display()` calls. The client code uses only `Shape` and never knows about `LegacyRectangle`.

---

## Applicability

Use Adapter when:

- ✅ You want to use an existing class but its interface doesn't match what you need
- ✅ You want to create a reusable class that cooperates with unrelated or unforeseen classes with incompatible interfaces
- ✅ You need to use several existing subclasses but it's impractical to adapt their interface by subclassing every one (an **object adapter** can adapt the interface of the parent class)

---

## Structure

### Object Adapter (preferred in Java)
```
+----------+         +---------------+        +------------------+
|  Client  |-------->|    Target     |        |    Adaptee       |
+----------+         +---------------+        +------------------+
                     | +request()    |        | +specificRequest()|
                     +---------------+        +------------------+
                             ^                        ^
                             |                        |
                    +--------+-------+                |
                    |    Adapter    |----------------+
                    +---------------+   (wraps via composition)
                    | +request()    |
                    +---------------+
```

### Class Adapter (uses multiple inheritance — not idiomatic in Java)
```
+----------+         +---------------+        +------------------+
|  Client  |-------->|    Target     |        |    Adaptee       |
+----------+         +---------------+        +------------------+
                             ^                        ^
                             |                        |
                    +--------+-------+----------------+
                    |    Adapter    |  (extends both)
                    +---------------+
```

---

## Participants

- **Target** (`Shape`) — Defines the domain-specific interface that Client uses
- **Client** — Collaborates with objects conforming to the Target interface
- **Adaptee** (`LegacyRectangle`) — Defines an existing interface that needs adapting
- **Adapter** (`RectangleAdapter`) — Adapts the interface of Adaptee to the Target interface

---

## Collaborations

Clients call operations on an Adapter instance. In turn, the Adapter calls Adaptee operations that carry out the request.

---

## Consequences

### Benefits
- ✅ **Reuse of existing code** — Adapts incompatible interfaces without modification
- ✅ **Single Responsibility** — Separates the interface-conversion code from primary business logic
- ✅ **Open/Closed Principle** — Introduce new adapters without changing existing client code

### Object Adapter vs. Class Adapter

| | Object Adapter | Class Adapter |
|---|---|---|
| Mechanism | Composition | Inheritance |
| Adapts subclasses? | Yes | No (commits to one concrete Adaptee) |
| Can override Adaptee behavior? | No | Yes |
| Java-friendly? | Yes | Limited (no multiple inheritance) |

---

## Related Patterns

- **Bridge** separates interface from implementation up front; Adapter makes two existing things work together
- **Decorator** enhances an object without changing its interface; Adapter changes the interface
- **Facade** defines a new simplified interface; Adapter re-uses an existing interface
- **Proxy** provides the same interface as the object it wraps

---

## Example

See [Adapter.java](Adapter.java) for a complete object adapter implementation.
