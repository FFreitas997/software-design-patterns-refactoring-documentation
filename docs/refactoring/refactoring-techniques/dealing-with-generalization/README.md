# Dealing with Generalization

These refactoring techniques deal with moving functionality along the inheritance hierarchy — pulling common features up into superclasses, pushing specialisations down into subclasses, extracting interfaces, and deciding when to replace inheritance with composition.

---

## 1. Pull Up Field

**Problem:** Two subclasses have the same field.
**Solution:** Move the field to the superclass.

```java
// BEFORE
class Salesperson extends Employee { private String name; }
class Engineer    extends Employee { private String name; } // duplicated field

// AFTER
class Employee    { private String name; } // field lives once in the superclass
class Salesperson extends Employee { }
class Engineer    extends Employee { }
```

---

## 2. Pull Up Method

**Problem:** Two subclasses have methods with identical or very similar implementations.
**Solution:** Move the method to the superclass.

```java
// BEFORE
class Customer    extends Party { String createBill() { /* identical */ return ""; } }
class Supplier    extends Party { String createBill() { /* identical */ return ""; } }

// AFTER
class Party       { String createBill() { return ""; } } // pulled up
class Customer    extends Party { }
class Supplier    extends Party { }
```

---

## 3. Pull Up Constructor Body

**Problem:** Subclass constructors have identical code at the top of their bodies.
**Solution:** Move the common code to a superclass constructor and invoke it via `super()`.

```java
// BEFORE
class Manager extends Employee {
    Manager(String name, String id) {
        this.name = name; // duplicated in every subclass
        this.id   = id;
    }
}

// AFTER
class Employee {
    Employee(String name, String id) { this.name = name; this.id = id; }
}
class Manager extends Employee {
    Manager(String name, String id) { super(name, id); } // common code pulled up
}
```

---

## 4. Push Down Method

**Problem:** Behaviour on a superclass is relevant to only one or a few subclasses.
**Solution:** Move that behaviour into the relevant subclasses.

```java
// BEFORE
class Employee { void reportSales() { /* only Salespeople report sales */ } }

// AFTER
class Employee    { /* no sales reporting */ }
class Salesperson extends Employee { void reportSales() { /* now where it belongs */ } }
```

---

## 5. Push Down Field

**Problem:** A field on a superclass is used only by some subclasses.
**Solution:** Move it to the subclasses that actually use it.

```java
// BEFORE
class Employee    { protected String salesQuota; } // only Salespeople use this

// AFTER
class Employee    { }
class Salesperson extends Employee { private String salesQuota; } // where it belongs
```

---

## 6. Extract Subclass

**Problem:** A class has features that are used only in some instances.
**Solution:** Create a subclass for that subset of features.

```java
// BEFORE
class JobItem {
    boolean isLabor;
    Employee employee; // only set when isLabor == true
    double getUnitPrice() { return isLabor ? employee.getRate() : unitPrice; }
}

// AFTER
class JobItem    { double getUnitPrice() { return unitPrice; } }
class LaborItem  extends JobItem {
    Employee employee;
    @Override double getUnitPrice() { return employee.getRate(); }
}
```

---

## 7. Extract Superclass

**Problem:** Two classes have similar features.
**Solution:** Create a superclass and move the common features to it.

```java
// BEFORE
class Department { String name; double totalAnnualCost() { /* ... */ return 0; } }
class Employee   { String name; double annualCost()      { /* ... */ return 0; } }

// AFTER
abstract class Party {
    String name;
    abstract double annualCost();
}
class Department extends Party { double annualCost() { /* ... */ return 0; } }
class Employee   extends Party { double annualCost() { /* ... */ return 0; } }
```

---

## 8. Extract Interface

**Problem:** Several clients use the same subset of a class's interface, or two classes have part of their interfaces in common.
**Solution:** Extract the common subset into an interface.

```java
// BEFORE — TimeSheet uses only billableWeeks() and rate() from Employee
class TimeSheet {
    double charge(Employee employee, int weeks) {
        return employee.rate() * weeks * employee.billableWeeks();
    }
}

// AFTER — Billable interface captures exactly what TimeSheet needs
interface Billable {
    double rate();
    int    billableWeeks();
}
class Employee implements Billable { /* ... */ }
class TimeSheet {
    double charge(Billable employee, int weeks) {
        return employee.rate() * weeks * employee.billableWeeks();
    }
}
```

---

## 9. Collapse Hierarchy

**Problem:** A superclass and subclass are not very different — merging them simplifies the hierarchy.
**Solution:** Merge them into one class.

```java
// BEFORE — SalesEmployee adds nothing beyond Employee
class Employee     { String name; double salary; }
class SalesEmployee extends Employee { } // empty subclass

// AFTER — collapsed
class Employee { String name; double salary; }
```

---

## 10. Form Template Method

**Problem:** Two subclasses implement similar algorithms whose steps are in the same order but with different details.
**Solution:** Pull the invariant skeleton into the superclass as a Template Method; push the varying steps into the subclasses.

```java
// BEFORE
class Residency { void generate() { printTitle(); printBody(); printFooter(); /* each different */ } }
class Lease     { void generate() { printTitle(); printBody(); printFooter(); /* each different */ } }

// AFTER — Template Method pattern
abstract class Statement {
    final void generate() { printTitle(); printBody(); printFooter(); } // invariant skeleton
    abstract void printTitle();
    abstract void printBody();
    abstract void printFooter();
}
class Residency extends Statement { /* implements the three hook methods */ }
class Lease     extends Statement { /* implements the three hook methods */ }
```

---

## 11. Replace Inheritance with Delegation

**Problem:** A subclass uses only part of a parent class interface, or the "is-a" relationship is wrong.
**Solution:** Create a field referencing the old parent, delegate to it for the needed methods, and remove the inheritance.

```java
// BEFORE — Stack "is-a" Vector? No! Stack should "have-a" Vector.
class Stack extends Vector<Object> {
    public Object push(Object element) { addElement(element); return element; }
    public Object pop()  { Object top = lastElement(); removeElementAt(size() - 1); return top; }
}

// AFTER — composition instead of inheritance
class Stack {
    private final java.util.Vector<Object> storage = new java.util.Vector<>();
    public Object push(Object element) { storage.addElement(element); return element; }
    public Object pop()  { Object top = storage.lastElement(); storage.removeElementAt(storage.size()-1); return top; }
    public int    size() { return storage.size(); }
}
```

---

## 12. Replace Delegation with Inheritance

**Problem:** A class delegates all its methods to another class, adding no value.
**Solution:** Make the delegating class a subclass of the delegate class.

```java
// BEFORE — Employee delegates every method call to Person
class Employee {
    private Person person = new Person();
    String getName()  { return person.getName(); }
    String getPhone() { return person.getPhone(); }
}

// AFTER — Employee simply extends Person
class Employee extends Person {
    // Inherits getName() and getPhone() directly — no delegation boilerplate
}
```

---

## See Also
- [DealingWithGeneralization.java](./DealingWithGeneralization.java) — compilable before/after Java examples for all twelve techniques
