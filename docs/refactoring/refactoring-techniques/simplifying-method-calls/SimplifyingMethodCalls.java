import java.util.Date;
import java.util.List;

/**
 * SimplifyingMethodCalls.java
 *
 * Before/after Java examples for all fourteen Simplifying Method Calls
 * refactoring techniques:
 *
 *   1.  Rename Method
 *   2.  Add Parameter
 *   3.  Remove Parameter
 *   4.  Separate Query from Modifier
 *   5.  Parameterize Method
 *   6.  Replace Parameter with Explicit Methods
 *   7.  Preserve Whole Object
 *   8.  Replace Parameter with Method Call
 *   9.  Introduce Parameter Object
 *  10.  Remove Setting Method
 *  11.  Hide Method
 *  12.  Replace Constructor with Factory Method
 *  13.  Replace Error Code with Exception
 *  14.  Replace Exception with Test
 */
public class SimplifyingMethodCalls {

    // =========================================================================
    // 1. RENAME METHOD
    // =========================================================================

    static class PhoneBookBefore {
        private int phoneNumber = 5551234;
        int getPNumber() { return phoneNumber; }  // SMELL: abbreviation
    }

    static class PhoneBookAfter {
        private int phoneNumber = 5551234;
        int getTelephoneNumber() { return phoneNumber; }  // clear name
    }

    // =========================================================================
    // 2. ADD PARAMETER
    // =========================================================================

    // BEFORE: no date → can only return today's contact
    static String getContactBefore() { return "Alice (current)"; }

    // AFTER: date parameter enables historic look-ups
    static String getContactAfter(Date date) {
        return "Alice (as of " + date + ")";
    }

    // =========================================================================
    // 3. REMOVE PARAMETER
    // =========================================================================

    static class CustomerDiscount {
        double loyaltyDiscount = 0.10;

        // BEFORE: 'unused' is never used
        double discountBefore(String unused) { return loyaltyDiscount; }

        // AFTER: unused parameter removed
        double discountAfter() { return loyaltyDiscount; }
    }

    // =========================================================================
    // 4. SEPARATE QUERY FROM MODIFIER
    // =========================================================================

    // Dummy invoice model
    static class Invoice { double amount; Invoice(double a) { this.amount = a; } }

    static class Billing {
        List<Invoice> invoices = List.of(new Invoice(100), new Invoice(200));
        boolean billed = false;

        // BEFORE: returns a value AND has a side effect
        double getTotalOutstandingAndSendBillBefore() {
            double total = invoices.stream().mapToDouble(i -> i.amount).sum();
            billed = true;   // side effect
            return total;
        }

        // AFTER: query and command are separate
        double getTotalOutstanding() {
            return invoices.stream().mapToDouble(i -> i.amount).sum();
        }
        void sendBill() { billed = true; }
        // Caller: double t = getTotalOutstanding(); sendBill();
    }

    // =========================================================================
    // 5. PARAMETERIZE METHOD
    // =========================================================================

    static class Salary {
        double amount = 50_000;

        // BEFORE: duplicated methods for fixed raise amounts
        void fivePercentRaise()  { amount *= 1.05; }
        void tenPercentRaise()   { amount *= 1.10; }

        // AFTER: single parameterized method
        void raise(double percentage) { amount *= (1 + percentage / 100.0); }
    }

    // =========================================================================
    // 6. REPLACE PARAMETER WITH EXPLICIT METHODS
    // =========================================================================

    // BEFORE: single method with string parameter to distinguish behaviour
    static class DimensionBefore {
        int height, width;
        void setValueBefore(String name, int value) {
            if      ("height".equals(name)) height = value;
            else if ("width" .equals(name)) width  = value;
        }
    }

    // AFTER: explicit, type-safe methods
    static class DimensionAfter {
        int height, width;
        void setHeight(int value) { height = value; }
        void setWidth(int value)  { width  = value; }
    }

