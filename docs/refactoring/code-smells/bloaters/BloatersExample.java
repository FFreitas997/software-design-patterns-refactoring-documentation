import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BloatersExample.java
 *
 * Demonstrates all five Bloater code smells and their refactored counterparts.
 *
 * Smells covered:
 *   1. Long Method        → extract focused helper methods
 *   2. Large Class        → extract cohesive classes (see bottom of file)
 *   3. Primitive Obsession → replace primitive with value object
 *   4. Long Parameter List → introduce a parameter object
 *   5. Data Clumps        → extract a class for the clump
 */
public class BloatersExample {

    // =========================================================================
    // 1. LONG METHOD — before and after
    // =========================================================================

    /**
     * SMELL: processOrderDirty() is a long method that validates, calculates,
     * and persists all in one body with no clear boundaries.
     */
    static double processOrderDirty(List<String> items, String customer,
                                     double unitPrice, int quantity) {
        // --- validation ---
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have items");
        }
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Customer name required");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // --- calculation ---
        double subtotal  = unitPrice * quantity;
        double discount  = subtotal > 100 ? subtotal * 0.10 : 0;
        double afterDisc = subtotal - discount;
        double tax       = afterDisc * 0.15;
        double total     = afterDisc + tax;

        // --- output ---
        System.out.println("Customer : " + customer);
        System.out.println("Items    : " + items.size());
        System.out.println("Subtotal : " + subtotal);
        System.out.println("Discount : " + discount);
        System.out.println("Tax      : " + tax);
        System.out.println("Total    : " + total);

