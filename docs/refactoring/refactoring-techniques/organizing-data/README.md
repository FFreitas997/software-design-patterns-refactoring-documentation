# Organizing Data

These refactoring techniques improve how data is represented, stored, and accessed. They help replace primitives with meaningful objects, enforce encapsulation, eliminate magic numbers, and build correct associations between objects.

---

## 1. Self Encapsulate Field

**Problem:** You are accessing a field directly within its own class.
**Solution:** Create a getter and setter for the field and use only those — even internally. This allows subclasses to override how the field is obtained.

```java
// BEFORE
class IntRange {
    int low, high;
    boolean includes(int arg) { return arg >= low && arg <= high; }
}

// AFTER
class IntRange {
    private int low, high;
    int getLow()  { return low; }
    int getHigh() { return high; }
    boolean includes(int arg) { return arg >= getLow() && arg <= getHigh(); }
}
```

---

## 2. Replace Data Value with Object

**Problem:** A data item needs additional data or behaviour.
**Solution:** Turn the data item into an object.

```java
// BEFORE — telephone number is just a String; no validation or formatting
class Order {
    String customerPhone;
}

// AFTER — TelephoneNumber is a class with its own behaviour
class TelephoneNumber {
    private final String number;
    TelephoneNumber(String number) {
        if (!number.matches("\\d{3}-\\d{4}")) throw new IllegalArgumentException("Invalid phone");
        this.number = number;
    }
    String getAreaCode() { return number.substring(0, 3); }
    @Override public String toString() { return number; }
}
class Order {
    TelephoneNumber customerPhone;
}
```

---

## 3. Change Value to Reference

**Problem:** You have many identical instances of a class, and you want them to be the same object so updates propagate everywhere.
**Solution:** Turn the object into a reference object (use a registry or factory).

```java
// BEFORE — two Customer objects for the same customer ID are independent
Customer c1 = new Customer("Alice");
Customer c2 = new Customer("Alice"); // separate instance — c1 != c2

// AFTER — CustomerRegistry ensures one canonical instance per name
class CustomerRegistry {
    private static final Map<String, Customer> instances = new HashMap<>();
    static Customer get(String name) {
        return instances.computeIfAbsent(name, Customer::new);
    }
}
Customer c1 = CustomerRegistry.get("Alice");
Customer c2 = CustomerRegistry.get("Alice"); // c1 == c2
```

---

## 4. Change Reference to Value

**Problem:** A reference object is small and immutable — it should be a value object (compared by value, not identity).
**Solution:** Make the object immutable and implement `equals()` and `hashCode()` based on its fields.

```java
// BEFORE — Currency is compared by reference
class Currency { String code; }

// AFTER — Currency is an immutable value object
final class Currency {
    private final String code;
    Currency(String code) { this.code = code; }
    @Override public boolean equals(Object o) {
        return o instanceof Currency && code.equals(((Currency) o).code);
    }
    @Override public int hashCode() { return code.hashCode(); }
    @Override public String toString() { return code; }
}
```

---

## 5. Replace Array with Object

**Problem:** You have an array in which certain elements mean different things.
**Solution:** Replace the array with an object that has a named field for each element.

```java
// BEFORE — array where [0]=name, [1]=wins, [2]=losses
String[] row = new String[3];
row[0] = "Liverpool";
row[1] = "15";
row[2] = "3";

// AFTER — named fields are self-documenting
class PerformanceRecord {
    String teamName;
    int wins, losses;
}
```

---

## 6. Duplicate Observed Data

**Problem:** Domain data is stored only in a GUI component, making it inaccessible to the domain layer.
**Solution:** Copy the data to a domain object and keep the two in sync with an observer/listener.

```java
// Concept: a TextField's text is domain data
// Domain object holds a copy; UI synchronises via a listener
class OrderForm {
    private JTextField startField = new JTextField();
    private Order      order      = new Order();

    public OrderForm() {
        startField.addActionListener(e -> order.setStartDate(startField.getText()));
    }
}
```

---

## 7. Replace Magic Number with Symbolic Constant

**Problem:** A literal number with a special meaning is scattered through the code.
**Solution:** Create a constant, name it after its meaning, and replace all occurrences.

```java
// BEFORE
double potentialEnergy(double mass, double height) {
    return mass * height * 9.81;
}

// AFTER
static final double GRAVITATIONAL_CONSTANT = 9.81;
double potentialEnergy(double mass, double height) {
    return mass * height * GRAVITATIONAL_CONSTANT;
}
```

---

## 8. Encapsulate Field

**Problem:** A public field is directly accessible — its representation cannot change without touching all callers.
**Solution:** Make the field private and provide accessor methods.

```java
// BEFORE
class Person { public String name; }

// AFTER
class Person {
    private String name;
    public String getName()             { return name; }
    public void   setName(String name)  { this.name = name; }
}
```

---

## 9. Encapsulate Collection

**Problem:** A collection field is returned directly — callers can modify the collection without the class knowing.
**Solution:** Return an unmodifiable view or a defensive copy; provide specific add/remove methods.

```java
// BEFORE
class Course {
    public List<String> prerequisites = new ArrayList<>();
}

// AFTER
class Course {
    private final List<String> prerequisites = new ArrayList<>();
    public List<String> getPrerequisites() { return Collections.unmodifiableList(prerequisites); }
    public void addPrerequisite(String p)   { prerequisites.add(p); }
    public void removePrerequisite(String p){ prerequisites.remove(p); }
}
```

---

## 10. Replace Type Code with Class / Subclasses / State / Strategy

**Problem:** An integer or string "type code" controls behaviour through switch/if statements.
**Solution:** Replace with a proper class, subclasses, State, or Strategy depending on complexity.

```java
// BEFORE — integer type code
class Employee {
    static final int ENGINEER   = 0;
    static final int SALESPERSON = 1;
    int type;
}

// AFTER — enum (simple case); or full polymorphism (complex case)
enum EmployeeType { ENGINEER, SALESPERSON }
class Employee {
    EmployeeType type;
}
```

---

## See Also
- [OrganizingData.java](./OrganizingData.java) — compilable before/after Java examples for all techniques
