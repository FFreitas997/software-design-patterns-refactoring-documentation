# Singleton Pattern

## Intent

Ensure a class has **only one instance** and provide a **global point of access** to it.

---

## Also Known As

- Single Instance

---

## Motivation

For some classes, it's important to have exactly one instance. For example:

- A **Logger** — you want all parts of the application writing to the same log output
- A **Configuration Manager** — one place to read/write application settings
- A **Database Connection Pool** — managing a shared pool of connections
- A **Thread Pool** — controlling resource usage across the application

Although a global variable makes an object accessible, it doesn't prevent instantiating multiple instances. The Singleton pattern provides a way to ensure only one instance exists, while still offering global access.

---

## Applicability

Use Singleton when:

- ✅ There must be exactly one instance of a class, accessible from a well-known access point
- ✅ The sole instance should be extensible by subclassing, and clients should be able to use an extended instance without modifying their code
- ✅ You need stricter control over global variables (Singleton is more controlled than a global variable)

Do **not** use Singleton when:

- ❌ You want to write unit-testable code (Singletons make mocking difficult)
- ❌ Multiple instances might be needed in the future
- ❌ You are in a multi-module/microservices environment where "one instance per JVM" doesn't make sense

---

## Structure

```
+---------------------------+
|        Singleton          |
+---------------------------+
| - instance: Singleton     |
+---------------------------+
| - Singleton()             |  ← private constructor
| + getInstance(): Singleton|  ← static factory method
| + operation()             |
+---------------------------+
```

---

## Participants

- **Singleton** — The class that:
  - Defines a private static instance of itself
  - Has a private constructor to prevent external instantiation
  - Provides a public static `getInstance()` method that returns the sole instance

---

## Collaborations

Clients access a Singleton instance solely through `Singleton.getInstance()`. The first call creates the instance; subsequent calls return the existing instance.

---

## Consequences

### Benefits
- ✅ **Controlled access** to the sole instance
- ✅ **Reduced namespace** — avoids polluting the global namespace with global variables
- ✅ **Permits refinement** — Singleton class can be subclassed
- ✅ **Permits variable number of instances** — The pattern makes it easy to change your mind and allow more than one instance (just change `getInstance()`)
- ✅ **More flexible than class operations** — Unlike static methods, Singleton methods can be overridden in subclasses

### Drawbacks
- ❌ **Hard to test** — Singletons introduce global state; tests can interfere with each other
- ❌ **Violation of Single Responsibility Principle** — The class manages both its sole-instance policy and its regular responsibilities
- ❌ **Concurrency issues** — Careless implementation in multi-threaded environments can create multiple instances
- ❌ **Hidden dependencies** — Clients of Singleton don't declare it in their signatures, making dependencies less visible

---

## Implementation Notes

### Thread Safety

The most critical implementation concern is **thread safety**. The recommended approach is **Double-Checked Locking** with `volatile`:

```java
private static volatile Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {                    // First check (no locking)
        synchronized (Singleton.class) {
            if (instance == null) {            // Second check (with locking)
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

The `volatile` keyword ensures that the write to `instance` is visible to all threads and prevents instruction reordering.

### Initialization-on-Demand Holder (Bill Pugh Singleton)

An elegant, thread-safe, lazy-loading alternative using a static inner class:

```java
public class Singleton {
    private Singleton() {}

    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

The JVM guarantees that class initialization is thread-safe, so `INSTANCE` is created only once without any explicit synchronization.

### Enum Singleton (Joshua Bloch's recommendation)

```java
public enum Singleton {
    INSTANCE;

    public void doSomething() { ... }
}
```

This is the most concise, serialization-safe, and reflection-proof approach.

---

## Related Patterns

- **Abstract Factory**, **Builder**, and **Prototype** can all be implemented as Singletons
- **Facade** objects are often Singletons since only one facade object is required
- **State** objects are often Singletons

---

## Example

See [Singleton.java](Singleton.java) for a complete, thread-safe implementation using Double-Checked Locking.
