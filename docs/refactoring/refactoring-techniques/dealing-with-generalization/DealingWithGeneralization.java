import java.util.Vector;

/**
 * DealingWithGeneralization.java
 *
 * Before/after Java examples for all twelve Dealing with Generalization
 * refactoring techniques:
 *
 *   1.  Pull Up Field
 *   2.  Pull Up Method
 *   3.  Pull Up Constructor Body
 *   4.  Push Down Method
 *   5.  Push Down Field
 *   6.  Extract Subclass
 *   7.  Extract Superclass
 *   8.  Extract Interface
 *   9.  Collapse Hierarchy
 *  10.  Form Template Method
 *  11.  Replace Inheritance with Delegation
 *  12.  Replace Delegation with Inheritance
 */
public class DealingWithGeneralization {

    // =========================================================================
    // 1. PULL UP FIELD
    // =========================================================================

    // BEFORE: 'name' duplicated in both subclasses
    static class SalespersonBefore { private String name; }
    static class EngineerBefore    { private String name; }

    // AFTER: 'name' lives once in the superclass
    static class EmployeePUF { protected String name; EmployeePUF(String n) { this.name = n; } }
    static class SalespersonPUF extends EmployeePUF { SalespersonPUF(String n) { super(n); } }
    static class EngineerPUF    extends EmployeePUF { EngineerPUF(String n)    { super(n); } }

    // =========================================================================
    // 2. PULL UP METHOD
    // =========================================================================

    // BEFORE: identical createBill() in both subclasses
    static abstract class PartyBefore {}
    static class CustomerBefore extends PartyBefore {
        String createBill() { return "Bill for customer"; }
    }
    static class SupplierBefore extends PartyBefore {
        String createBill() { return "Bill for customer"; } // identical
    }

    // AFTER: createBill() pulled up to Party
    static class PartyAfter {
        String createBill() { return "Bill for party"; } // single implementation
    }
    static class CustomerAfterPUM extends PartyAfter {}
    static class SupplierAfterPUM extends PartyAfter {}

    // =========================================================================
    // 3. PULL UP CONSTRUCTOR BODY
    // =========================================================================

    // BEFORE: identical constructor code in each subclass
    static abstract class EmployeePUCBefore {
        protected String name, id;
    }
    static class ManagerBefore extends EmployeePUCBefore {
        ManagerBefore(String name, String id) {
            this.name = name; // duplicated
            this.id   = id;   // duplicated
        }
    }

    // AFTER: common body in superclass constructor; subclasses call super()
    static class EmployeePUCAfter {
        protected String name, id;
        EmployeePUCAfter(String name, String id) { this.name = name; this.id = id; }
    }
    static class ManagerAfter extends EmployeePUCAfter {
        ManagerAfter(String name, String id) { super(name, id); }
    }

    // =========================================================================
    // 4. PUSH DOWN METHOD
    // =========================================================================

    // BEFORE: reportSales() on the superclass is only relevant to Salesperson
    static class EmployeePDMBefore {
        void reportSales() { System.out.println("Sales report"); } // in wrong class
    }
    static class SalespersonPDMBefore extends EmployeePDMBefore {}
    static class EngineerPDMBefore    extends EmployeePDMBefore {} // inherits method it doesn't need

    // AFTER: reportSales() pushed down to Salesperson
    static class EmployeePDMAfter {}
    static class SalespersonPDMAfter extends EmployeePDMAfter {
        void reportSales() { System.out.println("Sales report"); } // where it belongs
    }
    static class EngineerPDMAfter extends EmployeePDMAfter {}     // unaffected

    // =========================================================================
    // 5. PUSH DOWN FIELD
    // =========================================================================

    // BEFORE: salesQuota on superclass is only used by Salesperson
    static class EmployeePDFBefore { protected String salesQuota; } // pollutes the superclass

    // AFTER: pushed down to the subclass that uses it
    static class EmployeePDFAfter {}
    static class SalespersonPDFAfter extends EmployeePDFAfter {
        private String salesQuota; // correct home
        void setQuota(String q) { this.salesQuota = q; }
        String getQuota() { return salesQuota; }
    }

    // =========================================================================
    // 6. EXTRACT SUBCLASS
    // =========================================================================

    // BEFORE: JobItem conditionally acts differently based on isLabor flag
    static class JobItemBefore {
        private double unitPrice;
        private boolean isLabor;
        private double laborRate;

