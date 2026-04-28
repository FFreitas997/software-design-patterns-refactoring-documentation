import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrganizingData.java
 *
 * Before/after Java examples for Organizing Data refactoring techniques:
 *
 *   1.  Self Encapsulate Field
 *   2.  Replace Data Value with Object
 *   3.  Change Value to Reference
 *   4.  Change Reference to Value
 *   5.  Replace Array with Object
 *   6.  Replace Magic Number with Symbolic Constant
 *   7.  Encapsulate Field
 *   8.  Encapsulate Collection
 *   9.  Replace Type Code with Class / Enum
 */
public class OrganizingData {

    // =========================================================================
    // 1. SELF ENCAPSULATE FIELD
    // =========================================================================

    // BEFORE: fields accessed directly — subclasses cannot override access.
    static class IntRangeBefore {
        int low, high;
        boolean includes(int arg) { return arg >= low && arg <= high; }
    }

    // AFTER: accessed via getters — subclass can override getLow()/getHigh().
    static class IntRangeAfter {
        private int low, high;
        IntRangeAfter(int low, int high) { this.low = low; this.high = high; }
        int getLow()  { return low; }
        int getHigh() { return high; }
        boolean includes(int arg) { return arg >= getLow() && arg <= getHigh(); }
    }

    // =========================================================================
    // 2. REPLACE DATA VALUE WITH OBJECT
    // =========================================================================

    // BEFORE: phone is a raw string — no validation, no behaviour.
    static class OrderBefore {
        String customerPhone;
        OrderBefore(String phone) { this.customerPhone = phone; }
    }

    // AFTER: TelephoneNumber value object with validation.
    static final class TelephoneNumber {
        private final String number;
        TelephoneNumber(String number) {
            if (!number.matches("\\d{3}-\\d{4}"))
                throw new IllegalArgumentException("Invalid phone format: " + number);
            this.number = number;
        }
        String getAreaCode() { return number.substring(0, 3); }
        @Override public String toString() { return number; }
    }

    static class OrderAfter {
        TelephoneNumber customerPhone;
        OrderAfter(TelephoneNumber phone) { this.customerPhone = phone; }
    }

    // =========================================================================
    // 3. CHANGE VALUE TO REFERENCE
    // =========================================================================

    // BEFORE: two separate Customer instances for the same name.
    static class CustomerValue {
        final String name;
        CustomerValue(String name) { this.name = name; }
    }

    // AFTER: registry ensures one canonical instance per name.
    static class Customer {
        private static final Map<String, Customer> REGISTRY = new HashMap<>();
        final String name;
        private Customer(String name) { this.name = name; }
        static Customer get(String name) {
            return REGISTRY.computeIfAbsent(name, Customer::new);
        }
    }

    // =========================================================================
    // 4. CHANGE REFERENCE TO VALUE
    // =========================================================================

    // BEFORE: CurrencyRef compared by identity — two "USD" objects are not equal.
    static class CurrencyRef {
        String code;
        CurrencyRef(String code) { this.code = code; }
    }

    // AFTER: immutable value object — two "USD" objects ARE equal.
    static final class Currency {
        private final String code;
        Currency(String code) { this.code = code; }
        @Override public boolean equals(Object o) {
            return o instanceof Currency && code.equals(((Currency) o).code);
        }
        @Override public int    hashCode() { return code.hashCode(); }
        @Override public String toString() { return code; }
    }

    // =========================================================================
    // 5. REPLACE ARRAY WITH OBJECT
    // =========================================================================

    // BEFORE: array where position encodes meaning — brittle and opaque.
    static String[] buildRowBefore(String team, int wins, int losses) {
        String[] row = new String[3];
        row[0] = team;
        row[1] = String.valueOf(wins);
        row[2] = String.valueOf(losses);
        return row;
    }

    // AFTER: named fields communicate intent clearly.
    static class PerformanceRecord {
        final String teamName;
        final int    wins, losses;
        PerformanceRecord(String teamName, int wins, int losses) {
            this.teamName = teamName; this.wins = wins; this.losses = losses;
        }
        @Override public String toString() {
            return teamName + ": W=" + wins + " L=" + losses;
        }
    }

    // =========================================================================
    // 6. REPLACE MAGIC NUMBER WITH SYMBOLIC CONSTANT
    // =========================================================================

    // BEFORE: 9.81 is a magic number.
    static double potentialEnergyBefore(double mass, double height) {
        return mass * height * 9.81;
    }

    // AFTER: named constant documents the concept.
    static final double GRAVITATIONAL_CONSTANT = 9.81; // m/s²

