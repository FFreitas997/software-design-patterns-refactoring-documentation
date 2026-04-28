# Dispensables

**Dispensables** are code elements that are unnecessary. Their presence adds noise and obscures the real logic. Removing or replacing them makes the code shorter, clearer, and easier to maintain — without losing any functionality.

---

## 1. Comments (Excessive or Misleading)

### What is it?
Comments that are used as a substitute for clear code. A long comment explaining what a confusing method does is usually a signal to rename and restructure the method itself. Outdated comments that no longer match the code are actively harmful.

### Signs
- Every method has a comment that restates what the code already says
- Comments block-structuring a single long method into "sections" (each section wants to be its own method)
- Comments that begin with "// FIXME", "// HACK", or "// I don't know why this works"

### Fix: **Rename Method** / **Extract Method** / **Introduce Assertion**
Let the code explain itself. Reserve comments for explaining *why* — business rules, non-obvious constraints, performance trade-offs.

```java
// BEFORE — comment compensates for a poor name
// Get the number of days between two dates
int d(long t1, long t2) {
    return (int)((t2 - t1) / 86400000);
}

// AFTER — self-explanatory name; constant explains the magic number
static final long MILLIS_PER_DAY = 86_400_000L;
int daysBetween(long startMillis, long endMillis) {
    return (int) ((endMillis - startMillis) / MILLIS_PER_DAY);
}
```

---

## 2. Duplicate Code

### What is it?
The same code structure appears in more than one place. When the underlying rule changes, every copy must be found and updated — and missing even one creates a bug.

### Signs
- The same block of logic appears in two or more methods or classes
- Copy-paste programming: searching the codebase for a method body finds multiple identical bodies
- Slight variations that are almost identical but differ by one variable name

### Fix: **Extract Method** / **Pull Up Method** / **Form Template Method**
Create a single authoritative implementation. If the logic differs between subclasses, use a Template Method or Strategy.

```java
// BEFORE — tax calculation duplicated
class OnlineOrder {
    double total(double subtotal) { return subtotal + subtotal * 0.15; }
}
class InStoreOrder {
    double total(double subtotal) { return subtotal + subtotal * 0.15; }
}

// AFTER — extracted into shared utility
class TaxCalculator {
    static final double TAX_RATE = 0.15;
    static double withTax(double subtotal) { return subtotal * (1 + TAX_RATE); }
}
class OnlineOrder  { double total(double subtotal) { return TaxCalculator.withTax(subtotal); } }
class InStoreOrder { double total(double subtotal) { return TaxCalculator.withTax(subtotal); } }
```

---

## 3. Lazy Class

### What is it?
A class that does so little that its existence is not worth the maintenance overhead. It may have been created speculatively, or it may have become a shell after other refactorings moved its responsibilities elsewhere.

### Signs
- A class with only one or two trivial methods
- A subclass that adds no new behaviour over its parent
- A wrapper class that only delegates a single call

### Fix: **Inline Class** / **Collapse Hierarchy**
Move the lazy class's behaviour into its caller or parent, and delete the now-empty class.

```java
// BEFORE — TemperatureConverter is too thin to justify its own class
class TemperatureConverter {
    static double toCelsius(double fahrenheit) { return (fahrenheit - 32) * 5 / 9; }
}
// Used in exactly one place:
class WeatherReport {
    void display(double tempF) {
        System.out.println(TemperatureConverter.toCelsius(tempF) + "°C");
    }
}

// AFTER — inlined; the overhead of a separate class is gone
class WeatherReport {
    void display(double tempF) {
        double celsius = (tempF - 32) * 5.0 / 9.0;
        System.out.println(celsius + "°C");
    }
}
```

---

## 4. Data Class

### What is it?
A class that contains only fields with getters and setters — no real behaviour. Data classes are not inherently wrong (DTOs, records), but when other classes manipulate data class fields to do work that should belong to the data class itself, the responsibilities are in the wrong place.

### Signs
- A class that is only ever used via its getters/setters by external classes
- External classes compute things about the object's own data
- The class is sometimes called a "dumb data holder"

### Fix: **Move Method** — move the behaviour that manipulates the data into the data class
```java
// BEFORE — external code operates on CustomerData's fields
class CustomerData {
    String firstName, lastName, email;
}
class OrderService {
    String getFullName(CustomerData c) { return c.firstName + " " + c.lastName; }
}

// AFTER — behaviour lives with the data it needs
class Customer {
    private final String firstName, lastName, email;
    Customer(String firstName, String lastName, String email) {
        this.firstName = firstName; this.lastName = lastName; this.email = email;
    }
    public String getFullName() { return firstName + " " + lastName; }
    public String getEmail()    { return email; }
}
```

---

## 5. Dead Code

### What is it?
Code that is never executed — a variable never read, a method never called, a class never instantiated, a branch that can never be reached.

### Signs
- A method exists but no call site references it (IDE shows "0 usages")
- A field that is assigned but never read
- An `else` branch on a condition that is always true
- Commented-out code blocks left "just in case"

### Fix: **Delete it**
Version control preserves history. Dead code adds noise and misleads maintainers. Delete it without hesitation.

```java
// BEFORE — dead code in multiple forms
class Processor {
    private int unusedCounter = 0; // ← never read

    public void process(boolean flag) {
        if (flag) {
            doWork();
        } else {
            // Dead branch — 'flag' is always true at every call site
            doOldWork();
        }
    }

    private void doWork()    { System.out.println("Working"); }
    private void doOldWork() { System.out.println("Old work"); } // ← dead method
}

// AFTER — dead code removed
class Processor {
    public void process() {
        doWork();
    }
    private void doWork() { System.out.println("Working"); }
}
```

---

## 6. Speculative Generality

### What is it?
Code written to handle requirements that do not yet exist and may never exist. Hooks, abstract layers, parameters, and flags added "just in case" add complexity without delivering value today.

### Signs
- Abstract classes with only one concrete subclass
- Methods with parameters that are never used by any current caller
- Overly generic names like `AbstractBaseHandlerFactory` for code that does one thing

### Fix: Apply **YAGNI** — You Aren't Gonna Need It. Remove the unused abstraction.
```java
// BEFORE — unnecessary abstraction for a single concrete use case
interface DataProcessor<T, R> {
    R process(T input, ProcessingContext ctx, ProcessingOptions opts);
}
class CsvDataProcessor implements DataProcessor<String, List<String>> {
    public List<String> process(String input, ProcessingContext ctx, ProcessingOptions opts) {
        return List.of(input.split(",")); // ctx and opts are always null/ignored
    }
}

// AFTER — simple method, no unnecessary generality
class CsvParser {
    public List<String> parse(String csvLine) {
        return List.of(csvLine.split(","));
    }
}
```

---

## See Also
- [DispensablesExample.java](./DispensablesExample.java) — compilable Java examples of all six smells
- [Refactoring Techniques → Composing Methods](../../refactoring-techniques/composing-methods/README.md)
- [Refactoring Techniques → Moving Features](../../refactoring-techniques/moving-features/README.md)
