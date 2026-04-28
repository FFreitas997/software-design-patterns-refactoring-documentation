# Creational Design Patterns

Creational design patterns abstract the instantiation process. They help make a system independent of how its objects are created, composed, and represented.

## Why Creational Patterns?

As systems evolve, they tend to depend more on **object composition** than class inheritance. When that happens, the focus shifts from hard-coding a fixed set of behaviors to defining a smaller set of fundamental behaviors that can be composed into any number of more complex ones.

Creating objects with `new` directly couples your code to a concrete class, making it harder to change and test. Creational patterns solve this by:

- **Encapsulating knowledge** about which concrete classes the system uses
- **Hiding how instances** of these classes are created and put together
- **Providing flexibility** in *what* gets created, *who* creates it, *how* it gets created, and *when*

---

## The Five Creational Patterns

### 🔒 [Singleton](singleton/README.md)
**Intent:** Ensure a class has only one instance and provide a global point of access to it.

**Use when:** You need exactly one object to coordinate actions across the system (e.g., a configuration manager, logger, or connection pool).

---

### 🏭 [Factory Method](factory-method/README.md)
**Intent:** Define an interface for creating an object, but let subclasses decide which class to instantiate.

**Use when:** A class cannot anticipate the type of objects it must create, or subclasses should specify what they create.

---

### 🏗️ [Abstract Factory](abstract-factory/README.md)
**Intent:** Provide an interface for creating families of related or dependent objects without specifying their concrete classes.

**Use when:** A system must be independent of how its products are created and you need to enforce constraints across a family of products.

---

### 🧱 [Builder](builder/README.md)
**Intent:** Separate the construction of a complex object from its representation, allowing the same construction process to create different representations.

**Use when:** The construction process of a complex object should be independent of the parts that make it up and how they're assembled.

---

### 🧬 [Prototype](prototype/README.md)
**Intent:** Specify the kinds of objects to create using a prototypical instance, and create new objects by copying (cloning) this prototype.

**Use when:** Classes to instantiate are specified at run-time, or to avoid building a class hierarchy of factories that parallels the class hierarchy of products.

---

## Comparison

| Pattern | Creates | Key Benefit |
|---|---|---|
| Singleton | One instance of one class | Controlled access to sole instance |
| Factory Method | Instances of several related classes | Subclasses choose the class to instantiate |
| Abstract Factory | Families of related objects | Enforces constraints among products |
| Builder | Complex objects step by step | Fine control over construction process |
| Prototype | Clones of existing objects | Avoids costly creation; copies object state |

---

## When to Use Creational Patterns

- When the exact types and dependencies of the objects your code should work with are not known upfront
- When you want to provide a library of products, revealing only their interfaces
- When you want to save resources by reusing existing objects instead of rebuilding them
- When you want to ensure only a single instance exists throughout your application's lifetime