        JobItemBefore(double unitPrice) { this.unitPrice = unitPrice; isLabor = false; }
        JobItemBefore(double laborRate, boolean isLabor) { this.laborRate = laborRate; this.isLabor = isLabor; }

        double getUnitPrice() { return isLabor ? laborRate : unitPrice; }
    }

    // AFTER: LaborItem extracted as a subclass; the conditional disappears
    static class JobItemAfter {
        private final double unitPrice;
        JobItemAfter(double unitPrice) { this.unitPrice = unitPrice; }
        double getUnitPrice() { return unitPrice; }
    }

    static class LaborItem extends JobItemAfter {
        private final double laborRate;
        LaborItem(double laborRate) { super(0); this.laborRate = laborRate; }
        @Override double getUnitPrice() { return laborRate; }
    }

    // =========================================================================
    // 7. EXTRACT SUPERCLASS
    // =========================================================================

    // BEFORE: Department and Employee both have a name and annual cost
    static class DepartmentBefore {
        String name;
        double totalAnnualCost() { return 100_000; }
    }
    static class EmployeeESBefore {
        String name;
        double annualCost() { return 60_000; }
    }

    // AFTER: common elements extracted into abstract Party superclass
    static abstract class Party {
        protected String name;
        Party(String name) { this.name = name; }
        abstract double annualCost();
    }
    static class DepartmentAfterES extends Party {
        DepartmentAfterES(String name) { super(name); }
        @Override double annualCost() { return 100_000; }
    }
    static class EmployeeAfterES extends Party {
        EmployeeAfterES(String name) { super(name); }
        @Override double annualCost() { return 60_000; }
    }

    // =========================================================================
    // 8. EXTRACT INTERFACE
    // =========================================================================

    // BEFORE: TimeSheet depends on all of Employee
    static class EmployeeEI {
        String name;
        double rate() { return 500; }
        int billableWeeks() { return 48; }
        void  doAdminWork() { /* not needed by TimeSheet */ }
    }

    static class TimeSheetBefore {
        double charge(EmployeeEI e, int weeks) { return e.rate() * weeks; }
    }

    // AFTER: TimeSheet depends only on the Billable interface
    interface Billable {
        double rate();
        int    billableWeeks();
    }

    static class EmployeeEIAfter implements Billable {
        public double rate()          { return 500; }
        public int    billableWeeks() { return 48;  }
        void doAdminWork()            { /* not part of interface */ }
    }

    static class TimeSheetAfter {
        double charge(Billable b, int weeks) { return b.rate() * weeks; }
    }

    // =========================================================================
    // 9. COLLAPSE HIERARCHY
    // =========================================================================

    // BEFORE: SalesEmployee adds nothing useful beyond Employee
    static class EmployeeCH    { String name; double salary; }
    static class SalesEmployee extends EmployeeCH { } // empty subclass — dispensable

    // AFTER: collapsed — SalesEmployee deleted; use Employee directly

    // =========================================================================
    // 10. FORM TEMPLATE METHOD
    // =========================================================================

    // BEFORE: two subclasses implement the same algorithmic skeleton differently
    static class ResidencyBefore {
        void generate() {
            System.out.println("[Residency] Title");
            System.out.println("[Residency] Body with specific lease terms");
            System.out.println("[Residency] Footer");
        }
    }
    static class LeaseBefore {
        void generate() {
            System.out.println("[Lease] Title");
            System.out.println("[Lease] Body with commercial terms");
            System.out.println("[Lease] Footer");
        }
    }

    // AFTER: Template Method pattern — skeleton in superclass, hooks in subclasses
    static abstract class Statement {
        final void generate() {   // template method — invariant skeleton
            printTitle();
            printBody();
            printFooter();
        }
        abstract void printTitle();
        abstract void printBody();
        void printFooter() { System.out.println("[Statement] Standard footer"); }
    }

    static class Residency extends Statement {
        void printTitle()  { System.out.println("[Residency] Title"); }
        void printBody()   { System.out.println("[Residency] Specific residential terms"); }
    }

    static class Lease extends Statement {
        void printTitle()  { System.out.println("[Lease] Title"); }
        void printBody()   { System.out.println("[Lease] Commercial terms"); }
        @Override void printFooter() { System.out.println("[Lease] Commercial footer"); }
    }

    // =========================================================================
    // 11. REPLACE INHERITANCE WITH DELEGATION
    // =========================================================================

