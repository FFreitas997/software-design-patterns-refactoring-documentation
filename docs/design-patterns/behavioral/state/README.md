# State Pattern

## Intent

Allow an object to **alter its behavior when its internal state changes**. The object will appear to change its class.

---

## Also Known As

- Objects for States

---

## Motivation

Consider a vending machine. It behaves differently depending on its state:
- **Idle**: waiting for money to be inserted
- **Has Money**: money inserted, waiting for product selection
- **Dispensing**: delivering the selected product
- **Out of Stock**: no products available

Without the State pattern, you'd have large `if-else` or `switch` blocks checking the current state at every operation. Adding a new state means modifying every method. This is fragile and hard to maintain.

The State pattern puts each state's behavior into a separate `State` class. The "context" object (vending machine) delegates operations to its current state object. Transitioning to a new state is just changing the current state object.

---

## Applicability

Use State when:

- ✅ An object's behavior **depends on its state**, and it must change its behavior at run-time depending on that state
- ✅ Operations have **large, multipart conditional statements** that depend on the object's state
- ✅ States and transitions are numerous and difficult to manage in one class

---

## Structure

```
+-------------+         +----------+
|   Context   |◆------->|  State   |
+-------------+         +----------+
| -state      |         |+handle() |
| +request()  |-------->+----------+
| +setState() |               ^
+-------------+      +--------+--------+
                      |                 |
             +--------+------+ +-------+-------+
             |ConcreteStateA | |ConcreteStateB |
             +---------------+ +---------------+
             | +handle()     | | +handle()     |
             +---------------+ +---------------+
```

---

## Participants

- **Context** (`VendingMachine`) — Defines the interface of interest to clients; maintains an instance of a ConcreteState subclass that defines the current state
- **State** — Defines an interface for encapsulating the behavior associated with a particular state of the Context
- **ConcreteState** (`IdleState`, `HasMoneyState`, etc.) — Each subclass implements a behavior associated with a state of the Context

---

## Collaborations

- Context delegates state-specific requests to the current ConcreteState object
- A context may pass itself as an argument to the State object handling the request — this lets State objects transition the context to another state
- The client interacts with the Context and is not aware of state objects

---

## Consequences

### Benefits
- ✅ **Localizes state-specific behavior** — All behavior for a state is in one place
- ✅ **Explicit state transitions** — Transitions are explicit, not scattered through if-else
- ✅ **Easy to add new states** — Just add a new State class; no changes to Context or other states
- ✅ **Eliminates conditional statements** — State-dependent behavior is replaced by polymorphism

### Drawbacks
- ❌ **Increased number of classes** — Each state is a class; can be excessive for few, simple states
- ❌ **Coupling between states** — States need to know about each other to trigger transitions

---

## State vs. Strategy

| | State | Strategy |
|---|---|---|
| Changing | State objects can change themselves (transition) | Client sets the strategy |
| Knowledge | States know about other states | Strategies are independent |
| Use case | Object's behavior changes based on internal state | Swap algorithms for an operation |

---

## Related Patterns

- **Singleton** — State objects are often Singletons (no per-instance state)
- **Flyweight** — Explains when and how State objects can be shared
- **Strategy** — Similar in structure; different in intent

---

## Example

See [State.java](State.java) for a complete vending machine implementation.
