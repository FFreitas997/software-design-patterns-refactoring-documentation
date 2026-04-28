import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CleanCodeExample.java
 *
 * This class implements the same functionality as DirtyCodeExample.java
 * but applies clean code principles throughout:
 *
 *   - Meaningful names for all classes, methods, fields, and variables
 *   - Named constants instead of magic numbers
 *   - Short, focused methods (Single Responsibility)
 *   - No duplicated logic
 *   - Flat structure using guard clauses instead of deep nesting
 *   - Proper encapsulation (private fields, public methods)
 *   - Separation of concerns (calculation ≠ output)
 */
public class CleanCodeExample {

    // Named constants make the business rules self-documenting.
    private static final double PREMIUM_HIGH_VOLUME_DISCOUNT = 0.20;
    private static final double PREMIUM_STANDARD_DISCOUNT    = 0.10;
    private static final double STANDARD_HIGH_VOLUME_DISCOUNT = 0.15;
    private static final double TAX_RATE                     = 0.15;
    private static final int    PREMIUM_HIGH_VOLUME_THRESHOLD  = 500;
    private static final int    STANDARD_HIGH_VOLUME_THRESHOLD = 1000;
    private static final int    INVENTORY_PASSES_PER_ITEM     = 3;

    // Private fields with meaningful names — encapsulation enforced.
    private final double basePrice;
    private final int    loyaltyPoints;
    private final String customerName;
    private final List<String> inventoryItems;

    public CleanCodeExample(double basePrice, int loyaltyPoints, String customerName) {
        this.basePrice      = basePrice;
        this.loyaltyPoints  = loyaltyPoints;
        this.customerName   = customerName;
        this.inventoryItems = loadInventoryItems();
    }

    /** Initialises inventory with default items — separate from constructor logic. */
    private List<String> loadInventoryItems() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add("item" + i);
        }
        return items;
    }

    // -------------------------------------------------------------------------
    // Order calculation — each concern has its own focused method
    // -------------------------------------------------------------------------

    /**
     * Calculates the final price for an order, applying discount and optionally tax.
     *
     * @param customerType  "PREMIUM" or "STANDARD"
     * @param applyTax      whether to add the standard tax rate
     * @param processInventory whether to run the inventory processing pass
     * @return the final price after discount and tax
     */
    public double calculateOrderTotal(String customerType, boolean applyTax, boolean processInventory) {
        double discountRate = resolveDiscountRate(customerType);
        double discountedPrice = applyDiscount(basePrice, discountRate);
        double priceWithTax = applyTax ? addTax(discountedPrice) : discountedPrice;

        printOrderSummary(customerType, discountRate, discountedPrice, priceWithTax);

        if (processInventory) {
            processAllInventoryItems();
        }

        return priceWithTax;
    }

    /** Determines the correct discount rate based on customer type and loyalty points. */
    private double resolveDiscountRate(String customerType) {
        if ("PREMIUM".equals(customerType)) {
            return loyaltyPoints > PREMIUM_HIGH_VOLUME_THRESHOLD
                    ? PREMIUM_HIGH_VOLUME_DISCOUNT
                    : PREMIUM_STANDARD_DISCOUNT;
        }
        if ("STANDARD".equals(customerType)) {
            return loyaltyPoints > STANDARD_HIGH_VOLUME_THRESHOLD
                    ? STANDARD_HIGH_VOLUME_DISCOUNT
                    : 0.0;
        }
        // Unknown customer type — no discount applied.
        return 0.0;
    }

    /** Applies a percentage discount to a price. */
    private double applyDiscount(double price, double discountRate) {
        return price - (price * discountRate);
    }

    /** Adds the standard tax rate to a price. */
    private double addTax(double price) {
        return price * (1 + TAX_RATE);
    }

    /** Prints a clear, labelled order summary — output is isolated from calculation. */
    private void printOrderSummary(String customerType, double discountRate,
                                   double discountedPrice, double finalPrice) {
        System.out.println("=== Order Summary ===");
        System.out.printf("Customer  : %s (%s)%n", customerName, customerType);
        System.out.printf("Base Price: %.2f%n", basePrice);
        System.out.printf("Discount  : %.0f%%%n", discountRate * 100);
        System.out.printf("After Disc: %.2f%n", discountedPrice);
        System.out.printf("Tax (%.0f%%): %.2f%n", TAX_RATE * 100, finalPrice - discountedPrice);
        System.out.printf("Total     : %.2f%n", finalPrice);
    }

    /** Runs INVENTORY_PASSES_PER_ITEM processing passes for every inventory item. */
    private void processAllInventoryItems() {
        for (String item : inventoryItems) {
            processInventoryItem(item);
        }
    }

    /** Processes a single inventory item for the configured number of passes. */
    private void processInventoryItem(String item) {
        if (!isValidInventoryItem(item)) {
            return; // Guard clause — skip invalid items without deep nesting.
        }
        for (int pass = 0; pass < INVENTORY_PASSES_PER_ITEM; pass++) {
            System.out.printf("Processing inventory: %s (pass %d)%n", item, pass);
        }
    }

    /** Returns true if the item is a valid, recognised inventory entry. */
    private boolean isValidInventoryItem(String item) {
        return item != null && item.startsWith("item");
    }

    // -------------------------------------------------------------------------
    // String utility — now a separate, well-named, focused method
    // -------------------------------------------------------------------------

    /**
     * Joins a list of strings with " | " as separator, ignoring null/blank values.
     * Uses StringBuilder for O(n) performance instead of String concatenation.
     *
     * @param values the strings to join
     * @return joined, pipe-separated string
     */
    public String joinNonBlank(String[] values) {
        return Arrays.stream(values)
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.joining(" | "));
    }

    // -------------------------------------------------------------------------
    // Item addition — clear return type communicates success/failure explicitly
    // -------------------------------------------------------------------------

    /**
     * Validates and adds a value to the inventory list.
     *
     * @param value the string to add
     * @throws IllegalArgumentException if the value is null or too short
     */
    public void addInventoryItem(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Inventory item must not be null.");
        }
        if (value.length() < 3) {
            throw new IllegalArgumentException(
                    "Inventory item must be at least 3 characters long, got: '" + value + "'");
        }
        inventoryItems.add(value);
        inventoryItems.add(value.toUpperCase());
    }

    // -------------------------------------------------------------------------
    // Main — demonstrates usage with readable, self-documenting arguments
    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        // All arguments have clear, obvious meaning.
        CleanCodeExample order = new CleanCodeExample(
                /* basePrice      */ 99.99,
                /* loyaltyPoints  */ 600,
                /* customerName   */ "Alice Smith"
        );

        boolean applyTax         = true;
        boolean processInventory = false;

        double total = order.calculateOrderTotal("PREMIUM", applyTax, processInventory);
        System.out.printf("%nFinal order total: %.2f%n%n", total);

        // Join example
        String joined = order.joinNonBlank(new String[]{"apple", "banana", null, "", "cherry"});
        System.out.println("Joined: " + joined);

        // Item addition with exception-based error signalling
        try {
            order.addInventoryItem("widget");
            System.out.println("Item added successfully.");
            order.addInventoryItem("ab"); // too short — will throw
        } catch (IllegalArgumentException ex) {
            System.out.println("Could not add item: " + ex.getMessage());
        }
    }
}
