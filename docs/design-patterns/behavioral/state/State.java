/**
 * State Pattern
 *
 * Intent: Allow an object to alter its behavior when its internal state
 *         changes. The object will appear to change its class.
 *
 * Example: A vending machine with states:
 *   IDLE         -> Waiting for money to be inserted
 *   HAS_MONEY    -> Money inserted, waiting for product selection
 *   DISPENSING   -> Delivering a product
 *   OUT_OF_STOCK -> No products available
 *
 * Roles:
 *   Context         -> VendingMachine
 *   State           -> VendingState (interface)
 *   ConcreteState   -> IdleState, HasMoneyState, DispensingState, OutOfStockState
 */
public class State {

    // =========================================================================
    // Context: VendingMachine
    // =========================================================================

    /**
     * The Context class. Maintains the current state and delegates all
     * state-specific behavior to the current state object.
     *
     * The machine interface (insertMoney, selectProduct, cancel) remains
     * the same regardless of the current state.
     */
    static class VendingMachine {
        // Available states — created once and reused (Flyweight-like)
        private final VendingState idleState        = new IdleState(this);
        private final VendingState hasMoneyState    = new HasMoneyState(this);
        private final VendingState dispensingState  = new DispensingState(this);
        private final VendingState outOfStockState  = new OutOfStockState(this);

        private VendingState currentState;
        private int stockCount;
        private double insertedAmount;
        private String selectedProduct;

        VendingMachine(int stockCount) {
            this.stockCount = stockCount;
            this.currentState = stockCount > 0 ? idleState : outOfStockState;
            System.out.println("[VendingMachine] Started with " + stockCount + " items. State: " + currentState.getStateName());
        }

        // --- Client-facing operations (delegated to current state) ---

        public void insertMoney(double amount) {
            System.out.println("\n[Client] Inserting $" + amount + "...");
            currentState.insertMoney(amount);
        }

        public void selectProduct(String product) {
            System.out.println("[Client] Selecting product: " + product + "...");
            currentState.selectProduct(product);
        }

        public void dispense() {
            System.out.println("[Client] Requesting dispense...");
            currentState.dispense();
        }

        public void cancel() {
            System.out.println("[Client] Cancelling transaction...");
            currentState.cancel();
        }

        // --- State transition method ---

        public void setState(VendingState newState) {
            System.out.println("  [Machine] Transitioning: "
                    + currentState.getStateName() + " → " + newState.getStateName());
            this.currentState = newState;
        }

        // --- State accessors (used by state objects for transitions) ---

        public VendingState getIdleState()       { return idleState; }
        public VendingState getHasMoneyState()   { return hasMoneyState; }
        public VendingState getDispensingState() { return dispensingState; }
        public VendingState getOutOfStockState() { return outOfStockState; }

        // --- Internal state modifiers ---

        public void addMoney(double amount)          { insertedAmount += amount; }
        public double getInsertedAmount()            { return insertedAmount; }
        public void clearInsertedAmount()            { insertedAmount = 0; }
        public void setSelectedProduct(String p)     { selectedProduct = p; }
        public String getSelectedProduct()           { return selectedProduct; }
        public int getStock()                        { return stockCount; }
        public void decrementStock()                 { stockCount--; }

        /** Prints the current machine status. */
        public void printStatus() {
            System.out.printf("[Machine Status] State: %-15s | Stock: %d | Balance: $%.2f%n",
                    currentState.getStateName(), stockCount, insertedAmount);
        }
    }

    // =========================================================================
    // State Interface
    // =========================================================================

    /**
     * The State interface declares all actions that can be performed on
     * the vending machine. Each ConcreteState implements them differently.
     */
    interface VendingState {
        void insertMoney(double amount);
        void selectProduct(String product);
        void dispense();
        void cancel();
        String getStateName();
    }

    // =========================================================================
    // Concrete States
    // =========================================================================

    /**
     * IDLE state: Machine is waiting. No money inserted.
     * Valid action: insertMoney (transitions to HAS_MONEY)
     */
    static class IdleState implements VendingState {
        private final VendingMachine machine;

        IdleState(VendingMachine machine) { this.machine = machine; }

        @Override
        public void insertMoney(double amount) {
            if (amount <= 0) {
                System.out.println("  [Idle] Invalid amount. Please insert a positive amount.");
                return;
            }
            machine.addMoney(amount);
            System.out.printf("  [Idle] Accepted $%.2f. Total: $%.2f%n",
                    amount, machine.getInsertedAmount());
            machine.setState(machine.getHasMoneyState());
        }

        @Override
        public void selectProduct(String product) {
            System.out.println("  [Idle] Please insert money first.");
        }

        @Override
        public void dispense() {
            System.out.println("  [Idle] Please insert money and select a product.");
        }

