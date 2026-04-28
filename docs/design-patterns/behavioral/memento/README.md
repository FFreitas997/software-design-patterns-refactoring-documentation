# Memento Pattern

## Intent

Without violating encapsulation, **capture and externalize an object's internal state** so that the object can be restored to this state later.

---

## Also Known As

- Token, Snapshot

---

## Motivation

Sometimes you need to record the internal state of an object so you can restore it later. Consider a text editor that must implement undo/redo: to undo an operation, you need to roll back the editor to a previous state. A calculator that should be able to revert its last calculation. A game that needs to save/restore checkpoints.

The naive approach — exposing the object's state via getters so an external caretaker can save it — breaks encapsulation. Any class that can read the state can also change it, which is dangerous.

The Memento pattern solves this by:
1. Letting the **Originator** (the object with state) create a **Memento** — an opaque snapshot
2. The **Caretaker** stores the Memento but cannot inspect or modify it
3. The Originator can restore its state from a Memento when needed

---

## Applicability

Use Memento when:

- ✅ A snapshot of an object's state must be saved so it can be restored later
- ✅ A direct interface to obtaining the state would expose implementation details and break encapsulation
- ✅ You need to implement **undo/redo**, snapshots, or rollback operations

---

## Structure

```
+-------------+        +-------------+        +---------------+
|  Originator |------->|   Memento   |<-------|   Caretaker   |
+-------------+        +-------------+        +---------------+
| state       |        | state       |        | mementos[]    |
| +createMemento()     | +getState() |        | +addMemento() |
| +restore(memento)    +-------------+        | +getMemento() |
+-------------+                               +---------------+
```

---

## Participants

- **Originator** — Creates a Memento containing a snapshot of its current internal state; uses the Memento to restore its internal state
- **Memento** — Stores internal state of the Originator; protects against access by objects other than the Originator (wide vs. narrow interface)
- **Caretaker** — Responsible for the Memento's safekeeping; never operates on or examines the contents of a Memento

---

## Collaborations

1. Caretaker requests a memento from Originator to save state
2. Caretaker stores the memento (without inspecting it)
3. When undo is needed, Caretaker passes the memento back to the Originator
4. Originator restores its state from the memento

---

## Consequences

### Benefits
- ✅ **Preserves encapsulation** — Originator's state is never exposed to external classes
- ✅ **Simplifies Originator** — The Originator doesn't need to track its own undo history
- ✅ **Complete history** — Multiple mementos can be stored for multi-level undo

### Drawbacks
- ❌ **Memory overhead** — Storing many mementos can be expensive
- ❌ **Java limitations** — Java doesn't have the concept of a "narrow interface" (accessible only to Originator), so Caretaker can potentially access the Memento's contents
- ❌ **Hidden cost** — If the Originator state is large, copying it is expensive

---

## Related Patterns

- **Command** — Commands can use Mementos to maintain state for undoing effects
- **Iterator** — Mementos can be used to store iteration state
- **Prototype** — Can sometimes substitute for Memento when copies are lightweight

---

## Example

See [Memento.java](Memento.java) for a complete text editor and canvas with undo/redo support.
