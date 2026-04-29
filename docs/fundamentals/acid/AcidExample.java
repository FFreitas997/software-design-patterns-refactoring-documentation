import java.util.List;

/**
 * ACID — Atomicity, Consistency, Isolation, Durability
 *
 * Demonstrates each ACID property using a classic bank transfer scenario.
 *
 * A -> Atomicity   : The debit AND credit happen together, or not at all.
 * C -> Consistency : Account balance cannot go negative (business rule).
 * I -> Isolation   : Concurrent transfers see a consistent snapshot.
 * D -> Durability  : Once committed, the transfer survives a crash.
 */
public class AcidExample {

    // ── Domain Model ─────────────────────────────────────────────

    static class Account {
        private final String id;
        double balance; // package-private for rollback support in this demo

        Account(String id, double initialBalance) {
            this.id      = id;
            this.balance = initialBalance;
        }

        public String getId()      { return id;      }
        public double getBalance() { return balance; }

        /** Throws if the business rule (balance >= 0) would be violated -> Consistency */
        public void debit(double amount) {
            if (balance - amount < 0)
                throw new IllegalStateException("Consistency violation: insufficient funds in " + id);
            balance -= amount;
        }

        public void credit(double amount) { balance += amount; }

        @Override public String toString() {
            return String.format("Account{id='%s', balance=%.2f}", id, balance);
        }
    }

    // ── Transaction Coordinator ───────────────────────────────────

    static class TransactionCoordinator {
        /**
         * ATOMICITY: if any step throws, the rollback runnable is called,
         * restoring the system to the state before the transaction started.
         */
        public void execute(String txName, List<Runnable> steps, Runnable rollback) {
            System.out.println("\n[TX:" + txName + "] BEGIN");
            try {
                for (Runnable step : steps) step.run();
                // DURABILITY: in a real DB the engine flushes the write-ahead log here.
                System.out.println("[TX:" + txName + "] COMMIT  <- changes are now durable");
            } catch (Exception e) {
                System.out.println("[TX:" + txName + "] ERROR   -> " + e.getMessage());
                rollback.run();
                System.out.println("[TX:" + txName + "] ROLLBACK <- all changes undone (Atomicity preserved)");
            }
        }
    }

    // ── Bank Transfer Service ─────────────────────────────────────

    static class BankTransferService {
        private final TransactionCoordinator tx = new TransactionCoordinator();

        public void transfer(Account from, Account to, double amount) {
            final double fromSnap = from.balance;
            final double toSnap   = to.balance;

            tx.execute(
                "TRANSFER " + from.getId() + "->" + to.getId() + " $" + amount,
                List.of(
                    () -> { System.out.println("  Step 1: debit  " + from.getId() + " -$" + amount); from.debit(amount); },
                    () -> { System.out.println("  Step 2: credit " + to.getId()   + " +$" + amount); to.credit(amount); },
                    () ->   System.out.println("  Step 3: audit log written")
                ),
                () -> { from.balance = fromSnap; to.balance = toSnap; }
            );
        }
    }

    // ── Isolation Levels Overview ─────────────────────────────────

    static void isolationDemo() {
        System.out.println("\n=== ISOLATION - Isolation Levels Overview ===");
        System.out.printf("%-25s %-15s %-22s %-15s%n", "Level", "Dirty Read", "Non-Repeatable Read", "Phantom Read");
        System.out.println("-".repeat(80));
        System.out.printf("%-25s %-15s %-22s %-15s%n", "READ UNCOMMITTED", "Possible",  "Possible",  "Possible");
        System.out.printf("%-25s %-15s %-22s %-15s%n", "READ COMMITTED",   "Prevented", "Possible",  "Possible");
        System.out.printf("%-25s %-15s %-22s %-15s%n", "REPEATABLE READ",  "Prevented", "Prevented", "Possible");
        System.out.printf("%-25s %-15s %-22s %-15s%n", "SERIALIZABLE",     "Prevented", "Prevented", "Prevented");
        System.out.println("\nIn JDBC: conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);");
        System.out.println("         conn.setAutoCommit(false); ... conn.commit(); // or conn.rollback();");
    }

    // ── Demo ──────────────────────────────────────────────────────

    public static void main(String[] args) {
        Account alice = new Account("Alice", 500.00);
        Account bob   = new Account("Bob",   200.00);
        BankTransferService service = new BankTransferService();

        System.out.println("Initial state:");
        System.out.println("  " + alice);
        System.out.println("  " + bob);

        System.out.println("\n=== Scenario 1: Successful Transfer $100 ===");
        service.transfer(alice, bob, 100.00);
        System.out.println("After transfer: " + alice + " | " + bob);

        System.out.println("\n=== Scenario 2: Over-Draft Attempt ($1000 - exceeds balance) ===");
        service.transfer(alice, bob, 1000.00);
        System.out.println("After failed transfer (balances MUST be unchanged): " + alice + " | " + bob);

        isolationDemo();
    }
}

