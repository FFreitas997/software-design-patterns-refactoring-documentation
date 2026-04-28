import java.util.Arrays;
import java.util.Set;

/**
 * ComposingMethods.java
 *
 * Before/after Java examples for all nine Composing Methods refactoring techniques:
 *
 *   1.  Extract Method
 *   2.  Inline Method
 *   3.  Extract Variable
 *   4.  Inline Temp
 *   5.  Replace Temp with Query
 *   6.  Split Temporary Variable
 *   7.  Remove Assignments to Parameters
 *   8.  Replace Method with Method Object
 *   9.  Substitute Algorithm
 */
public class ComposingMethods {

    // =========================================================================
    // 1. EXTRACT METHOD
    // =========================================================================

    // BEFORE: banner printing and detail printing are inline in one method.
    static void printOwingBefore(String name, double amount) {
        System.out.println("*****");
        System.out.println("* Customer Owes *");
        System.out.println("*****");
        System.out.println("name: " + name);
        System.out.println("amount: " + amount);
    }

    // AFTER: each logical step is a named method.
    static void printOwingAfter(String name, double amount) {
        printBanner();
        printDetails(name, amount);
    }
    private static void printBanner() {
        System.out.println("***** Customer Owes *****");
    }
    private static void printDetails(String name, double amount) {
        System.out.println("name: " + name + ", amount: " + amount);
    }

    // =========================================================================
    // 2. INLINE METHOD
    // =========================================================================

    // BEFORE: indirection through moreThanFiveLateDeliveries adds no clarity.
    static class RatingBefore {
        int numberOfLateDeliveries;
        RatingBefore(int n) { this.numberOfLateDeliveries = n; }
        int getRating() { return moreThanFiveLateDeliveries() ? 2 : 1; }
        boolean moreThanFiveLateDeliveries() { return numberOfLateDeliveries > 5; }
    }

    // AFTER: inlined — the condition is readable without the helper method.
    static class RatingAfter {
        int numberOfLateDeliveries;
        RatingAfter(int n) { this.numberOfLateDeliveries = n; }
        int getRating() { return numberOfLateDeliveries > 5 ? 2 : 1; }
    }

    // =========================================================================
    // 3. EXTRACT VARIABLE
    // =========================================================================

    static class OrderEB {
        final int    quantity;
        final double itemPrice;
        OrderEB(int q, double p) { quantity = q; itemPrice = p; }
    }

    // BEFORE: one inscrutable arithmetic expression.
    static double priceBefore(OrderEB order) {
        return order.quantity * order.itemPrice
               - Math.max(0, order.quantity - 500) * order.itemPrice * 0.05
               + Math.min(order.quantity * order.itemPrice * 0.1, 100.0);
    }

    // AFTER: three named intermediate values make the calculation self-documenting.
    static double priceAfter(OrderEB order) {
        double basePrice        = order.quantity * order.itemPrice;
        double quantityDiscount = Math.max(0, order.quantity - 500) * order.itemPrice * 0.05;
        double shipping         = Math.min(basePrice * 0.1, 100.0);
        return basePrice - quantityDiscount + shipping;
    }

    // =========================================================================
    // 4. INLINE TEMP
    // =========================================================================

    // BEFORE: temp variable adds no clarity.
    static boolean isExpensiveBefore(OrderEB order) {
        double basePrice = order.quantity * order.itemPrice;
        return basePrice > 1000;
    }

    // AFTER: expression used directly.
    static boolean isExpensiveAfter(OrderEB order) {
        return order.quantity * order.itemPrice > 1000;
    }

    // =========================================================================
    // 5. REPLACE TEMP WITH QUERY
    // =========================================================================

    static class OrderRTQ {
        int    quantity;
        double unitPrice;
        OrderRTQ(int q, double p) { quantity = q; unitPrice = p; }

        // BEFORE: basePrice is a local variable repeated in conditions.
        double calculateTotalBefore() {
            double basePrice = quantity * unitPrice;
            if (basePrice > 1000) return basePrice * 0.95;
            return basePrice * 0.98;
        }

        // AFTER: basePrice becomes a query method — reusable and testable.
        double calculateTotalAfter() {
            if (basePrice() > 1000) return basePrice() * 0.95;
            return basePrice() * 0.98;
        }
        private double basePrice() { return quantity * unitPrice; }
    }

    // =========================================================================
    // 6. SPLIT TEMPORARY VARIABLE
    // =========================================================================

    // BEFORE: 'temp' reused for two completely different values.
    static void geometryBefore(double height, double width) {
        double temp = 2 * (height + width);
        System.out.println("Perimeter: " + temp);
        temp = height * width;                    // reuse — confusing!
        System.out.println("Area: " + temp);
    }