        return total;
    }

    // --- REFACTORED: three focused, testable methods ---

    static void validateOrderInputs(List<String> items, String customer,
                                     double unitPrice, int quantity) {
        if (items == null || items.isEmpty())   throw new IllegalArgumentException("Order must have items");
        if (customer == null || customer.isBlank()) throw new IllegalArgumentException("Customer name required");
        if (unitPrice <= 0)  throw new IllegalArgumentException("Unit price must be positive");
        if (quantity  <= 0)  throw new IllegalArgumentException("Quantity must be positive");
    }

    static double calculateOrderTotal(double unitPrice, int quantity) {
        double subtotal  = unitPrice * quantity;
        double discount  = subtotal > 100 ? subtotal * 0.10 : 0;
        double afterDisc = subtotal - discount;
        return afterDisc * 1.15; // tax included
    }

    static void printOrderSummary(String customer, List<String> items, double total) {
        System.out.printf("Customer : %s | Items: %d | Total: %.2f%n",
                customer, items.size(), total);
    }

    static double processOrderClean(List<String> items, String customer,
                                     double unitPrice, int quantity) {
        validateOrderInputs(items, customer, unitPrice, quantity);
        double total = calculateOrderTotal(unitPrice, quantity);
        printOrderSummary(customer, items, total);
        return total;
    }

    // =========================================================================
    // 2. LARGE CLASS — smell shown via comment; fix is separate classes below
    // =========================================================================

    /*
     * SMELL: A hypothetical UserGodClass would contain:
     *   - User data fields (name, email, passwordHash)
     *   - Email configuration (smtpHost, smtpPort)
     *   - Audit log list
     *   - Methods: save(), sendWelcomeEmail(), sendPasswordReset(), logAction()
     *
     * FIX: Separate into User, EmailService, AuditLogger (see inner classes below).
     */

    static class User {
        private final String name;
        private final String email;

        User(String name, String email) { this.name = name; this.email = email; }
        String getName()  { return name;  }
        String getEmail() { return email; }
        @Override public String toString() { return name + " <" + email + ">"; }
    }

    static class EmailService {
        private final String smtpHost;

        EmailService(String smtpHost) { this.smtpHost = smtpHost; }

        void sendWelcome(User user) {
            System.out.printf("[%s] Sending welcome email to %s%n", smtpHost, user.getEmail());
        }
    }

    static class AuditLogger {
        private final List<String> entries = new ArrayList<>();

        void log(String action) { entries.add(action); }
        List<String> getTrail() { return Collections.unmodifiableList(entries); }
    }

    // =========================================================================
    // 3. PRIMITIVE OBSESSION — before and after
    // =========================================================================

    // SMELL: money represented as a plain double — currency is lost.
    static double addPricesDirty(double a, double b) {
        return a + b;
    }

    // REFACTORED: Money value object carries both amount and currency.
    static final class Money {
        private final double amount;
        private final String currency;

        Money(double amount, String currency) {
            if (amount < 0) throw new IllegalArgumentException("Negative amount");
            this.amount   = amount;
            this.currency = currency;
        }

        Money add(Money other) {
            if (!this.currency.equals(other.currency))
                throw new IllegalArgumentException("Currency mismatch: " + currency + " vs " + other.currency);
            return new Money(this.amount + other.amount, this.currency);
        }

        @Override public String toString() { return String.format("%.2f %s", amount, currency); }
    }

    // =========================================================================
    // 4. LONG PARAMETER LIST — before and after
    // =========================================================================

    // SMELL: seven parameters at the call site are unreadable and error-prone.
    static void createUserDirty(String firstName, String lastName, String email,
                                  String phone, String role,
                                  boolean active, boolean verified) {
        System.out.printf("Creating user: %s %s (%s) role=%s active=%b verified=%b%n",
                firstName, lastName, email, role, active, verified);
    }

    // REFACTORED: parameter object groups related data.
    static final class UserRegistrationRequest {
        final String  firstName, lastName, email, phone, role;
        final boolean active, verified;

        UserRegistrationRequest(String firstName, String lastName, String email,
                                 String phone, String role,
                                 boolean active, boolean verified) {
            this.firstName = firstName; this.lastName = lastName;
            this.email = email;         this.phone = phone;
            this.role = role;           this.active = active;
            this.verified = verified;
        }
    }

    static void createUserClean(UserRegistrationRequest req) {
        System.out.printf("Creating user: %s %s (%s) role=%s active=%b verified=%b%n",
                req.firstName, req.lastName, req.email, req.role, req.active, req.verified);
    }

    // =========================================================================
    // 5. DATA CLUMPS — before and after
    // =========================================================================

    // SMELL: address fields repeated in every entity class.
    static class CustomerDirty {
        String name;
        String street, city, postalCode, country; // clump
    }

    static class SupplierDirty {
        String companyName;
        String street, city, postalCode, country; // same clump duplicated
    }

    // REFACTORED: Address is a first-class value object shared by all.
    static final class Address {
        final String street, city, postalCode, country;

        Address(String street, String city, String postalCode, String country) {
            this.street = street; this.city = city;
            this.postalCode = postalCode; this.country = country;
        }

        @Override public String toString() {
            return street + ", " + city + " " + postalCode + ", " + country;
        }
    }

    static class CustomerClean {
        String  name;
        Address address;
    }

    static class SupplierClean {
        String  companyName;
        Address address;
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Long Method ===");
        List<String> items = List.of("Widget", "Gadget");
        processOrderClean(items, "Alice", 60.0, 2);

        System.out.println("\n=== 2. Large Class ===");
        User user = new User("Bob", "bob@example.com");
        EmailService emailService = new EmailService("smtp.example.com");
        AuditLogger  auditLogger  = new AuditLogger();
        emailService.sendWelcome(user);
        auditLogger.log("User created: " + user);
        System.out.println("Audit trail: " + auditLogger.getTrail());

        System.out.println("\n=== 3. Primitive Obsession ===");
        Money price = new Money(29.99, "USD");
        Money tax   = new Money(4.50,  "USD");
        System.out.println("Total: " + price.add(tax));

        System.out.println("\n=== 4. Long Parameter List ===");
        UserRegistrationRequest req = new UserRegistrationRequest(
                "Carol", "Smith", "carol@example.com",
                "555-9876", "Admin", true, false);
        createUserClean(req);

        System.out.println("\n=== 5. Data Clumps ===");
        CustomerClean customer = new CustomerClean();
        customer.name    = "Dave";
        customer.address = new Address("10 Main St", "Springfield", "12345", "US");
        System.out.println(customer.name + " lives at " + customer.address);
    }
}
