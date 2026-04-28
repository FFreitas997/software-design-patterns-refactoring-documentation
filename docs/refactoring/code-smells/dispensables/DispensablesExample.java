import java.util.List;

/**
 * DispensablesExample.java
 *
 * Demonstrates all six Dispensable code smells and their refactored counterparts.
 *
 * Smells covered:
 *   1. Excessive Comments      → Rename / Extract Method so code is self-explanatory
 *   2. Duplicate Code          → Extract shared method / utility
 *   3. Lazy Class              → Inline Class
 *   4. Data Class              → Move behaviour into the class
 *   5. Dead Code               → Delete unreachable/unused code
 *   6. Speculative Generality  → YAGNI — remove unused abstractions
 */
public class DispensablesExample {

    // =========================================================================
    // 1. EXCESSIVE COMMENTS — before and after
    // =========================================================================

    // SMELL: poor name compensated by a comment; magic number unexplained.
    /** Get the number of days between two timestamps */
    static int d(long t1, long t2) {
        // divide by milliseconds in a day
        return (int) ((t2 - t1) / 86400000);
    }

    // REFACTORED: self-documenting name and named constant — no comment needed.
    private static final long MILLIS_PER_DAY = 86_400_000L;

    static int daysBetween(long startMillis, long endMillis) {
        return (int) ((endMillis - startMillis) / MILLIS_PER_DAY);
    }

    // =========================================================================
    // 2. DUPLICATE CODE — before and after
    // =========================================================================

    // SMELL: identical tax calculation in two sibling classes.
    static class OnlineOrderDirty {
        double total(double subtotal) { return subtotal + subtotal * 0.15; }
    }

    static class InStoreOrderDirty {
        double total(double subtotal) { return subtotal + subtotal * 0.15; } // exact duplicate
    }

    // REFACTORED: single authoritative source.
    static class TaxCalculator {
        static final double TAX_RATE = 0.15;
        static double withTax(double subtotal) { return subtotal * (1 + TAX_RATE); }
    }

    static class OnlineOrder  { double total(double sub) { return TaxCalculator.withTax(sub); } }
    static class InStoreOrder { double total(double sub) { return TaxCalculator.withTax(sub); } }

    // =========================================================================
    // 3. LAZY CLASS — before and after
    // =========================================================================

    // SMELL: TemperatureConverter does one trivial thing and exists in exactly one place.
    static class TemperatureConverter {
        static double toCelsius(double fahrenheit) { return (fahrenheit - 32) * 5.0 / 9.0; }
    }

    static class WeatherReportDirty {
        void display(double tempF) {
            System.out.println(TemperatureConverter.toCelsius(tempF) + "°C");
        }
    }

    // REFACTORED: inline the conversion — no separate class needed.
    static class WeatherReport {
        void display(double tempF) {
            double celsius = (tempF - 32) * 5.0 / 9.0;
            System.out.printf("%.1f°C%n", celsius);
        }
    }

    // =========================================================================
    // 4. DATA CLASS — before and after
    // =========================================================================

    // SMELL: CustomerData has no behaviour; callers compute things it should own.
    static class CustomerData {
        String firstName, lastName, email;
    }

    static class OrderServiceDirty {
        // External code knows too much about CustomerData's internals.
        String getFullName(CustomerData c) { return c.firstName + " " + c.lastName; }
    }

    // REFACTORED: behaviour lives with the data.
    static class Customer {
        private final String firstName, lastName, email;

        Customer(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName  = lastName;
            this.email     = email;
        }

        public String getFullName() { return firstName + " " + lastName; }
        public String getEmail()    { return email; }
    }

    // =========================================================================
    // 5. DEAD CODE — before and after
    // =========================================================================

    // SMELL: unusedCounter is never read; doOldWork() is never called; the else
    // branch can never be reached because flag is always true at the one call site.
    static class ProcessorDirty {
        private int unusedCounter = 0; // dead field

        void process(boolean flag) {
            if (flag) {
                System.out.println("Working (dirty)");
            } else {
                doOldWork(); // dead branch — flag is always true
            }
        }

        private void doOldWork() { System.out.println("Old work — never called"); } // dead method
    }

    // REFACTORED: dead code deleted — code is shorter and clearer.
    static class ProcessorClean {
        void process() {
            System.out.println("Working (clean)");
        }
    }

    // =========================================================================
    // 6. SPECULATIVE GENERALITY — before and after
    // =========================================================================

    // SMELL: a generic interface and unused parameters added "just in case".
    interface DataProcessor<T, R> {
        R process(T input, Object ctx, Object opts); // ctx and opts are always null
    }

    static class CsvDataProcessorDirty implements DataProcessor<String, List<String>> {
        public List<String> process(String input, Object ctx, Object opts) {
            return List.of(input.split(",")); // ctx and opts ignored
        }
    }

    // REFACTORED: simple, concrete class — no unnecessary abstraction.
    static class CsvParser {
        List<String> parse(String csvLine) {
            return List.of(csvLine.split(","));
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Excessive Comments ===");
        long now   = System.currentTimeMillis();
        long week  = now + 7L * MILLIS_PER_DAY;
        System.out.println("Days in a week: " + daysBetween(now, week));

        System.out.println("\n=== 2. Duplicate Code ===");
        System.out.printf("Online total : %.2f%n", new OnlineOrder().total(100));
        System.out.printf("InStore total: %.2f%n", new InStoreOrder().total(100));

        System.out.println("\n=== 3. Lazy Class ===");
        new WeatherReport().display(98.6);

        System.out.println("\n=== 4. Data Class ===");
        Customer customer = new Customer("Jane", "Doe", "jane@example.com");
        System.out.println("Full name: " + customer.getFullName());

        System.out.println("\n=== 5. Dead Code ===");
        new ProcessorClean().process();

        System.out.println("\n=== 6. Speculative Generality ===");
        CsvParser parser = new CsvParser();
        System.out.println("Parsed: " + parser.parse("apple,banana,cherry"));
    }
}
