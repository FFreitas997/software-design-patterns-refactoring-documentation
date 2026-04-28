# Strategy Pattern

## Intent

Define a family of algorithms, **encapsulate each one**, and make them **interchangeable**. Strategy lets the algorithm vary independently from clients that use it.

---

## Also Known As

- Policy

---

## Motivation

Consider a navigation application that calculates routes. It might support different strategies: shortest path (Dijkstra), fastest path (considering traffic), scenic route, or bicycle-friendly. Each algorithm is complex; you wouldn't want to hardcode one in the navigator or clutter it with if-else blocks.

The Strategy pattern:
1. Defines a `RouteStrategy` interface with a `buildRoute()` method
2. Implements each algorithm in separate classes (`DrivingStrategy`, `WalkingStrategy`, `BicycleStrategy`)
3. The `Navigator` (context) holds a reference to a `RouteStrategy` and delegates routing to it

The client can swap strategies at runtime without changing the Navigator.

---

## Applicability

Use Strategy when:

- ✅ Many related classes differ only in their behavior — Strategy provides a way to configure a class with one of many behaviors
- ✅ You need different variants of an algorithm and want to switch between them at runtime
- ✅ An algorithm uses data that clients shouldn't know about (hide complex data structures with Strategy)
- ✅ A class defines many behaviors, and these appear as multiple conditional statements — move each branch into its own Strategy class

---

## Structure

```
+------------+         +-----------+
|  Context   |◆------->|  Strategy |
+------------+         +-----------+
| -strategy  |         |+execute() |
| +setStrat()|         +-----------+
| +doWork()  |               ^
+------------+      +--------+--------+
                    |                  |
          +---------+-----+  +---------+-----+
          |ConcreteStratA |  |ConcreteStratB |
          +---------------+  +---------------+
          | +execute()    |  | +execute()    |
          +---------------+  +---------------+
```

---

## Participants

- **Strategy** — Declares an interface common to all supported algorithms
- **ConcreteStrategy** — Implements the algorithm using the Strategy interface
- **Context** — Is configured with a ConcreteStrategy object; maintains a reference to a Strategy object; may define an interface that lets Strategy access its data

---

## Collaborations

- Context and Strategy interact to implement the selected algorithm
- A Context may pass all the data required by the algorithm to the Strategy; alternatively, Context can pass itself as an argument
- The client chooses the ConcreteStrategy and passes it to the Context; thereafter, the client interacts with the Context exclusively

---

## Consequences

### Benefits
- ✅ **Families of related algorithms** — Hierarchies of Strategy classes define a family of algorithms
- ✅ **Alternative to subclassing** — Varying behavior through inheritance is inflexible; Strategy provides a cleaner alternative
- ✅ **Eliminates conditional statements** — Replaces conditional chains with polymorphism
- ✅ **Choice of implementations** — Clients can choose between strategies with different time/space trade-offs
- ✅ **Open/Closed Principle** — New strategies without changing the Context

### Drawbacks
- ❌ **Client must be aware of strategies** — Client must understand how strategies differ before selecting one
- ❌ **Communication overhead** — Strategy interface may be unused by some ConcreteStrategies
- ❌ **Increased number of objects** — Creates many small strategy classes

---

## Strategy vs. Template Method

| | Strategy | Template Method |
|---|---|---|
| Mechanism | Object composition | Inheritance |
| Granularity | Entire algorithm is replaced | Part of the algorithm (hooks) |
| Runtime change | Yes — swap strategy objects | No — fixed at compile time |

---

## Related Patterns

- **Template Method** uses inheritance to vary part of an algorithm; Strategy uses delegation to vary the whole algorithm
- **Flyweight** — Strategy objects often make good flyweights
- **Factory Method** can create strategies
- **State** — Similar structure; State objects can change context's behavior (and themselves); Strategy is set by the client

---

## Example

See [Strategy.java](Strategy.java) for a complete e-commerce checkout with multiple payment strategies and sorting strategies.
