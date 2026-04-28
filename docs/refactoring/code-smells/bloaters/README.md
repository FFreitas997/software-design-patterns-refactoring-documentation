# Bloaters

**Bloaters** are code elements — methods, classes, or parameter lists — that have grown so large and unwieldy that they are nearly impossible to work with comfortably. Size creeps up incrementally; no single addition seems unreasonable in isolation, but the cumulative result is code that is expensive to read, test, and change.

---

## 1. Long Method

### What is it?
A method that has grown to dozens or hundreds of lines, typically because new logic was repeatedly appended rather than extracted. The longer a method, the harder it is to understand what it does, to test it in isolation, and to reuse any part of it.

### Signs
- Scrolling required to read the full method
- Multiple levels of nesting inside the method
- Comment blocks that break the method into "sections" (a sign each section wants to be its own method)
- Difficulty naming the method without using "and" or "or"

### Fix: **Extract Method**
Break the method into smaller, well-named methods. Each method should do one thing.

```java
// BEFORE — long method doing 3 different things
public void processOrder(Order order) {
    // validate
    if (order.getItems().isEmpty()) throw new IllegalArgumentException("Empty order");
    if (order.getCustomer() == null) throw new IllegalArgumentException("No customer");

    // calculate
    double subtotal = 0;
    for (Item item : order.getItems()) subtotal += item.getPrice() * item.getQuantity();
    double tax = subtotal * 0.15;
    double total = subtotal + tax;

    // persist
    orderRepository.save(order);
    System.out.println("Order saved. Total: " + total);
}

// AFTER — three focused, testable methods
public void processOrder(Order order) {
    validateOrder(order);
    double total = calculateTotal(order);
    saveAndConfirm(order, total);
}

private void validateOrder(Order order) {
    if (order.getItems().isEmpty()) throw new IllegalArgumentException("Empty order");
    if (order.getCustomer() == null)  throw new IllegalArgumentException("No customer");
}

private double calculateTotal(Order order) {
    double subtotal = order.getItems().stream()
            .mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
    return subtotal * 1.15;
}

private void saveAndConfirm(Order order, double total) {
    orderRepository.save(order);
    System.out.printf("Order saved. Total: %.2f%n", total);
}
```

---

## 2. Large Class

### What is it?
A class that has accumulated too many fields, methods, and responsibilities over time. Often called a **God Class** because it "knows too much" about the entire system. Large classes have low cohesion — their members serve unrelated purposes.

### Signs
- More than ~10–15 fields
- Methods that can be split into clearly distinct groups by theme
- The class name ends in `Manager`, `Handler`, `Processor`, `Utility`, or `Helper`
- Low test coverage because the class is hard to instantiate in isolation

### Fix: **Extract Class** / **Extract Subclass**
Move related groups of fields and methods into new, cohesive classes.

```java
// BEFORE — one class doing user management AND email AND audit logging
public class UserManager {
    private String name, email, passwordHash;
    private String smtpHost, smtpPort, fromAddress;
    private List<String> auditLog;

    public void saveUser() { /* ... */ }
    public void sendWelcomeEmail() { /* ... */ }
    public void sendPasswordReset() { /* ... */ }
    public void logAction(String action) { /* ... */ }
    public List<String> getAuditTrail() { return auditLog; }
}

// AFTER — three cohesive, single-responsibility classes
public class User {
    private String name, email, passwordHash;
    public void save() { /* ... */ }
}

public class EmailService {
    private String smtpHost, smtpPort, fromAddress;
    public void sendWelcomeEmail(User user) { /* ... */ }
    public void sendPasswordReset(User user) { /* ... */ }
}

public class AuditLogger {
    private List<String> entries = new ArrayList<>();
    public void log(String action) { entries.add(action); }
    public List<String> getTrail() { return Collections.unmodifiableList(entries); }
}
```

---

## 3. Primitive Obsession

### What is it?
Using primitive types (`int`, `String`, `double`) to represent concepts that deserve their own class. Common examples: money as a `double`, phone numbers as `String`, status codes as `int`, date ranges as two separate `Date` fields.

### Signs
- Validation of the same primitive scattered across multiple places
- Groups of primitives that always travel together (see Data Clumps)
- `String` used to represent enumerated values (`"ACTIVE"`, `"INACTIVE"`)

### Fix: **Replace Data Value with Object** / **Replace Type Code with Class**
```java
// BEFORE — money as a plain double; currency lost
double price = 29.99;
double tax   = price * 0.15;

// AFTER — Money value object encapsulates value and currency
public final class Money {
    private final double amount;
    private final String currency;

    public Money(double amount, String currency) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        this.amount   = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException("Currency mismatch");
        return new Money(this.amount + other.amount, this.currency);
    }

    public Money multiplyBy(double factor) {
        return new Money(this.amount * factor, this.currency);
    }

    @Override public String toString() { return String.format("%.2f %s", amount, currency); }
}
```

---

## 4. Long Parameter List

### What is it?
A method that requires more than three or four parameters. Long parameter lists are hard to read at the call site and are often a sign that the method is doing more than one thing, or that some parameters belong together in an object.

### Signs
- Call sites look like: `createUser("Alice", "Smith", "alice@x.com", "555-1234", "Admin", true, false)`
- Several parameters are always passed together
- Optional parameters lead to `null` arguments

### Fix: **Introduce Parameter Object** / **Preserve Whole Object**
```java
// BEFORE
public void createUser(String firstName, String lastName,
                        String email, String phone,
                        String role, boolean active, boolean verified) { /* ... */ }

// AFTER — group related data into a DTO
public class UserRegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String role;
    private final boolean active;
    private final boolean verified;
    // constructor + getters ...
}

public void createUser(UserRegistrationRequest request) { /* ... */ }
```

---

## 5. Data Clumps

### What is it?
Two or more data values that always appear together — as fields in multiple classes, or as parameters in multiple method signatures — but have never been extracted into their own class.

### Signs
- `String street, String city, String postalCode, String country` repeated across many classes and methods
- Removing one item from the group makes the remaining items meaningless

### Fix: **Extract Class**
```java
// BEFORE — address fields scattered and repeated everywhere
public class Customer {
    private String name;
    private String street, city, postalCode, country;
}

public class Supplier {
    private String companyName;
    private String street, city, postalCode, country;
}

// AFTER — Address is a first-class concept
public class Address {
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String postalCode, String country) {
        this.street     = street;
        this.city       = city;
        this.postalCode = postalCode;
        this.country    = country;
    }
    // getters, equals, hashCode, toString
}

public class Customer {
    private String name;
    private Address address;
}

public class Supplier {
    private String companyName;
    private Address address;
}
```

---

## See Also
- [BloatersExample.java](./BloatersExample.java) — compilable Java examples of all five smells
- [Refactoring Techniques → Composing Methods](../../refactoring-techniques/composing-methods/README.md)
- [Refactoring Techniques → Moving Features](../../refactoring-techniques/moving-features/README.md)
