# Object-Orientation Abusers

**Object-Orientation Abusers** are code smells that arise when object-oriented principles are applied incorrectly, partially, or not at all. The code may use classes and inheritance, but in ways that work against the grain of OO design rather than with it.

---

## 1. Switch Statements

### What is it?
A complex `switch` statement (or equivalent chains of `if/else if`) that checks the type or state of an object to decide what behaviour to apply. The same switch may be duplicated across several places in the codebase. Every time a new case is needed, all duplicates must be updated.

### Signs
- `switch` on a type code, string constant, or enum
- The same `switch` logic appears in more than one method
- Adding a new "type" requires editing multiple switch blocks

### Fix: **Replace Conditional with Polymorphism**
Move each case's behaviour into a subclass (or strategy) and let dynamic dispatch replace the switch.

```java
// BEFORE
public double calculateShipping(String method, double weight) {
    switch (method) {
        case "STANDARD": return weight * 1.5;
        case "EXPRESS":  return weight * 3.0;
        case "OVERNIGHT":return weight * 5.0;
        default: throw new IllegalArgumentException("Unknown: " + method);
    }
}

// AFTER — each shipping method is a class
interface ShippingMethod {
    double calculate(double weight);
}

class StandardShipping  implements ShippingMethod {
    public double calculate(double weight) { return weight * 1.5; }
}
class ExpressShipping   implements ShippingMethod {
    public double calculate(double weight) { return weight * 3.0; }
}
class OvernightShipping implements ShippingMethod {
    public double calculate(double weight) { return weight * 5.0; }
}
// Caller just: shippingMethod.calculate(weight); — no switch needed
```

---

## 2. Temporary Field

### What is it?
A class field that is only assigned a value under certain conditions — typically set before calling one long method, then ignored everywhere else. This leaves the object in an inconsistent state and makes the class harder to reason about.

### Signs
- A field that is `null` most of the time
- Fields initialised in one method and used only in another, with no conceptual relationship
- A comment like `// only set when processing large orders`

### Fix: **Extract Class** / **Replace Method with Method Object**
Move the fields that only make sense together into a separate object whose lifecycle matches their usage.

```java
// BEFORE — 'tempDiscount' is a temporary field with inconsistent state
class OrderProcessor {
    private double tempDiscount; // only meaningful during processOrder()

    public double processOrder(Order order) {
        tempDiscount = order.isPremium() ? 0.2 : 0.1;
        return order.getTotal() * (1 - tempDiscount);
    }
}

// AFTER — discount is a local variable (or a method object) — no temporary field
class OrderProcessor {
    public double processOrder(Order order) {
        double discount = order.isPremium() ? 0.2 : 0.1;
        return order.getTotal() * (1 - discount);
    }
}
```

---

## 3. Refused Bequest

### What is it?
A subclass that inherits methods and data from its parent but does not use most of them — or actively overrides them to do nothing or throw exceptions. This violates the **Liskov Substitution Principle**: the subclass cannot be used wherever the parent is expected.

### Signs
- A subclass overrides a method only to throw `UnsupportedOperationException`
- A subclass ignores most inherited fields (they are always zero or null)
- The subclass relationship is "is-a" only in name, not in behaviour

### Fix: **Replace Inheritance with Delegation** / **Extract Superclass**
If the subclass only needs some behaviour of the parent, favour composition over inheritance.

```java
// BEFORE — ReadOnlyList refuses the add() bequest
class ReadOnlyList extends ArrayList<String> {
    @Override
    public boolean add(String item) {
        throw new UnsupportedOperationException("This list is read-only");
    }
    // Inherits 30+ methods it doesn't want
}

// AFTER — compose, don't inherit; expose only what is needed
class ReadOnlyList {
    private final List<String> delegate;

    ReadOnlyList(List<String> source) {
        this.delegate = Collections.unmodifiableList(new ArrayList<>(source));
    }

    public String get(int index)  { return delegate.get(index); }
    public int    size()          { return delegate.size(); }
    public boolean contains(Object o) { return delegate.contains(o); }
}
```

---

## 4. Alternative Classes with Different Interfaces

### What is it?
Two or more classes that perform the same or equivalent job but expose it through different method names. Clients cannot use them interchangeably and must know which class they are dealing with. This often emerges when two developers solve the same problem independently.

### Signs
- `UserService.fetchById(id)` and `CustomerRepository.loadCustomer(id)` do the same thing
- Two event classes with fields named `eventDate` in one and `occurredAt` in the other
- Parallel hierarchies with identical structure but different naming

### Fix: **Rename Method** + **Extract Interface** / **Move Method**
Align the interfaces so both classes implement a common contract, or merge them.

```java
// BEFORE — two classes that do the same job with different method names
class XmlExporter {
    public String exportToXml(Report report) { return "<report>...</report>"; }
}

class JsonExporter {
    public String buildJson(Report report) { return "{\"report\": {}}"; }
}

// AFTER — unified interface makes them interchangeable
interface ReportExporter {
    String export(Report report);
}

class XmlExporter implements ReportExporter {
    public String export(Report report) { return "<report>...</report>"; }
}

class JsonExporter implements ReportExporter {
    public String export(Report report) { return "{\"report\": {}}"; }
}
```

---

## See Also
- [OOAbusersExample.java](./OOAbusersExample.java) — compilable Java examples of all four smells
- [Refactoring Techniques → Simplifying Conditional Expressions](../../refactoring-techniques/simplifying-conditional-expressions/README.md)
- [Refactoring Techniques → Dealing with Generalization](../../refactoring-techniques/dealing-with-generalization/README.md)
