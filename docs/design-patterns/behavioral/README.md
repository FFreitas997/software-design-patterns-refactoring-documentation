# Behavioral Design Patterns

Behavioral patterns are concerned with **algorithms and the assignment of responsibilities** between objects. They characterize complex control flow that's difficult to follow at run-time, and shift focus away from the flow to how objects are interconnected.

## Why Behavioral Patterns?

As systems grow, the logic of how objects communicate and who is responsible for what tends to get messy. Behavioral patterns help by:

- **Encapsulating** algorithms and behaviors so they can vary independently
- **Defining** clear communication protocols between objects
- **Distributing** responsibility intelligently across the system
- **Decoupling** the sender of a request from its receiver

---

## The Ten Behavioral Patterns

### ⛓️ [Chain of Responsibility](chain-of-responsibility/README.md)
**Intent:** Pass requests along a chain of handlers. Each handler decides to process or pass along the chain.

**Use when:** More than one object may handle a request, and the handler isn't known a priori.

---

### 📋 [Command](command/README.md)
**Intent:** Encapsulate a request as an object, allowing parameterization, queuing, logging, and undoable operations.

**Use when:** You need to parameterize actions, support undo/redo, or implement transactional behavior.

---

### 🔁 [Iterator](iterator/README.md)
**Intent:** Provide a way to access elements of an aggregate object sequentially without exposing its underlying representation.

**Use when:** You want to traverse a collection without knowing its internal structure.

---

### 🤝 [Mediator](mediator/README.md)
**Intent:** Define an object that encapsulates how a set of objects interact. Promotes loose coupling by keeping objects from referring to each other explicitly.

**Use when:** Many objects communicate in complex ways, resulting in high coupling.

---

### 📸 [Memento](memento/README.md)
**Intent:** Without violating encapsulation, capture and externalize an object's internal state so the object can be restored to this state later.

**Use when:** You need undo/redo functionality or snapshot/restore capabilities.

---

### 👀 [Observer](observer/README.md)
**Intent:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified automatically.

**Use when:** A change in one object requires changing others, and you don't know how many objects need to change.

---

### 🚦 [State](state/README.md)
**Intent:** Allow an object to alter its behavior when its internal state changes. The object will appear to change its class.

**Use when:** An object's behavior depends on its state and must change at runtime.

---

### 🎯 [Strategy](strategy/README.md)
**Intent:** Define a family of algorithms, encapsulate each one, and make them interchangeable. Lets the algorithm vary independently from clients that use it.

**Use when:** You want to define a class that will have one behavior similar to other behaviors, or you need to switch algorithms at runtime.

---

### 📐 [Template Method](template-method/README.md)
**Intent:** Define the skeleton of an algorithm in an operation, deferring some steps to subclasses.

**Use when:** You want to let subclasses redefine certain steps of an algorithm without changing the algorithm's overall structure.

---

### 👤 [Visitor](visitor/README.md)
**Intent:** Represent an operation to be performed on elements of an object structure. Visitor lets you define a new operation without changing the classes of the elements.

**Use when:** You need to perform many distinct and unrelated operations on an object structure without polluting their classes.

---

## Comparison

| Pattern | Key Mechanism | Main Benefit |
|---|---|---|
| Chain of Responsibility | Linked list of handlers | Decouple sender/receiver; multiple handlers |
| Command | Request as object | Undo/redo, queuing, logging |
| Iterator | External traversal object | Uniform collection traversal |
| Mediator | Central hub object | Reduce coupling between colleagues |
| Memento | Saved state snapshot | Undo/redo without breaking encapsulation |
| Observer | Event subscription | Automatic notification of dependents |
| State | Strategy per state | Clean state-dependent behavior |
| Strategy | Interchangeable algorithm | Runtime algorithm selection |
| Template Method | Abstract base class hook | Reuse algorithm skeleton |
| Visitor | Separate operation class | Add operations without modifying classes |
