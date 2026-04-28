# Couplers

**Couplers** are code smells that create excessive, inappropriate, or unnecessary coupling between classes. They make the codebase fragile: a change in one place unexpectedly breaks another, and code is difficult to reuse in isolation.

---

## 1. Feature Envy

### What is it?
A method that seems more interested in the data of another class than in the class it belongs to. It accesses the other class's fields or calls its methods repeatedly, suggesting the method wants to *live* in the other class.

### Signs
- A method calls several getters on another object in sequence
- The method would make more sense as a member of the class it "envies"
- The method uses more of another class's data than its own

### Fix: **Move Method** — move the envious method to the class it accesses most
```java
// BEFORE — calculateRentalCost lives in Rental but works on Car's data
class Car {
    double dailyRate;
    boolean isLuxury;
}

class Rental {
    Car car;
    int days;

    // Feature Envy: uses car's data more than its own
    double calculateCost() {
        double base = car.dailyRate * days;
        double surcharge = car.isLuxury ? base * 0.20 : 0;
        return base + surcharge;
    }
}

// AFTER — cost calculation moved to Car where the data lives
class Car {
    double dailyRate;
    boolean isLuxury;

    double calculateRentalCost(int days) {
        double base      = dailyRate * days;
        double surcharge = isLuxury  ? base * 0.20 : 0;
        return base + surcharge;
    }
}

class Rental {
    Car car;
    int days;

    double calculateCost() { return car.calculateRentalCost(days); }
}
```

---

## 2. Inappropriate Intimacy

### What is it?
Two classes that are too knowledgeable about each other's internal implementation details — accessing each other's private fields (sometimes via package-private visibility), or having circular dependencies. They are so entangled that they cannot be used or tested independently.

### Signs
- Classes that frequently access each other's private/package fields
- Bidirectional associations where a unidirectional one would suffice
- Two classes that must always be changed together

### Fix: **Move Method / Move Field** / **Extract Class** / **Hide Delegate**
Identify and relocate the pieces of shared state into a single owner. Remove the circular dependency by introducing an abstraction.

```java
// BEFORE — Order directly accesses Customer's internal discount list
class Customer {
    List<Double> specialDiscounts = new ArrayList<>(); // package-private; intimate access
}

class Order {
    Customer customer;
    double applyBestDiscount(double price) {
        // Reaches into Customer's internals
        double best = customer.specialDiscounts.stream()
                .mapToDouble(Double::doubleValue).max().orElse(0);
        return price * (1 - best);
    }
}

// AFTER — Customer owns its discount logic; Order asks, not peeks
class Customer {
    private List<Double> specialDiscounts = new ArrayList<>();

    void addDiscount(double rate) { specialDiscounts.add(rate); }

    double getBestDiscount() {
        return specialDiscounts.stream().mapToDouble(Double::doubleValue).max().orElse(0);
    }
}

class Order {
    Customer customer;
    double applyBestDiscount(double price) {
        return price * (1 - customer.getBestDiscount());
    }
}
```

---

## 3. Message Chains

### What is it?
A long chain of navigation calls: `a.getB().getC().getD().getValue()`. Each link in the chain couples the calling code to the full internal structure of the object graph. If any link changes, the caller breaks.

This violates the **Law of Demeter**: *"Only talk to your immediate friends."*

### Signs
- Method calls chained more than two or three levels deep
- The caller knows the internal structure of objects it should not care about
- Many places in the codebase navigate the same chain

### Fix: **Hide Delegate** — add a shortcut method that hides the navigation
```java
// BEFORE — caller navigates three levels deep
String city = order.getCustomer().getAddress().getCity();

// AFTER — hide the chain behind a method on Order
class Order {
    private Customer customer;
    // ...
    public String getCustomerCity() {
        return customer.getAddress().getCity(); // chain hidden inside Order
    }
}
// Caller:
String city = order.getCustomerCity();
```

---

## 4. Middle Man

### What is it?
A class that exists only to delegate every method call to another class, doing nothing itself. This may be the result of over-applying Hide Delegate — the hiding went too far. The middle man class adds an extra indirection layer with no benefit.

### Signs
- More than half the methods in a class do nothing but call the same method on a delegate
- The class has no state of its own
- Removing the class would not lose any functionality

### Fix: **Remove Middle Man** — callers should call the real class directly
```java
// BEFORE — PersonMiddleMan only delegates to Department
class Department {
    String getManager() { return "Alice"; }
}

class PersonMiddleMan {
    private Department department;
    // Only delegation — no real work done here
    String getManager() { return department.getManager(); }
}

// AFTER — remove the middle man; call Department directly
Department dept   = new Department();
String manager = dept.getManager();
```

---

## 5. Incomplete Library Class

### What is it?
A library class (from a third-party or standard library) that lacks a method you need. Because you cannot modify the library, you are forced to write the needed logic outside the class — often duplicated wherever it is needed.

### Signs
- Utility methods that operate on a library class scattered across the codebase
- Wrapper calls that add only the missing feature around an existing library call

### Fix: **Introduce Foreign Method** (if one place) / **Introduce Local Extension** (if many places)
```java
// BEFORE — date arithmetic scattered everywhere because java.util.Date lacks it
Date start = ...;
// repeated everywhere:
Date nextWeek = new Date(start.getTime() + 7L * 24 * 60 * 60 * 1000);

// FIX: Introduce a Foreign Method as a static utility in one place
class DateUtils {
    static Date addDays(Date date, int days) {
        return new Date(date.getTime() + (long) days * 24 * 60 * 60 * 1000);
    }
}
// Callers:
Date nextWeek = DateUtils.addDays(start, 7);

// ALTERNATIVELY — Introduce Local Extension (subclass or wrapper)
class ExtendedDate extends java.util.Date {
    ExtendedDate(long time) { super(time); }
    ExtendedDate addDays(int days) {
        return new ExtendedDate(this.getTime() + (long) days * 24 * 60 * 60 * 1000);
    }
}
```

---

## See Also
- [CouplersExample.java](./CouplersExample.java) — compilable Java examples of all five smells
- [Refactoring Techniques → Moving Features](../../refactoring-techniques/moving-features/README.md)
- [Refactoring Techniques → Simplifying Method Calls](../../refactoring-techniques/simplifying-method-calls/README.md)