    // BEFORE: Stack extends Vector — violates "is-a" semantics
    static class StackBefore extends Vector<Object> {
        public Object push(Object element) { addElement(element); return element; }
        public Object pop() {
            Object top = lastElement();
            removeElementAt(size() - 1);
            return top;
        }
    }

    // AFTER: Stack composes a Vector — delegation instead of inheritance
    static class StackAfter {
        private final Vector<Object> storage = new Vector<>();
        public Object push(Object element) { storage.addElement(element); return element; }
        public Object pop() {
            Object top = storage.lastElement();
            storage.removeElementAt(storage.size() - 1);
            return top;
        }
        public int  size()  { return storage.size(); }
        public boolean isEmpty() { return storage.isEmpty(); }
    }

    // =========================================================================
    // 12. REPLACE DELEGATION WITH INHERITANCE
    // =========================================================================

    // BEFORE: PersonDelegator forwards every method call to Person
    static class PersonBase {
        String getName()  { return "Alice"; }
        String getPhone() { return "555-1234"; }
    }

    static class EmployeeDelegatesBefore {
        private final PersonBase person = new PersonBase();
        String getName()  { return person.getName();  } // pure delegation
        String getPhone() { return person.getPhone(); } // pure delegation
    }

    // AFTER: Employee extends Person directly — no delegation boilerplate needed
    static class EmployeeExtendsAfter extends PersonBase {
        // Inherits getName() and getPhone() — delegation code deleted
        String getRole() { return "Engineer"; }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Pull Up Field ===");
        SalespersonPUF sp = new SalespersonPUF("Alice");
        EngineerPUF    en = new EngineerPUF("Bob");
        System.out.println("Names: " + sp.name + ", " + en.name);

        System.out.println("\n=== 2. Pull Up Method ===");
        System.out.println(new CustomerAfterPUM().createBill());
        System.out.println(new SupplierAfterPUM().createBill());

        System.out.println("\n=== 3. Pull Up Constructor Body ===");
        ManagerAfter mgr = new ManagerAfter("Carol", "MGR-1");
        System.out.println("Manager: " + mgr.name + " / " + mgr.id);

        System.out.println("\n=== 4. Push Down Method ===");
        new SalespersonPDMAfter().reportSales();

        System.out.println("\n=== 5. Push Down Field ===");
        SalespersonPDFAfter s = new SalespersonPDFAfter();
        s.setQuota("Q4-2024");
        System.out.println("Quota: " + s.getQuota());

        System.out.println("\n=== 6. Extract Subclass ===");
        JobItemAfter part  = new JobItemAfter(25.0);
        LaborItem    labor = new LaborItem(75.0);
        System.out.println("Part price : " + part.getUnitPrice());
        System.out.println("Labor rate : " + labor.getUnitPrice());

        System.out.println("\n=== 7. Extract Superclass ===");
        Party[] parties = {
            new DepartmentAfterES("Engineering"),
            new EmployeeAfterES("Dave")
        };
        for (Party p : parties) {
            System.out.printf("%s annual cost: %.0f%n", p.name, p.annualCost());
        }

        System.out.println("\n=== 8. Extract Interface ===");
        TimeSheetAfter ts = new TimeSheetAfter();
        Billable emp = new EmployeeEIAfter();
        System.out.printf("Charge for 4 weeks: %.2f%n", ts.charge(emp, 4));

        System.out.println("\n=== 9. Collapse Hierarchy ===");
        EmployeeCH collapsed = new EmployeeCH(); // SalesEmployee absorbed into Employee
        collapsed.name   = "Eve";
        collapsed.salary = 55000;
        System.out.println("Employee: " + collapsed.name + " salary=" + collapsed.salary);

        System.out.println("\n=== 10. Form Template Method ===");
        new Residency().generate();
        System.out.println("---");
        new Lease().generate();

        System.out.println("\n=== 11. Replace Inheritance with Delegation ===");
        StackAfter stack = new StackAfter();
        stack.push("first");
        stack.push("second");
        System.out.println("Pop: " + stack.pop());
        System.out.println("Size: " + stack.size());

        System.out.println("\n=== 12. Replace Delegation with Inheritance ===");
        EmployeeExtendsAfter eea = new EmployeeExtendsAfter();
        System.out.println(eea.getName() + " / " + eea.getPhone() + " / " + eea.getRole());
    }
}
