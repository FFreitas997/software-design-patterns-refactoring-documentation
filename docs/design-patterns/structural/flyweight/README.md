# Flyweight Pattern

## Intent

Use **sharing** to support large numbers of fine-grained objects efficiently.

---

## Motivation

Some applications benefit from using objects throughout their design, but a naive implementation would be prohibitively expensive due to memory.

Consider a text editor. Displaying a page of text may require thousands of `Character` objects. If each character object stores its character code, font, size, and color, you'd use a lot of memory for what could be the same font/size/color repeated millions of times.

Flyweight solves this by recognizing that most character objects share the same **intrinsic state** (font, size, color) and only differ in their **extrinsic state** (position on screen). The flyweight stores the intrinsic state and receives extrinsic state as parameters. Thousands of "A" characters can share a single `Character('A', Arial, 12pt, Black)` flyweight object.

---

## Key Concepts

### Intrinsic State
- Stored *inside* the flyweight
- Independent of the flyweight's context
- Shareable across many objects

### Extrinsic State
- Stored *outside* the flyweight (by the client or in a separate context object)
- Depends on and varies with the flyweight's context
- Cannot be shared

---

## Applicability

Use Flyweight when **all** of the following are true:

- ✅ An application uses a large number of objects
- ✅ Storage costs are high because of the sheer quantity of objects
- ✅ Most object state can be made extrinsic
- ✅ Many groups of objects may be replaced by relatively few shared objects once extrinsic state is removed
- ✅ The application doesn't depend on object identity (shared objects are identical)

---

## Structure

```
+--------------+        +------------------+
| FlyweightFactory|     |    Flyweight     |
+--------------+        +------------------+
| flyweights   |------->| +operation(      |
| +getFlyweight|        |   extrinsicState)|
+--------------+        +------------------+
                                 ^
                       +---------+---------+
                       |                   |
             +---------+------+  +---------+--------+
             |ConcreteFlyweight|  |UnsharedFlyweight |
             +----------------+  +------------------+
             |intrinsicState  |  |allState          |
             |+operation()    |  |+operation()      |
             +----------------+  +------------------+
```

---

## Participants

- **Flyweight** — Declares an interface through which flyweights can receive and act on extrinsic state
- **ConcreteFlyweight** — Implements the Flyweight interface and adds storage for intrinsic state; must be shareable; any state it stores must be intrinsic (context-independent)
- **UnsharedConcreteFlyweight** — Not all subclasses need to be shared; often has children that are ConcreteFlyweights
- **FlyweightFactory** — Creates and manages flyweight objects; ensures flyweights are shared properly (returns existing if already exists, creates new one otherwise)
- **Client** — Maintains references to flyweights; computes or stores extrinsic state

---

## Consequences

### Benefits
- ✅ **Massive memory savings** when the intrinsic state constitutes most of the object
- ✅ Effective for large numbers of similar objects

### Drawbacks
- ❌ **Runtime cost** — Managing extrinsic state adds overhead
- ❌ **Complexity** — The code becomes more complex due to separation of state
- ❌ **Not useful** if each object is truly unique (no intrinsic state to share)

---

## Related Patterns

- **Composite** is often used with Flyweight to implement shared leaf nodes
- **State** and **Strategy** objects are often implemented as Flyweights
- **Singleton** guarantees one instance; Flyweight uses a factory to manage many shared instances

---

## Example

See [Flyweight.java](Flyweight.java) for a complete character rendering implementation.
