# Prototype Pattern

## Intent

Specify the kinds of objects to create using a **prototypical instance**, and create new objects by **copying** (cloning) this prototype.

---

## Also Known As

- Clone

---

## Motivation

Sometimes creating an object is expensive or complex — it might involve database lookups, network calls, expensive initialization, or deep configuration. Instead of creating a new object from scratch each time, you can create one "template" object (the prototype), and simply clone it when you need more instances.

Consider a game with hundreds of enemy types, each loaded from a config file. Creating each enemy from scratch every time is slow. Instead, load the configuration once per type, create a prototype enemy, and clone it for each new instance during gameplay.

Another scenario: when you need to copy an object whose class is not known at compile time (e.g., you hold a reference to a `Shape` but don't know if it's a `Circle` or `Rectangle`). The prototype lets you copy it without knowing its concrete type.

---

## Applicability

Use Prototype when:

- ✅ The classes to instantiate are specified at runtime (e.g., dynamic loading)
- ✅ You want to avoid building a factory hierarchy that mirrors the product hierarchy
- ✅ Object creation is expensive, and a copy is cheaper than a full initialization
- ✅ The difference between instances is only in their **state**, not their structure
- ✅ You need to copy objects without coupling to their concrete classes

---

## Structure

```
+-------------------+
|    Prototype      |
+-------------------+
| +clone(): Proto   |
+-------------------+
          ^
          |
+---------+---------+
|                   |
+-------+-------+   +-------+-------+
|ConcreteProto1 |   |ConcreteProto2 |
+---------------+   +---------------+
| +clone()      |   | +clone()      |
+---------------+   +---------------+
```

---

## Participants

- **Prototype** — Declares an interface for cloning itself (e.g., `Cloneable` in Java, or a custom `clone()` method)
- **ConcretePrototype** — Implements the cloning operation; copies itself
- **Client** — Creates new objects by asking a prototype to clone itself

---

## Collaborations

A client asks a prototype to clone itself. The prototype returns a new object that is a copy of itself.

---

## Consequences

### Benefits
- ✅ **Adds and removes products at runtime** — Register and unregister prototypes dynamically
- ✅ **Specifies new objects by varying values** — Clone a prototype and change a few fields instead of subclassing
- ✅ **Reduced subclassing** — No need for a parallel Creator hierarchy as in Factory Method
- ✅ **Configures application with classes dynamically** — Load and register new product classes at runtime

### Drawbacks
- ❌ **Cloning complex objects with circular references** can be tricky
- ❌ **Deep vs. shallow copy** ambiguity — Must decide whether to copy referenced objects too
- ❌ Each subclass must implement the `clone()` method, which can be difficult if classes contain objects that don't support copying or have circular references

---

## Deep Copy vs. Shallow Copy

| Type | Description | Risk |
|---|---|---|
| **Shallow copy** | Copies field values; references point to same objects | Shared mutable objects create bugs |
| **Deep copy** | Recursively copies all referenced objects | Safe but can be expensive |

In Java, `Object.clone()` performs a **shallow copy** by default. For deep copy, you must manually clone each mutable field.

---

## Implementation Notes

### Java's Cloneable

Java provides the `Cloneable` marker interface and `Object.clone()`. To use it:
1. Implement `Cloneable` on the class
2. Override `clone()` and make it `public`
3. For deep copy, clone mutable fields manually

```java
@Override
public MyClass clone() {
    try {
        MyClass copy = (MyClass) super.clone(); // shallow copy
        copy.mutableList = new ArrayList<>(this.mutableList); // deep copy field
        return copy;
    } catch (CloneNotSupportedException e) {
        throw new AssertionError(); // won't happen
    }
}
```

### Prototype Registry

Often combined with a **Prototype Registry** (or cache):
```java
Map<String, Prototype> registry = new HashMap<>();
registry.put("circle", new Circle(5, Color.RED));
// ...
Shape shape = registry.get("circle").clone();
```

---

## Related Patterns

- **Abstract Factory** may store a set of Prototypes and clone them to return product objects
- **Composite** and **Decorator** designs benefit from Prototype because they are often complex object graphs that need copying
- **Singleton** and Prototype are at opposite ends: Singleton ensures one instance; Prototype encourages many copies

---

## Example

See [Prototype.java](Prototype.java) for a complete implementation with geometric shapes and a prototype registry.