    // =========================================================================
    // 7. PRESERVE WHOLE OBJECT
    // =========================================================================

    static class TempRange {
        final int low, high;
        TempRange(int low, int high) { this.low = low; this.high = high; }
        int getLow()  { return low; }
        int getHigh() { return high; }
    }

    static class HeatingPlan {
        private final int planLow = 15, planHigh = 30;

        // BEFORE: caller extracts two values then passes them separately
        boolean withinRangeBefore(int low, int high) {
            return low >= planLow && high <= planHigh;
        }

        // AFTER: whole object is passed — no extraction at call site
        boolean withinRangeAfter(TempRange range) {
            return range.getLow() >= planLow && range.getHigh() <= planHigh;
        }
    }

    // =========================================================================
    // 8. REPLACE PARAMETER WITH METHOD CALL
    // =========================================================================

    static class OrderRPMC {
        int quantity = 10;
        double itemPrice = 5.0;

        private double discountLevel() { return quantity > 50 ? 0.1 : 0.05; }

        // BEFORE: discount level computed outside and passed in
        double discountedPriceBefore(double basePrice, double discount) {
            return basePrice * (1 - discount);
        }

        // AFTER: method calls discountLevel() itself — parameter removed
        double discountedPriceAfter(double basePrice) {
            return basePrice * (1 - discountLevel());
        }
    }

    // =========================================================================
    // 9. INTRODUCE PARAMETER OBJECT
    // =========================================================================

    // BEFORE: start/end date parameters repeated in every method
    static double amountInvoicedInBefore(Date start, Date end) { return 100.0; }
    static double amountReceivedInBefore(Date start, Date end) { return 80.0; }

    // AFTER: DateRange parameter object groups the two dates
    static final class DateRange {
        final Date start, end;
        DateRange(Date start, Date end) { this.start = start; this.end = end; }
    }

    static double amountInvoicedInAfter(DateRange range) { return 100.0; }
    static double amountReceivedInAfter(DateRange range) { return 80.0;  }

    // =========================================================================
    // 10. REMOVE SETTING METHOD
    // =========================================================================

    // BEFORE: customerCode is setable after construction — accidental mutation possible
    static class CustomerBefore {
        private String customerCode;
        public void setCustomerCode(String code) { this.customerCode = code; }
        public String getCustomerCode() { return customerCode; }
    }

    // AFTER: customerCode is final — set once at construction
    static class CustomerAfter {
        private final String customerCode;
        CustomerAfter(String code) { this.customerCode = code; }
        String getCustomerCode() { return customerCode; }
    }

    // =========================================================================
    // 11. HIDE METHOD
    // =========================================================================

    static class Account {
        private double balance = 1000, rate = 0.05;

        // BEFORE: unnecessarily public
        // public double computeInterest() { return balance * rate; }

        // AFTER: private — only used internally
        private double computeInterest() { return balance * rate; }

        public double getBalanceWithInterest() { return balance + computeInterest(); }
    }

    // =========================================================================
    // 12. REPLACE CONSTRUCTOR WITH FACTORY METHOD
    // =========================================================================

    abstract static class Employee {
        abstract String getType();
    }
    static class Engineer    extends Employee { public String getType() { return "Engineer"; } }
    static class Salesperson extends Employee { public String getType() { return "Salesperson"; } }

    static final int ENGINEER_CODE    = 0;
    static final int SALESPERSON_CODE = 1;

    // BEFORE: constructor-based creation — no polymorphism
    // new Employee(ENGINEER_CODE) — not possible with abstract Employee