    static double potentialEnergyAfter(double mass, double height) {
        return mass * height * GRAVITATIONAL_CONSTANT;
    }

    // =========================================================================
    // 7. ENCAPSULATE FIELD
    // =========================================================================

    // BEFORE: public field — any caller can read or write without restriction.
    static class PersonBefore { public String name; }

    // AFTER: private field with controlled access methods.
    static class PersonAfter {
        private String name;
        PersonAfter(String name) { this.name = name; }
        String getName()             { return name; }
        void   setName(String name)  { this.name = (name == null ? "" : name.trim()); }
    }

    // =========================================================================
    // 8. ENCAPSULATE COLLECTION
    // =========================================================================

    // BEFORE: collection is public — callers mutate it freely.
    static class CourseBefore {
        public List<String> prerequisites = new ArrayList<>();
    }

    // AFTER: unmodifiable view returned; mutation via named methods.
    static class CourseAfter {
        private final List<String> prerequisites = new ArrayList<>();
        List<String> getPrerequisites()  { return Collections.unmodifiableList(prerequisites); }
        void addPrerequisite(String p)    { prerequisites.add(p); }
        void removePrerequisite(String p) { prerequisites.remove(p); }
    }

    // =========================================================================
    // 9. REPLACE TYPE CODE WITH CLASS / ENUM
    // =========================================================================

    // BEFORE: integer type codes with no type safety.
    static class EmployeeBefore {
        static final int ENGINEER    = 0;
        static final int SALESPERSON = 1;
        int type;
        double payBonus() {
            switch (type) {
                case ENGINEER:    return 5000;
                case SALESPERSON: return 10000;
                default: throw new IllegalArgumentException("Unknown type: " + type);
            }
        }
    }

    // AFTER: enum replaces integer codes; polymorphism can replace switch.
    enum EmployeeType {
        ENGINEER    { @Override double payBonus() { return 5000;  } },
        SALESPERSON { @Override double payBonus() { return 10000; } };
        abstract double payBonus();
    }

    static class EmployeeAfter {
        final EmployeeType type;
        EmployeeAfter(EmployeeType type) { this.type = type; }
        double payBonus() { return type.payBonus(); }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Self Encapsulate Field ===");
        IntRangeAfter range = new IntRangeAfter(1, 10);
        System.out.println("includes(5): " + range.includes(5));
        System.out.println("includes(11): " + range.includes(11));

        System.out.println("\n=== 2. Replace Data Value with Object ===");
        try {
            TelephoneNumber phone = new TelephoneNumber("415-1234");
            System.out.println("Phone: " + phone + ", area: " + phone.getAreaCode());
            new TelephoneNumber("invalid"); // should throw
        } catch (IllegalArgumentException e) {
            System.out.println("Validation caught: " + e.getMessage());
        }

        System.out.println("\n=== 3. Change Value to Reference ===");
        Customer c1 = Customer.get("Alice");
        Customer c2 = Customer.get("Alice");
        System.out.println("Same instance: " + (c1 == c2));

        System.out.println("\n=== 4. Change Reference to Value ===");
        Currency usd1 = new Currency("USD");
        Currency usd2 = new Currency("USD");
        System.out.println("Currencies equal: " + usd1.equals(usd2));

        System.out.println("\n=== 5. Replace Array with Object ===");
        PerformanceRecord record = new PerformanceRecord("Liverpool", 15, 3);
        System.out.println(record);

        System.out.println("\n=== 6. Replace Magic Number with Symbolic Constant ===");
        System.out.printf("Potential energy (5kg, 10m): %.2f J%n", potentialEnergyAfter(5, 10));

        System.out.println("\n=== 7. Encapsulate Field ===");
        PersonAfter person = new PersonAfter("Bob");
        person.setName("  Charlie  ");
        System.out.println("Name: '" + person.getName() + "'");

        System.out.println("\n=== 8. Encapsulate Collection ===");
        CourseAfter course = new CourseAfter();
        course.addPrerequisite("Mathematics");
        course.addPrerequisite("Physics");
        System.out.println("Prerequisites: " + course.getPrerequisites());

        System.out.println("\n=== 9. Replace Type Code with Enum ===");
        EmployeeAfter engineer    = new EmployeeAfter(EmployeeType.ENGINEER);
        EmployeeAfter salesperson = new EmployeeAfter(EmployeeType.SALESPERSON);
        System.out.printf("Engineer bonus   : %.0f%n", engineer.payBonus());
        System.out.printf("Salesperson bonus: %.0f%n", salesperson.payBonus());
    }
}
