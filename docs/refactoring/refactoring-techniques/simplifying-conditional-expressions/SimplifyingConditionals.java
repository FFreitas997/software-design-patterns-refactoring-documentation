import java.util.List;

/**
 * SimplifyingConditionals.java
 *
 * Before/after Java examples for all eight Simplifying Conditional Expressions
 * refactoring techniques:
 *
 *   1. Decompose Conditional
 *   2. Consolidate Conditional Expression
 *   3. Consolidate Duplicate Conditional Fragments
 *   4. Remove Control Flag
 *   5. Replace Nested Conditional with Guard Clauses
 *   6. Replace Conditional with Polymorphism
 *   7. Introduce Null Object
 *   8. Introduce Assertion
 */
public class SimplifyingConditionals {

    // =========================================================================
    // 1. DECOMPOSE CONDITIONAL
    // =========================================================================

    static final int SUMMER_START_MONTH = 6;
    static final int SUMMER_END_MONTH   = 8;
    static final double SUMMER_RATE     = 2.0;
    static final double WINTER_RATE     = 3.0;
    static final double WINTER_SERVICE  = 5.0;

    // BEFORE: long condition and branches inline.
    static double chargeBefore(int month, int quantity) {
        if (month < SUMMER_START_MONTH || month > SUMMER_END_MONTH) {
            return quantity * WINTER_RATE + WINTER_SERVICE;
        } else {
            return quantity * SUMMER_RATE;
        }
    }

    // AFTER: condition and branches are extracted into named methods.
    static boolean isSummer(int month) { return month >= SUMMER_START_MONTH && month <= SUMMER_END_MONTH; }
    static double summerCharge(int q)  { return q * SUMMER_RATE; }
    static double winterCharge(int q)  { return q * WINTER_RATE + WINTER_SERVICE; }

    static double chargeAfter(int month, int quantity) {
        return isSummer(month) ? summerCharge(quantity) : winterCharge(quantity);
    }

    // =========================================================================
    // 2. CONSOLIDATE CONDITIONAL EXPRESSION
    // =========================================================================

    static class Employee2 {
        int seniority, monthsDisabled;
        boolean isPartTime;
        Employee2(int sen, int months, boolean part) {
            this.seniority = sen; this.monthsDisabled = months; this.isPartTime = part;
        }

        // BEFORE: three separate conditions all returning 0.
        double disabilityAmountBefore() {
            if (seniority < 2)          return 0;
            if (monthsDisabled > 12)    return 0;
            if (isPartTime)             return 0;
            return seniority * 100.0;
        }

        // AFTER: consolidated into a single named predicate.
        boolean isNotEligibleForDisability() {
            return seniority < 2 || monthsDisabled > 12 || isPartTime;
        }
        double disabilityAmountAfter() {
            if (isNotEligibleForDisability()) return 0;
            return seniority * 100.0;
        }
    }

    // =========================================================================
    // 3. CONSOLIDATE DUPLICATE CONDITIONAL FRAGMENTS
    // =========================================================================

    // Dummy helper
    static void send(double total) { System.out.println("Sending invoice for " + total); }

    // BEFORE: send() is duplicated in both branches.
    static double checkoutBefore(double price, boolean isSpecialDeal) {
        double total;
        if (isSpecialDeal) {
            total = price * 0.95;
            send(total);   // duplicated
        } else {
            total = price * 0.98;
            send(total);   // duplicated
        }
        return total;
    }

    // AFTER: send() moved outside the conditional.
    static double checkoutAfter(double price, boolean isSpecialDeal) {
        double total = isSpecialDeal ? price * 0.95 : price * 0.98;
        send(total);   // called once, unconditionally
        return total;
    }

    // =========================================================================
    // 4. REMOVE CONTROL FLAG
    // =========================================================================

    // BEFORE: boolean flag 'found' used as a control mechanism.
    static void checkNamesBefore(List<String> names) {
        boolean found = false;
        for (String name : names) {
            if (!found) {
                if (name.equals("Don") || name.equals("John")) {
                    System.out.println("Alert for: " + name);
                    found = true;
                }
            }
        }
    }

    // AFTER: break provides the natural exit — no flag needed.
    static void checkNamesAfter(List<String> names) {
        for (String name : names) {
            if (name.equals("Don") || name.equals("John")) {
                System.out.println("Alert for: " + name);
                break;
            }
        }
    }

    // =========================================================================
    // 5. REPLACE NESTED CONDITIONAL WITH GUARD CLAUSES
    // =========================================================================

    static class PayrollBefore {
        boolean isDead, isSeparated, isRetired;

        // BEFORE: normal case buried under three levels of nesting.
        double getPayAmount() {
            double result;
            if (isDead) {
                result = 0;
            } else {
                if (isSeparated) {
                    result = 500;
                } else {
                    if (isRetired) {
                        result = 1000;
                    } else {
                        result = 3000;
                    }
                }
            }
            return result;
        }
    }

