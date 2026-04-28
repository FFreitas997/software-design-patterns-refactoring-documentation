import java.util.Arrays;
import java.util.List;

/**
 * Strategy Pattern
 *
 * Intent: Define a family of algorithms, encapsulate each one, and make
 *         them interchangeable. Strategy lets the algorithm vary independently
 *         from clients that use it.
 *
 * This file demonstrates two independent uses of the Strategy pattern:
 *
 * 1. Payment strategies in an e-commerce checkout
 *    (CreditCard, PayPal, CryptoCurrency, BankTransfer)
 *
 * 2. Sorting strategies for a list of numbers
 *    (BubbleSort, QuickSort, MergeSort -- demonstrating algorithm swap)
 *
 * Roles:
 *   Strategy         -> PaymentStrategy, SortStrategy
 *   ConcreteStrategy -> CreditCardPayment, PayPalPayment, ...
 *   Context          -> ShoppingCart, Sorter
 *   Client           -> main()
 */
public class Strategy {

    // =========================================================================
    // EXAMPLE 1: Payment Strategies
    // =========================================================================

    /** Order details passed to the payment strategy. */
    static class Order {
        private final String orderId;
        private final double total;
        private final String customerEmail;

        Order(String orderId, double total, String customerEmail) {
            this.orderId       = orderId;
            this.total         = total;
            this.customerEmail = customerEmail;
        }

        public String getOrderId()       { return orderId; }
        public double getTotal()         { return total; }
        public String getCustomerEmail() { return customerEmail; }
    }

    /** PaymentStrategy interface — the common contract for all payment methods. */
    interface PaymentStrategy {
        /**
         * Processes the payment for the given order.
         *
         * @param order the order to pay for
         * @return true if payment was successful
         */
        boolean pay(Order order);

        /** Returns a human-readable name for this payment method. */
        String getPaymentMethodName();
    }

    // --- Concrete Payment Strategies ---

    /** Processes payment via credit or debit card. */
    static class CreditCardPayment implements PaymentStrategy {
        private final String cardNumber;
        private final String cardHolder;
        private final String cvv;
        private final String expiryDate;

        CreditCardPayment(String cardNumber, String cardHolder,
                          String cvv, String expiryDate) {
            this.cardNumber = cardNumber;
            this.cardHolder = cardHolder;
            this.cvv        = cvv;
            this.expiryDate = expiryDate;
        }

        @Override
        public boolean pay(Order order) {
            String masked = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
            System.out.printf("  [Credit Card] Charging $%.2f to %s (%s)%n",
                    order.getTotal(), masked, cardHolder);
            // In reality, would call card payment gateway API
            System.out.printf("  [Credit Card] Authorization code: CC-%s-OK%n", order.getOrderId());
            return true;
        }

        @Override
        public String getPaymentMethodName() { return "Credit Card"; }
    }

    /** Processes payment via PayPal. */
    static class PayPalPayment implements PaymentStrategy {
        private final String email;
        private final String password;

        PayPalPayment(String email, String password) {
            this.email    = email;
            this.password = password;
        }

        @Override
        public boolean pay(Order order) {
            System.out.printf("  [PayPal] Authenticating %s...%n", email);
            System.out.printf("  [PayPal] Transferring $%.2f via PayPal account: %s%n",
                    order.getTotal(), email);
            System.out.printf("  [PayPal] Transaction ID: PP-%s%n", order.getOrderId());
            return true;
        }

        @Override
        public String getPaymentMethodName() { return "PayPal"; }
    }

    /** Processes payment via cryptocurrency. */
    static class CryptoPayment implements PaymentStrategy {
        private final String walletAddress;
        private final String currency;   // "BTC", "ETH", etc.
        private final double exchangeRate;

        CryptoPayment(String walletAddress, String currency, double exchangeRate) {
            this.walletAddress = walletAddress;
            this.currency      = currency;
            this.exchangeRate  = exchangeRate;
        }

        @Override
        public boolean pay(Order order) {
            double cryptoAmount = order.getTotal() / exchangeRate;
            System.out.printf("  [Crypto] Sending %.6f %s (≈ $%.2f) to wallet %s%n",
                    cryptoAmount, currency, order.getTotal(), walletAddress);
            System.out.printf("  [Crypto] Transaction hash: 0x%s...%n",
                    Integer.toHexString(order.hashCode()));
            return true;
        }

        @Override
        public String getPaymentMethodName() { return currency + " Cryptocurrency"; }
    }

    /**
     * The Context: ShoppingCart.
     * Holds a PaymentStrategy and uses it to process checkout.
     * The strategy can be changed at any time (even at runtime).
     */
    static class ShoppingCart {
        private PaymentStrategy paymentStrategy;
        private double total;

        ShoppingCart(double total) { this.total = total; }

        /** Sets (or swaps) the payment strategy. */
        public void setPaymentStrategy(PaymentStrategy strategy) {
            this.paymentStrategy = strategy;
            System.out.println("[Cart] Payment method set to: " + strategy.getPaymentMethodName());
        }