    // AFTER: separate, well-named variables.
    static void geometryAfter(double height, double width) {
        double perimeter = 2 * (height + width);
        System.out.println("Perimeter: " + perimeter);
        double area = height * width;
        System.out.println("Area: " + area);
    }

    // =========================================================================
    // 7. REMOVE ASSIGNMENTS TO PARAMETERS
    // =========================================================================

    // BEFORE: parameter 'inputValue' is mutated — confusing at the call site.
    static int discountBefore(int inputValue, int quantity) {
        if (inputValue > 50) inputValue -= 2; // mutates parameter
        if (quantity   > 100) inputValue -= 1;
        return inputValue;
    }

    // AFTER: separate local result variable; parameter is never modified.
    static int discountAfter(int inputValue, int quantity) {
        int result = inputValue;
        if (inputValue > 50) result -= 2;
        if (quantity   > 100) result -= 1;
        return result;
    }

    // =========================================================================
    // 8. REPLACE METHOD WITH METHOD OBJECT
    // =========================================================================

    // BEFORE: a complex method whose locals are interdependent.
    static class OrderMO {
        final double baseCharge, itemCount, unitPrice;
        OrderMO(double b, double c, double u) { baseCharge = b; itemCount = c; unitPrice = u; }

        // Long method — locals primaryBase and secondaryBase are interdependent.
        double priceBefore() {
            double primaryBase    = baseCharge + itemCount * unitPrice;
            double secondaryBase  = primaryBase > 100 ? primaryBase * 0.10 : 0;
            double importantThing = primaryBase + secondaryBase;
            return importantThing;
        }
    }

    // AFTER: extracted into a dedicated PriceCalculator class.
    static class PriceCalculator {
        private final OrderMO order;
        private double primaryBase;
        private double secondaryBase;

        PriceCalculator(OrderMO order) { this.order = order; }

        double compute() {
            primaryBase   = calculatePrimaryBase();
            secondaryBase = calculateSecondaryBase();
            return primaryBase + secondaryBase;
        }

        private double calculatePrimaryBase()   { return order.baseCharge + order.itemCount * order.unitPrice; }
        private double calculateSecondaryBase() { return primaryBase > 100 ? primaryBase * 0.10 : 0; }
    }

    // =========================================================================
    // 9. SUBSTITUTE ALGORITHM
    // =========================================================================

    // BEFORE: manual iteration through an array.
    static String findPersonBefore(String[] people) {
        for (String person : people) {
            if (person.equals("Don"))  return "Don";
            if (person.equals("John")) return "John";
            if (person.equals("Kent")) return "Kent";
        }
        return "";
    }

    // AFTER: cleaner Set-lookup algorithm.
    static String findPersonAfter(String[] people) {
        Set<String> candidates = Set.of("Don", "John", "Kent");
        return Arrays.stream(people)
                .filter(candidates::contains)
                .findFirst()
                .orElse("");
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Extract Method ===");
        printOwingAfter("Alice", 250.00);

        System.out.println("\n=== 2. Inline Method ===");
        System.out.println("Rating (6 late): " + new RatingAfter(6).getRating());
        System.out.println("Rating (3 late): " + new RatingAfter(3).getRating());

        System.out.println("\n=== 3. Extract Variable ===");
        OrderEB order = new OrderEB(600, 5.0);
        System.out.printf("Price (before): %.2f%n", priceBefore(order));
        System.out.printf("Price (after) : %.2f%n", priceAfter(order));

        System.out.println("\n=== 4. Inline Temp ===");
        System.out.println("Expensive? " + isExpensiveAfter(new OrderEB(300, 4.0)));

        System.out.println("\n=== 5. Replace Temp with Query ===");
        OrderRTQ rtq = new OrderRTQ(200, 8.0);
        System.out.printf("Total: %.2f%n", rtq.calculateTotalAfter());

        System.out.println("\n=== 6. Split Temporary Variable ===");
        geometryAfter(4.0, 6.0);

        System.out.println("\n=== 7. Remove Assignments to Parameters ===");
        System.out.println("Discount result: " + discountAfter(55, 110));

        System.out.println("\n=== 8. Replace Method with Method Object ===");
        OrderMO orderMO = new OrderMO(20, 10, 9.5);
        System.out.printf("Price (before): %.2f%n", orderMO.priceBefore());
        System.out.printf("Price (after) : %.2f%n", new PriceCalculator(orderMO).compute());

        System.out.println("\n=== 9. Substitute Algorithm ===");
        String[] people = {"Alice", "Kent", "Bob", "Don"};
        System.out.println("Found (before): " + findPersonBefore(people));
        System.out.println("Found (after) : " + findPersonAfter(people));
    }
}