    // AFTER: factory method encapsulates creation logic
    static Employee createEmployee(int type) {
        switch (type) {
            case ENGINEER_CODE:    return new Engineer();
            case SALESPERSON_CODE: return new Salesperson();
            default: throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    // =========================================================================
    // 13. REPLACE ERROR CODE WITH EXCEPTION
    // =========================================================================

    static class BankAccountBefore {
        int balance = 500;

        // BEFORE: -1 error code on failure
        int withdrawBefore(int amount) {
            if (amount > balance) return -1;
            balance -= amount;
            return 0;
        }
    }

    static class InsufficientFundsException extends RuntimeException {
        InsufficientFundsException(String msg) { super(msg); }
    }

    static class BankAccountAfter {
        int balance = 500;

        // AFTER: exception communicates failure semantics clearly
        void withdrawAfter(int amount) {
            if (amount > balance)
                throw new InsufficientFundsException(
                        "Balance: " + balance + ", requested: " + amount);
            balance -= amount;
        }
    }

    // =========================================================================
    // 14. REPLACE EXCEPTION WITH TEST
    // =========================================================================

    static int[] values = {10, 20, 30};

    // BEFORE: exception used for normal control flow
    static double getValueBefore(int period) {
        try {
            return values[period];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    // AFTER: guard clause removes exception-driven control flow
    static double getValueAfter(int period) {
        if (period >= values.length) return 0;
        return values[period];
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Rename Method ===");
        System.out.println("Phone: " + new PhoneBookAfter().getTelephoneNumber());

        System.out.println("\n=== 2. Add Parameter ===");
        System.out.println(getContactAfter(new Date()));

        System.out.println("\n=== 3. Remove Parameter ===");
        System.out.println("Discount: " + new CustomerDiscount().discountAfter());

        System.out.println("\n=== 4. Separate Query from Modifier ===");
        Billing billing = new Billing();
        System.out.printf("Outstanding: %.2f%n", billing.getTotalOutstanding());
        billing.sendBill();
        System.out.println("Bill sent: " + billing.billed);

        System.out.println("\n=== 5. Parameterize Method ===");
        Salary salary = new Salary();
        salary.raise(10);
        System.out.printf("After 10%% raise: %.2f%n", salary.amount);

        System.out.println("\n=== 6. Replace Parameter with Explicit Methods ===");
        DimensionAfter dim = new DimensionAfter();
        dim.setHeight(200);
        dim.setWidth(300);
        System.out.println("Dimensions: " + dim.height + "x" + dim.width);

        System.out.println("\n=== 7. Preserve Whole Object ===");
        HeatingPlan plan = new HeatingPlan();
        TempRange range = new TempRange(18, 25);
        System.out.println("Within range: " + plan.withinRangeAfter(range));

        System.out.println("\n=== 8. Replace Parameter with Method Call ===");
        OrderRPMC order = new OrderRPMC();
        System.out.printf("Discounted price: %.2f%n",
                order.discountedPriceAfter(order.quantity * order.itemPrice));

        System.out.println("\n=== 9. Introduce Parameter Object ===");
        DateRange dr = new DateRange(new Date(), new Date());
        System.out.printf("Invoiced: %.2f | Received: %.2f%n",
                amountInvoicedInAfter(dr), amountReceivedInAfter(dr));

        System.out.println("\n=== 10. Remove Setting Method ===");
        CustomerAfter cust = new CustomerAfter("CUST-001");
        System.out.println("Customer code: " + cust.getCustomerCode());

        System.out.println("\n=== 11. Hide Method ===");
        Account account = new Account();
        System.out.printf("Balance with interest: %.2f%n", account.getBalanceWithInterest());

        System.out.println("\n=== 12. Replace Constructor with Factory Method ===");
        Employee emp = createEmployee(SALESPERSON_CODE);
        System.out.println("Created: " + emp.getType());

        System.out.println("\n=== 13. Replace Error Code with Exception ===");
        BankAccountAfter bank = new BankAccountAfter();
        try {
            bank.withdrawAfter(600);
        } catch (InsufficientFundsException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("\n=== 14. Replace Exception with Test ===");
        System.out.println("Period 1: " + getValueAfter(1));
        System.out.println("Period 9: " + getValueAfter(9));
    }
}
