# Structural Design Patterns

Structural design patterns explain how to assemble objects and classes into larger structures, while keeping these structures flexible and efficient.

## Why Structural Patterns?

As systems grow, individual classes and objects need to be combined into larger architectures. Doing this naively often leads to:
- **Tight coupling** — changes in one class break others
- **Rigid hierarchies** — hard to extend without modifying existing code
- **Bloated interfaces** — clients know too much about the internals

Structural patterns solve these problems by describing ways to compose classes and objects to form larger structures **without losing flexibility**.

---

## The Seven Structural Patterns

### 🔌 [Adapter](adapter/README.md)
**Intent:** Convert the interface of a class into another interface that clients expect. Lets incompatible interfaces work together.

**Analogy:** A power plug adapter that lets a European device plug into an American outlet.

**Use when:** You want to use an existing class but its interface doesn't match what you need.

---

### 🌉 [Bridge](bridge/README.md)
**Intent:** Decouple an abstraction from its implementation so that the two can vary independently.

**Analogy:** A TV remote (abstraction) can control different TV brands (implementations). The remote API doesn't change when you switch TV brands.

**Use when:** You want to avoid a permanent binding between abstraction and implementation, or both should be extensible via subclassing.

---

### 🌳 [Composite](composite/README.md)
**Intent:** Compose objects into tree structures to represent part-whole hierarchies. Let clients treat individual objects and compositions uniformly.

**Analogy:** A file system where both files and folders are "file system entries" — you can copy/move/delete either the same way.

**Use when:** You want clients to ignore the difference between compositions of objects and individual objects.

---

### 🎁 [Decorator](decorator/README.md)
**Intent:** Attach additional responsibilities to an object dynamically. Provides a flexible alternative to subclassing for extending functionality.

**Analogy:** Coffee with different add-ons: add milk → add sugar → add whipped cream. Each add-on wraps the previous.

**Use when:** You want to add responsibilities to objects without affecting other objects, and subclassing would lead to an explosion of classes.

---

### 🏛️ [Facade](facade/README.md)
**Intent:** Provide a simplified interface to a complex subsystem.

**Analogy:** A home theater remote with a "Watch Movie" button that adjusts lights, starts the projector, switches inputs, and dims the screen — all in one action.

**Use when:** You want to provide a simple interface to a complex body of code.

---

### 🪶 [Flyweight](flyweight/README.md)
**Intent:** Use sharing to support large numbers of fine-grained objects efficiently.

**Analogy:** In a text editor, every character 'a' shares the same font/color object rather than each having its own copy.

**Use when:** An application uses a large number of objects that share state and the memory cost of each object is high.

---

### 🛡️ [Proxy](proxy/README.md)
**Intent:** Provide a surrogate or placeholder for another object to control access to it.

**Analogy:** A bank check is a proxy for funds in an account — it controls when and how the funds are accessed.

**Use when:** You need a more versatile or sophisticated reference to an object than a simple pointer.

---

## Comparison

| Pattern | Changes Interface? | Adds Behavior? | Key Benefit |
|---|---|---|---|
| Adapter | Yes (wraps incompatible) | Rarely | Compatibility between interfaces |
| Bridge | No | No | Separate abstraction from implementation |
| Composite | No | No | Uniform treatment of tree structures |
| Decorator | No (same interface) | Yes | Dynamic behavior extension |
| Facade | Yes (simplifies) | No | Simplified access to subsystem |
| Flyweight | No | No | Memory efficiency |
| Proxy | No | Optionally | Controlled access |

---

## When to Use Structural Patterns

- When you need to make incompatible classes work together (Adapter)
- When you want to add features without subclassing (Decorator)
- When you need to simplify a complex API (Facade)
- When you're building tree-structured data (Composite)
- When memory is constrained and objects share state (Flyweight)
- When you need controlled, lazy, or logged access to an object (Proxy)
- When you want to change the abstraction and implementation independently (Bridge)
