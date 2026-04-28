# Mediator Pattern

## Intent

Define an object that **encapsulates how a set of objects interact**. Mediator promotes loose coupling by keeping objects from referring to each other explicitly, and it lets you vary their interaction independently.

---

## Also Known As

- Coordinator, Hub

---

## Motivation

Consider a flight control system. Each aircraft needs to know about other aircraft to avoid collisions — but having each aircraft talk directly to every other would be extremely complex and tightly coupled (O(n²) connections). Instead, all aircraft communicate through an air traffic controller (the Mediator). Each aircraft only knows about the controller; the controller coordinates all interactions.

Similarly, in a GUI form with many widgets (checkboxes, text fields, buttons), you might want: "When checkbox is checked, enable text field; when text changes, update button label." If each widget directly references every other widget, it's a mess. A mediator (like a form dialog controller) centralizes all that logic.

---

## Applicability

Use Mediator when:

- ✅ A set of objects communicate in **well-defined but complex** ways, resulting in interdependencies that are hard to understand
- ✅ Reusing an object is difficult because it refers to and communicates with many other objects
- ✅ Behavior distributed among several classes should be **customizable without subclassing**
- ✅ You want to replace many-to-many communication with one-to-many (through the mediator)

---

## Structure

```
+-----------+         +-------------------+
| Colleague |◆------->|    Mediator       |
+-----------+         +-------------------+
| -mediator |         |+notify(sender,event)|
| +changed()|         +-------------------+
+-----------+                  ^
      ^                        |
      |                +-------+--------+
+-----+------+         |ConcreteMediator|
|ConcreteColleague     +----------------+
+-------------+        |+notify()       |
|             |        |+coordinate()   |
+-------------+        +----------------+
```

---

## Participants

- **Mediator** — Defines an interface for communicating with Colleague objects
- **ConcreteMediator** — Implements cooperative behavior by coordinating Colleague objects; knows and maintains its colleagues
- **Colleague** — Each Colleague class knows its Mediator object; communicates with the mediator instead of other colleagues

---

## Collaborations

Colleagues send and receive requests from a Mediator object. The Mediator implements the cooperative behavior by routing requests between the appropriate Colleague(s).

---

## Consequences

### Benefits
- ✅ **Reduces subclassing** — Centralizes behavior that would otherwise be distributed across many objects
- ✅ **Decouples colleagues** — Colleagues can vary and be reused independently
- ✅ **Simplifies object protocols** — Replaces many-to-many with one-to-many interactions
- ✅ **Centralizes control** — Easier to understand and maintain cooperation logic

### Drawbacks
- ❌ **Mediator complexity** — The mediator can become a "god object" that knows and does too much

---

## Mediator vs. Facade

| | Mediator | Facade |
|---|---|---|
| Direction | Bidirectional — colleagues talk back to mediator | Unidirectional — client talks to facade |
| Awareness | Colleagues know the mediator | Subsystem doesn't know about facade |
| Purpose | Coordinate peers | Simplify access |

---

## Related Patterns

- **Facade** differs from Mediator: Facade just simplifies; Mediator coordinates bidirectional communication
- **Observer** — Colleagues can observe the Mediator for events
- **Command** — Commands can be sent to the Mediator

---

## Example

See [Mediator.java](Mediator.java) for a complete chat room implementation.
