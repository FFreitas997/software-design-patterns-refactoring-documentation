# Composite Pattern

## Intent

Compose objects into **tree structures** to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects **uniformly**.

---

## Also Known As

- Tree Structure, Part-Whole Hierarchy

---

## Motivation

Consider a drawing application. Drawings can contain primitive shapes (Line, Circle) or complex groups of shapes. Groups can themselves contain other groups. When you want to draw everything, you don't want to treat groups differently from primitives — you just call `draw()` on all of them.

Similarly, in a file system: folders contain files or other folders. Whether you're calculating the size of a single file or an entire folder tree, the operation `getSize()` works the same way.

The Composite pattern creates a **Component** interface that both primitives ("leaves") and composites ("branches") implement. The composite's implementation of operations typically delegates to its children.

---

## Applicability

Use Composite when:

- ✅ You want to represent **part-whole hierarchies** of objects
- ✅ You want clients to be able to **ignore the difference** between compositions of objects and individual objects — clients treat all objects in the composite structure uniformly
- ✅ The structure can have **any level of nesting**

---

## Structure

```
+-------------------+
|    Component      |
+-------------------+
| +operation()      |
| +add(Component)   |
| +remove(Component)|
| +getChild(int)    |
+-------------------+
        ^
        |
   +----+-----+
   |           |
+------+    +----------+
| Leaf |    | Composite |
+------+    +----------+
|+operation()|+operation()|  <- calls operation() on each child
+------+    |+add()     |
            |+remove()  |
            |children   |
            +----------+
```

---

## Participants

- **Component** — Declares the interface for objects in the composition; optionally implements default behavior for the interface common to all classes; declares an interface for accessing and managing child components
- **Leaf** — Represents leaf objects in the composition (no children); defines behavior for primitive objects
- **Composite** — Defines behavior for components having children; stores child components; implements child-related operations in the Component interface
- **Client** — Manipulates objects in the composition through the Component interface

---

## Collaborations

Clients use the Component interface to interact with objects. If the recipient is a Leaf, the request is handled directly. If it's a Composite, it typically forwards the request to its children, possibly performing additional operations before or after.

---

## Consequences

### Benefits
- ✅ **Defines class hierarchies** of primitive and composite objects; complex objects can be assembled from simple ones
- ✅ **Simplifies client code** — Clients can treat composite structures and individual objects uniformly
- ✅ **Easy to add new component types** — Works with any new leaf or composite classes

### Drawbacks
- ❌ **Hard to restrict component types** — Can't use the type system to enforce that composites only contain certain kinds of leaves
- ❌ **Design can become overly general** — Providing a common interface for very different classes can make some operations meaningless for certain classes

---

## Related Patterns

- **Iterator** can be used to traverse composite trees
- **Visitor** can be used to apply operations to all components without changing their classes
- **Flyweight** lets you share components rather than creating new ones
- **Decorator** and Composite have similar structure diagrams; Decorator wraps one object, Composite wraps multiple

---

## Example

See [Composite.java](Composite.java) for a complete file system implementation.
