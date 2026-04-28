/**
 * ChangePreventersExample.java
 *
 * Demonstrates all three Change Preventer code smells
 * and their refactored counterparts.
 *
 * Smells covered:
 *   1. Divergent Change              → Extract Class (single reason to change)
 *   2. Shotgun Surgery               → Consolidate scattered logic into one place
 *   3. Parallel Inheritance Hierarchies → Collapse via composition / move method
 */
public class ChangePreventersExample {

    // =========================================================================
    // 1. DIVERGENT CHANGE — before and after
    // =========================================================================

    // Dummy model
    static class Employee {
        final String name;
        final double salary;
        Employee(String name, double salary) { this.name = name; this.salary = salary; }
    }

    // SMELL: EmployeeReportDirty changes for three independent reasons —
    //   schema changes, formatting changes, and tax rule changes.
    static class EmployeeReportDirty {
        private final Employee employee;
        EmployeeReportDirty(Employee e) { this.employee = e; }

        // Reason 1: changes when database schema changes
        void saveToDatabase() {
            System.out.println("INSERT INTO employees VALUES ('" + employee.name + "', " + employee.salary + ")");
        }

        // Reason 2: changes when report format changes
        String formatAsCsv()  { return employee.name + "," + employee.salary; }
        String formatAsHtml() { return "<tr><td>" + employee.name + "</td><td>" + employee.salary + "</td></tr>"; }

        // Reason 3: changes when tax rules change
        double calculateTax() { return employee.salary * 0.30; }
    }

    // REFACTORED: three classes, each with exactly one reason to change.
    static class EmployeeRepository {
        void save(Employee e) {
            System.out.println("INSERT INTO employees VALUES ('" + e.name + "', " + e.salary + ")");
        }
    }

    static class EmployeeFormatter {
        String toCsv(Employee e)  { return e.name + "," + e.salary; }
        String toHtml(Employee e) { return "<tr><td>" + e.name + "</td><td>" + e.salary + "</td></tr>"; }
    }

    static class EmployeeTaxCalculator {
        private static final double TAX_RATE = 0.30;
        double calculate(Employee e) { return e.salary * TAX_RATE; }
    }

    // =========================================================================
    // 2. SHOTGUN SURGERY — before and after
    // =========================================================================

    // SMELL: VAT rate (0.20) is scattered across three unrelated classes.
    //   Changing the VAT rate requires editing all three.
    static class InvoiceDirty {
        double calculateVat(double amount) { return amount * 0.20; }
    }

    static class ReceiptDirty {
        double calculateVat(double amount) { return amount * 0.20; }
    }

    static class TaxReportDirty {
        double computeVat(double amount) { return amount * 0.20; }
    }

    // REFACTORED: single authoritative source — change VAT_RATE here only.
    static class TaxPolicy {
        public static final double VAT_RATE = 0.20;
        public static double applyVat(double amount) { return amount * VAT_RATE; }
    }

    static class InvoiceClean    { double calculateVat(double amount) { return TaxPolicy.applyVat(amount); } }
    static class ReceiptClean    { double calculateVat(double amount) { return TaxPolicy.applyVat(amount); } }
    static class TaxReportClean  { double computeVat(double amount)   { return TaxPolicy.applyVat(amount); } }

    // =========================================================================
    // 3. PARALLEL INHERITANCE HIERARCHIES — before and after
    // =========================================================================

    // SMELL: two hierarchies (Shape + ShapeRenderer) grow in lockstep.
    //   Adding a Triangle shape requires adding a TriangleRenderer too.
    abstract static class ShapeDirty { abstract double area(); }
    static class CircleDirty extends ShapeDirty {
        double r;
        CircleDirty(double r) { this.r = r; }
        public double area()  { return Math.PI * r * r; }
    }
    static class SquareDirty extends ShapeDirty {
        double side;
        SquareDirty(double side) { this.side = side; }
        public double area() { return side * side; }
    }

    abstract static class ShapeRenderer       { abstract void render(ShapeDirty s); }
    static class CircleRenderer extends ShapeRenderer {
        void render(ShapeDirty s) { System.out.println("Rendering circle (area=" + s.area() + ")"); }
    }
    static class SquareRenderer extends ShapeRenderer {
        void render(ShapeDirty s) { System.out.println("Rendering square (area=" + s.area() + ")"); }
    }

    // REFACTORED: render() moves into the Shape hierarchy — one hierarchy to maintain.
    abstract static class Shape {
        abstract double area();
        abstract void   render();
    }

    static class Circle extends Shape {
        private final double r;
        Circle(double r) { this.r = r; }
        public double area()   { return Math.PI * r * r; }
        public void   render() { System.out.printf("Rendering circle r=%.1f area=%.2f%n", r, area()); }
    }

    static class Square extends Shape {
        private final double side;
        Square(double side) { this.side = side; }
        public double area()   { return side * side; }
        public void   render() { System.out.printf("Rendering square side=%.1f area=%.2f%n", side, area()); }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Divergent Change ===");
        Employee emp = new Employee("Alice", 80000);
        new EmployeeRepository().save(emp);
        System.out.println("CSV  : " + new EmployeeFormatter().toCsv(emp));
        System.out.printf("Tax  : %.2f%n", new EmployeeTaxCalculator().calculate(emp));

        System.out.println("\n=== 2. Shotgun Surgery ===");
        double orderAmount = 250.0;
        System.out.printf("Invoice VAT  : %.2f%n", new InvoiceClean().calculateVat(orderAmount));
        System.out.printf("Receipt VAT  : %.2f%n", new ReceiptClean().calculateVat(orderAmount));
        System.out.printf("Report VAT   : %.2f%n", new TaxReportClean().computeVat(orderAmount));

        System.out.println("\n=== 3. Parallel Inheritance Hierarchies ===");
        Shape[] shapes = { new Circle(5), new Square(4) };
        for (Shape shape : shapes) {
            shape.render();
        }
    }
}
