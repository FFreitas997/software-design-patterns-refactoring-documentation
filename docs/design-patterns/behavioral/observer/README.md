# Observer Pattern

## Intent

Define a **one-to-many dependency** between objects so that when one object changes state, all its dependents are **notified and updated automatically**.

---

## Also Known As

- Dependents, Publish-Subscribe, Event System, Listener

---

## Motivation

Consider a spreadsheet application. A single data model (cells) is displayed in multiple views: a bar chart, a pie chart, a tabular view. When a cell value changes, all views should update automatically. The data model shouldn't need to know what views exist — views could be added or removed at any time.

The Observer pattern decouples the subject (data model) from its observers (views). The subject maintains a list of observers and notifies them when its state changes. Observers register themselves with the subject and are automatically notified.

This is the foundation of event-driven programming, reactive frameworks, and the Model-View-Controller (MVC) architecture.

---

## Applicability

Use Observer when:

- ✅ When an abstraction has **two aspects**, one dependent on the other — encapsulating these aspects separately lets you vary and reuse them independently
- ✅ When a change to one object requires **changing others**, and you don't know how many objects need to change
- ✅ When an object should notify **other objects without assuming who they are** (loose coupling)
- ✅ When you want an **event/notification system** (pub/sub)

---

## Structure

```
+----------+  notifies  +----------+
|  Subject |----------->| Observer |
+----------+            +----------+
| observers[]|          | +update()|
| +attach() |            +----------+
| +detach() |                 ^
| +notify() |       +---------+---------+
+----------+        |                   |
                +---+--------+  +-------+-----+
                |ConcreteObs1|  |ConcreteObs2 |
                +------------+  +-------------+
                | +update()  |  | +update()   |
                +------------+  +-------------+
```

---

## Participants

- **Subject** — Knows its observers; provides an interface for attaching and detaching Observer objects; notifies observers when state changes
- **Observer** — Defines an updating interface for objects that should be notified of changes in a Subject
- **ConcreteSubject** — Stores state of interest to observers; sends notification when state changes
- **ConcreteObserver** — Maintains a reference to the subject; implements the Observer interface to keep its state consistent with the subject's

---

## Collaborations

1. ConcreteSubject notifies its observers when a change occurs
2. After being informed, a ConcreteObserver queries the subject for information to reconcile its state with the subject's state

---

## Consequences

### Benefits
- ✅ **Loose coupling** — Subject and Observer are independent; each can vary independently
- ✅ **Broadcast communication** — A subject doesn't need to know who its observers are
- ✅ **Open/Closed Principle** — Add new observers without modifying the subject
- ✅ **Foundation of event-driven programming**

### Drawbacks
- ❌ **Unexpected updates** — An observer doesn't know about other observers; a change to the subject may cause a cascade of updates
- ❌ **Memory leaks** — If observers aren't detached properly, they can prevent garbage collection ("lapsed listener" problem)
- ❌ **Update order** — The order in which observers are notified is unpredictable

---

## Push vs. Pull

| | Push Model | Pull Model |
|---|---|---|
| Data sent | Subject sends all data in notification | Subject sends minimal data; observer queries for more |
| Coupling | Less: observer is self-contained | More: observer must know the subject interface |
| Efficiency | May send unused data | Observer fetches only what it needs |

---

## Related Patterns

- **Mediator** — By encapsulating complex update semantics, the ChangeManager acts as a mediator between subjects and observers
- **Singleton** — The ChangeManager is a Singleton
- **Command** — Commands can act as observers

---

## Example

See [Observer.java](Observer.java) for a complete stock market notification system.
