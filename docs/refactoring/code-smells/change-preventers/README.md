# Change Preventers

**Change Preventers** are code smells that make the system unnecessarily rigid. When you need to make a single logical change, you find yourself touching many unrelated places, or you discover that one change forces another. These smells violate the Single Responsibility Principle and the Open/Closed Principle.

---

## 1. Divergent Change

### What is it?
A single class that is changed for many different, unrelated reasons. If you find yourself saying *"I change this class when I need to change the database AND when I need to change the report format AND when the business rules change"*, you have divergent change.

The class is doing too many things. Each distinct *reason to change* should belong to a different class.

### Signs
- Opening the same class for every different type of change
- The class has groups of methods that relate to different themes (persistence, formatting, calculation)
- Hard to name the class without using "And"

### Fix: **Extract Class**
Split the class by reason to change. Each resulting class has a single responsibility.

```java
// BEFORE — one class changes for multiple unrelated reasons
class EmployeeReport {
    private Employee employee;

    // Changes when database schema changes:
    public void saveToDatabase() { /* SQL logic */ }

    // Changes when report format changes:
    public String formatAsCsv() { /* CSV logic */ }
    public String formatAsHtml() { /* HTML logic */ }

    // Changes when business tax rules change:
    public double calculateTax() { /* tax logic */ }
}

// AFTER — each class has exactly one reason to change
class EmployeeRepository {
    public void save(Employee employee) { /* SQL logic */ }
}

class EmployeeReportFormatter {
    public String toCsv(Employee employee)  { /* CSV logic */  return ""; }
    public String toHtml(Employee employee) { /* HTML logic */ return ""; }
}

class EmployeeTaxCalculator {
    public double calculate(Employee employee) { /* tax logic */ return 0; }
}
```

---

## 2. Shotgun Surgery

### What is it?
The opposite of Divergent Change. A single logical change requires making many small edits scattered across many different classes. For example, adding a new field to a concept requires touching the constructor, the DTO, the mapper, the formatter, the database schema, the API response class... all for one conceptual addition.

Shotgun Surgery is a coupling problem: the concept that is changing is spread across too many places.

### Signs
- Every feature addition touches 10+ files
- You must search the whole codebase for a constant or rule that appears in multiple locations
- Missing one of the scattered changes introduces a subtle bug

### Fix: **Move Method** / **Move Field** / **Inline Class**
Consolidate the scattered code into a single place so that the change requires touching only one class.

```java
// BEFORE — VAT rate scattered across four classes
class Invoice {
    double calculateVat(double amount) { return amount * 0.20; } // magic 0.20
}

class Receipt {
    double calculateVat(double amount) { return amount * 0.20; } // duplicated
}

class TaxReport {
    double computeVat(double amount) { return amount * 0.20; }   // duplicated again
}

// AFTER — single authoritative source for the VAT rate
class TaxPolicy {
    public static final double VAT_RATE = 0.20;

    public static double applyVat(double amount) {
        return amount * VAT_RATE;
    }
}

class Invoice  { double calculateVat(double amount) { return TaxPolicy.applyVat(amount); } }
class Receipt  { double calculateVat(double amount) { return TaxPolicy.applyVat(amount); } }
class TaxReport{ double computeVat(double amount)   { return TaxPolicy.applyVat(amount); } }
```

---

## 3. Parallel Inheritance Hierarchies

### What is it?
A special case of Shotgun Surgery where every time you add a subclass to one hierarchy, you must also add a corresponding subclass to another hierarchy. The two hierarchies mirror each other and must be kept in sync manually.

### Signs
- Class names in one hierarchy prefix/suffix the names of classes in another (`AnimalHandler`, `DogHandler`, `CatHandler` paralleling `Animal`, `Dog`, `Cat`)
- Adding a new subclass to hierarchy A requires adding a matching one to hierarchy B

### Fix: **Move Method** / **Move Field** — collapse one hierarchy into the other
Have one hierarchy's classes reference the other's (composition), so only one hierarchy needs to exist.

```java
// BEFORE — two parallel hierarchies that grow in lockstep
abstract class Shape      { abstract double area(); }
class Circle  extends Shape { double area() { return Math.PI * r * r; } double r; }
class Square  extends Shape { double area() { return side * side; }      double side; }

abstract class ShapeRenderer      { abstract void render(Shape s); }
class CircleRenderer extends ShapeRenderer { void render(Shape s) { System.out.println("Drawing circle"); } }
class SquareRenderer extends ShapeRenderer { void render(Shape s) { System.out.println("Drawing square"); } }

// AFTER — rendering responsibility moves into the Shape hierarchy itself
// (or use a Visitor / Strategy pattern to keep them separate without parallel hierarchies)
abstract class Shape {
    abstract double area();
    abstract void   render();   // rendering capability pulled into Shape
}

class Circle extends Shape {
    double r;
    Circle(double r) { this.r = r; }
    public double area()   { return Math.PI * r * r; }
    public void   render() { System.out.println("Drawing circle with r=" + r); }
}

class Square extends Shape {
    double side;
    Square(double side) { this.side = side; }
    public double area()   { return side * side; }
    public void   render() { System.out.println("Drawing square with side=" + side); }
}
```

---

## See Also
- [ChangePreventersExample.java](./ChangePreventersExample.java) — compilable Java examples of all three smells
- [Refactoring Techniques → Moving Features](../../refactoring-techniques/moving-features/README.md)
- [Refactoring Techniques → Dealing with Generalization](../../refactoring-techniques/dealing-with-generalization/README.md)