    static class PayrollAfter {
        boolean isDead, isSeparated, isRetired;

        // AFTER: guard clauses handle special cases; normal case is obvious.
        double getPayAmount() {
            if (isDead)      return 0;
            if (isSeparated) return 500;
            if (isRetired)   return 1000;
            return 3000;
        }
    }

    // =========================================================================
    // 6. REPLACE CONDITIONAL WITH POLYMORPHISM
    // =========================================================================

    // BEFORE: switch on a type string.
    static double getSpeedBefore(String type) {
        switch (type) {
            case "EUROPEAN":      return 35.0;
            case "AFRICAN":       return 25.0;
            case "NORWEGIAN_BLUE": return 0.0;
            default: throw new IllegalArgumentException("Unknown bird: " + type);
        }
    }

    // AFTER: each bird type is a class; no switch needed.
    abstract static class Bird      { abstract double getSpeed(); }
    static class European      extends Bird { public double getSpeed() { return 35.0; } }
    static class African       extends Bird { public double getSpeed() { return 25.0; } }
    static class NorwegianBlue extends Bird { public double getSpeed() { return 0.0;  } }

    // =========================================================================
    // 7. INTRODUCE NULL OBJECT
    // =========================================================================

    abstract static class Customer {
        abstract String getName();
        abstract String getPlan();
        boolean isNull() { return false; }
    }

    static class RealCustomer extends Customer {
        private final String name;
        RealCustomer(String name) { this.name = name; }
        @Override public String getName() { return name; }
        @Override public String getPlan() { return "PREMIUM"; }
    }

    // Null Object — do-nothing/default implementation instead of null.
    static class NullCustomer extends Customer {
        @Override public String  getName()  { return "Guest"; }
        @Override public String  getPlan()  { return "BASIC"; }
        @Override public boolean isNull()   { return true; }
    }

    static Customer findCustomer(String name) {
        if ("Alice".equals(name)) return new RealCustomer("Alice");
        return new NullCustomer(); // never return null
    }

    // BEFORE: null check required before every use
    static void printPlanBefore(String name) {
        Customer c = "Alice".equals(name) ? new RealCustomer(name) : null;
        String plan = (c != null) ? c.getPlan() : "BASIC";
        System.out.println(name + " → " + plan + " (before)");
    }

    // AFTER: no null check — NullCustomer handles the "not found" case
    static void printPlanAfter(String name) {
        Customer c = findCustomer(name);
        System.out.println(c.getName() + " → " + c.getPlan() + " (after)");
    }

    // =========================================================================
    // 8. INTRODUCE ASSERTION
    // =========================================================================

    static double discountRate = 0.10;

    // BEFORE: silent assumption that discountRate >= 0.
    static double applyDiscountBefore(double amount) {
        return amount - (amount * discountRate);
    }

    // AFTER: assumption made explicit with an assertion.
    static double applyDiscountAfter(double amount) {
        assert discountRate >= 0 : "Discount rate must be non-negative, was: " + discountRate;
        return amount - (amount * discountRate);
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Decompose Conditional ===");
        System.out.printf("July  charge (qty=10): %.2f%n", chargeAfter(7,  10));
        System.out.printf("Jan   charge (qty=10): %.2f%n", chargeAfter(1,  10));

        System.out.println("\n=== 2. Consolidate Conditional Expression ===");
        Employee2 emp = new Employee2(3, 6, false);
        System.out.printf("Disability amount: %.2f%n", emp.disabilityAmountAfter());

        System.out.println("\n=== 3. Consolidate Duplicate Conditional Fragments ===");
        System.out.printf("Special deal total: %.2f%n", checkoutAfter(100.0, true));

        System.out.println("\n=== 4. Remove Control Flag ===");
        checkNamesAfter(List.of("Alice", "Bob", "John", "Carol"));

        System.out.println("\n=== 5. Replace Nested Conditional with Guard Clauses ===");
        PayrollAfter p = new PayrollAfter();
        p.isRetired = true;
        System.out.println("Pay amount: " + p.getPayAmount());

        System.out.println("\n=== 6. Replace Conditional with Polymorphism ===");
        List<Bird> birds = List.of(new European(), new African(), new NorwegianBlue());
        for (Bird bird : birds) {
            System.out.printf("Speed: %.1f km/h%n", bird.getSpeed());
        }

        System.out.println("\n=== 7. Introduce Null Object ===");
        printPlanAfter("Alice");
        printPlanAfter("Unknown");

        System.out.println("\n=== 8. Introduce Assertion ===");
        System.out.printf("After discount: %.2f%n", applyDiscountAfter(200.0));
    }
}