        @Override
        public void cancel() {
            System.out.println("  [Idle] Nothing to cancel.");
        }

        @Override
        public String getStateName() { return "IDLE"; }
    }

    /**
     * HAS_MONEY state: Money has been inserted.
     * Valid actions: insertMoney (add more), selectProduct, cancel
     */
    static class HasMoneyState implements VendingState {
        private final VendingMachine machine;
        private static final double PRODUCT_PRICE = 1.50;

        HasMoneyState(VendingMachine machine) { this.machine = machine; }

        @Override
        public void insertMoney(double amount) {
            machine.addMoney(amount);
            System.out.printf("  [HasMoney] Added $%.2f. Total: $%.2f%n",
                    amount, machine.getInsertedAmount());
        }

        @Override
        public void selectProduct(String product) {
            if (machine.getInsertedAmount() < PRODUCT_PRICE) {
                System.out.printf("  [HasMoney] Insufficient funds. Need $%.2f, have $%.2f%n",
                        PRODUCT_PRICE, machine.getInsertedAmount());
                return;
            }
            machine.setSelectedProduct(product);
            System.out.println("  [HasMoney] Product selected: " + product);
            machine.setState(machine.getDispensingState());
            machine.dispense();
        }

        @Override
        public void dispense() {
            System.out.println("  [HasMoney] Please select a product first.");
        }

        @Override
        public void cancel() {
            double refund = machine.getInsertedAmount();
            machine.clearInsertedAmount();
            System.out.printf("  [HasMoney] Cancelled. Refunding $%.2f%n", refund);
            machine.setState(machine.getIdleState());
        }

        @Override
        public String getStateName() { return "HAS_MONEY"; }
    }

    /**
     * DISPENSING state: Product is being delivered.
     * This state automatically completes and transitions back to IDLE or OUT_OF_STOCK.
     */
    static class DispensingState implements VendingState {
        private final VendingMachine machine;
        private static final double PRODUCT_PRICE = 1.50;

        DispensingState(VendingMachine machine) { this.machine = machine; }

        @Override
        public void insertMoney(double amount) {
            System.out.println("  [Dispensing] Please wait, dispensing in progress...");
        }

        @Override
        public void selectProduct(String product) {
            System.out.println("  [Dispensing] Please wait, dispensing in progress...");
        }

        @Override
        public void dispense() {
            String product = machine.getSelectedProduct();
            double change  = machine.getInsertedAmount() - PRODUCT_PRICE;

            System.out.println("  [Dispensing] 🎁 Dispensing: " + product);
            machine.decrementStock();
            machine.clearInsertedAmount();

            if (change > 0) {
                System.out.printf("  [Dispensing] Change returned: $%.2f%n", change);
            }

            if (machine.getStock() == 0) {
                System.out.println("  [Dispensing] ⚠️  Stock depleted!");
                machine.setState(machine.getOutOfStockState());
            } else {
                machine.setState(machine.getIdleState());
            }
        }

        @Override
        public void cancel() {
            System.out.println("  [Dispensing] Cannot cancel — dispensing in progress.");
        }

        @Override
        public String getStateName() { return "DISPENSING"; }
    }

    /**
     * OUT_OF_STOCK state: No products available. All actions are rejected.
     */
    static class OutOfStockState implements VendingState {
        private final VendingMachine machine;

        OutOfStockState(VendingMachine machine) { this.machine = machine; }

        @Override
        public void insertMoney(double amount) {
            System.out.printf("  [OutOfStock] Machine empty. Returning $%.2f%n", amount);
        }

        @Override
        public void selectProduct(String product) {
            System.out.println("  [OutOfStock] Sorry, machine is empty.");
        }

        @Override
        public void dispense() {
            System.out.println("  [OutOfStock] Cannot dispense — no products.");
        }

        @Override
        public void cancel() {
            System.out.println("  [OutOfStock] Nothing to cancel.");
        }

        @Override
        public String getStateName() { return "OUT_OF_STOCK"; }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== State Pattern Demo — Vending Machine ===");

        VendingMachine machine = new VendingMachine(2);
        machine.printStatus();

        System.out.println("\n--- Scenario 1: Normal purchase ---");
        machine.insertMoney(1.50);
        machine.selectProduct("Cola");
        machine.printStatus();

        System.out.println("\n--- Scenario 2: Add more money, then cancel ---");
        machine.insertMoney(1.00);
        machine.insertMoney(0.50);
        machine.cancel();
        machine.printStatus();

        System.out.println("\n--- Scenario 3: Buy last item ---");
        machine.insertMoney(2.00);
        machine.selectProduct("Chips");
        machine.printStatus();   // should be OUT_OF_STOCK

        System.out.println("\n--- Scenario 4: Try to use empty machine ---");
        machine.insertMoney(1.50);
        machine.selectProduct("Water");
    }
}