        /** Processes checkout using the currently configured strategy. */
        public boolean checkout(String orderId, String customerEmail) {
            if (paymentStrategy == null) {
                System.out.println("[Cart] No payment method selected!");
                return false;
            }
            Order order = new Order(orderId, total, customerEmail);
            System.out.printf("%n[Cart] Checking out order %s for $%.2f via %s%n",
                    orderId, total, paymentStrategy.getPaymentMethodName());
            boolean success = paymentStrategy.pay(order);
            System.out.println("[Cart] Payment " + (success ? "✅ succeeded" : "❌ failed"));
            return success;
        }
    }

    // =========================================================================
    // EXAMPLE 2: Sorting Strategies
    // =========================================================================

    /** SortStrategy interface. */
    interface SortStrategy {
        void sort(int[] array);
        String getName();
    }

    /** Bubble Sort — O(n²) — simple but slow. */
    static class BubbleSort implements SortStrategy {
        @Override
        public void sort(int[] a) {
            int n = a.length;
            for (int i = 0; i < n - 1; i++)
                for (int j = 0; j < n - i - 1; j++)
                    if (a[j] > a[j + 1]) { int tmp = a[j]; a[j] = a[j+1]; a[j+1] = tmp; }
        }

        @Override
        public String getName() { return "BubbleSort O(n²)"; }
    }

    /** QuickSort — O(n log n) average — fast in practice. */
    static class QuickSort implements SortStrategy {
        @Override
        public void sort(int[] a) { quickSort(a, 0, a.length - 1); }

        private void quickSort(int[] a, int low, int high) {
            if (low < high) {
                int pi = partition(a, low, high);
                quickSort(a, low, pi - 1);
                quickSort(a, pi + 1, high);
            }
        }

        private int partition(int[] a, int low, int high) {
            int pivot = a[high], i = low - 1;
            for (int j = low; j < high; j++)
                if (a[j] <= pivot) { i++; int tmp = a[i]; a[i] = a[j]; a[j] = tmp; }
            int tmp = a[i+1]; a[i+1] = a[high]; a[high] = tmp;
            return i + 1;
        }

        @Override
        public String getName() { return "QuickSort O(n log n)"; }
    }

    /** MergeSort — O(n log n) guaranteed — stable sort. */
    static class MergeSort implements SortStrategy {
        @Override
        public void sort(int[] a) { mergeSort(a, 0, a.length - 1); }

        private void mergeSort(int[] a, int l, int r) {
            if (l < r) {
                int m = (l + r) / 2;
                mergeSort(a, l, m);
                mergeSort(a, m + 1, r);
                merge(a, l, m, r);
            }
        }

        private void merge(int[] a, int l, int m, int r) {
            int[] left  = Arrays.copyOfRange(a, l, m + 1);
            int[] right = Arrays.copyOfRange(a, m + 1, r + 1);
            int i = 0, j = 0, k = l;
            while (i < left.length && j < right.length)
                a[k++] = left[i] <= right[j] ? left[i++] : right[j++];
            while (i < left.length)  a[k++] = left[i++];
            while (j < right.length) a[k++] = right[j++];
        }

        @Override
        public String getName() { return "MergeSort O(n log n) stable"; }
    }

    /** Context: Sorter. Uses a SortStrategy to sort arrays. */
    static class Sorter {
        private SortStrategy strategy;

        Sorter(SortStrategy strategy) { this.strategy = strategy; }

        public void setStrategy(SortStrategy strategy) {
            this.strategy = strategy;
        }

        public int[] sort(int[] data) {
            int[] copy = Arrays.copyOf(data, data.length);
            System.out.println("[Sorter] Sorting " + Arrays.toString(copy)
                    + " with " + strategy.getName());
            strategy.sort(copy);
            System.out.println("[Sorter] Result: " + Arrays.toString(copy));
            return copy;
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Strategy Pattern Demo ===\n");

        // --- Payment Strategies ---
        System.out.println("--- Payment Strategies ---");
        ShoppingCart cart = new ShoppingCart(149.99);

        cart.setPaymentStrategy(new CreditCardPayment("4111111111111234", "Alice Smith", "123", "12/26"));
        cart.checkout("ORD-001", "alice@example.com");

        cart.setPaymentStrategy(new PayPalPayment("alice@paypal.com", "secret"));
        cart.checkout("ORD-002", "alice@example.com");

        cart.setPaymentStrategy(new CryptoPayment("0xABCDEF1234567890", "ETH", 3500.0));
        cart.checkout("ORD-003", "alice@example.com");

        // --- Sorting Strategies ---
        System.out.println("\n--- Sorting Strategies ---");
        int[] data = {64, 34, 25, 12, 22, 11, 90};

        Sorter sorter = new Sorter(new BubbleSort());
        sorter.sort(data);

        sorter.setStrategy(new QuickSort());
        sorter.sort(data);

        sorter.setStrategy(new MergeSort());
        sorter.sort(data);

        // Strategy can also be a lambda (for simple cases)
        System.out.println("\n--- Lambda Strategy (Java 8+) ---");
        SortStrategy javaBuiltin = new SortStrategy() {
            @Override
            public void sort(int[] a) { Arrays.sort(a); }

            @Override
            public String getName() { return "Arrays.sort (Java built-in)"; }
        };
        sorter.setStrategy(javaBuiltin);
        sorter.sort(data);
    }
}
