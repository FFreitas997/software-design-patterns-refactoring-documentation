# Decorator Pattern

## Intent

**Attach additional responsibilities to an object dynamically.** Decorators provide a flexible alternative to subclassing for extending functionality.

---

## Also Known As

- Wrapper (same as Adapter, but with a different intent)

---

## Motivation

Adding features to objects by subclassing works, but it can cause a class explosion. For a coffee ordering system with 3 base drinks and 5 possible add-ons (milk, soy, mocha, whip, sugar), subclassing produces 3 × 2^5 = 96 classes!

Decorator solves this by wrapping an object in a sequence of "decorator" objects, each adding one feature. A decorator implements the same interface as the object it wraps, so it can be used in the same places. Decorators can be stacked in any order:

```
DarkRoast → withMilk → withMocha → withWhip
```

Each call to `cost()` delegates to the wrapped object and adds its own cost.

---

## Applicability

Use Decorator when:

- ✅ You want to add responsibilities to individual objects dynamically and transparently (without affecting other objects)
- ✅ You want to add responsibilities that can be **withdrawn** later
- ✅ Extension by subclassing is impractical because it leads to a large number of subclasses to support every combination

---

## Structure

```
+------------------+
|   Component      |
+------------------+
| +operation()     |
+------------------+
        ^
   +----+-----+
   |           |
+------+    +-----------+
| Leaf |    | Decorator |◆-----> Component
+------+    +-----------+
|+operation()|+operation()|  <- calls component.operation() + adds behavior
+------+    +-----------+
               ^
               |
    +----------+-----------+
    |                       |
+----------+         +----------+
|ConcreteA |         |ConcreteB |
+----------+         +----------+
```

---

## Participants

- **Component** (`Beverage`) — Defines the interface for objects that can have responsibilities added dynamically
- **ConcreteComponent** (`Espresso`, `DarkRoast`) — Defines an object to which additional responsibilities can be attached
- **Decorator** (`CondimentDecorator`) — Maintains a reference to a Component object and defines an interface that conforms to Component's interface
- **ConcreteDecorator** (`Milk`, `Mocha`, `Whip`) — Adds responsibilities to the component

---

## Collaborations

The Decorator forwards requests to its Component object. It may optionally perform additional operations before and after forwarding the request.

---

## Consequences

### Benefits
- ✅ **More flexibility than static inheritance** — Responsibilities can be added/removed at runtime
- ✅ **Avoids feature-loaded base classes** — Pay-as-you-go feature addition
- ✅ **Open/Closed Principle** — Extend behavior without modifying existing code
- ✅ **Composition over inheritance** — Mix and match behaviors freely

### Drawbacks
- ❌ **Many small objects** — Systems using decorators often end up with many small objects that all look alike, making them hard to debug
- ❌ **Identity issues** — A decorated component is not identical to the original; don't rely on object identity
- ❌ **Order matters** — The order decorators are applied can affect behavior

---

## Related Patterns

- **Adapter** changes an object's interface; Decorator enhances an interface
- **Composite** and Decorator have similar structure diagrams but different intents; Composite structures objects into trees, Decorator adds responsibilities
- **Strategy** lets you change the guts of an object; Decorator lets you change the skin

---

## Example

See [Decorator.java](Decorator.java) for a complete coffee shop implementation.
