import java.util.ArrayList;
import java.util.List;

/**
 * Singleton Pattern — Thread-Safe with Double-Checked Locking
 *
 * Intent: Ensure a class has only one instance and provide a global point
 *         of access to it.
 *
 * This example models an application Logger that collects log messages.
 * All parts of the application use the same Logger instance.
 */
public class Singleton {

    // -------------------------------------------------------------------------
    // Singleton Implementation
    // -------------------------------------------------------------------------

    /**
     * The sole instance of this class.
     *
     * 'volatile' is critical here. Without it, the JVM or CPU may reorder
     * instructions. Specifically, "instance = new Logger()" involves:
     *   1. Allocate memory
     *   2. Initialize the object
     *   3. Assign the reference to 'instance'
     * Without volatile, step 3 could occur before step 2, causing another
     * thread to see a partially constructed object.
     */
    private static volatile Singleton instance;

    /** The log entries collected by this logger. */
    private final List<String> logs = new ArrayList<>();

    /** Application name stored for context in log messages. */
    private final String applicationName;

    /**
     * Private constructor — prevents direct instantiation from outside.
     * Called only once, the first time getInstance() is invoked.
     */
    private Singleton() {
        this.applicationName = "MyApp";
        log("Logger initialized.");
    }

    /**
     * Returns the sole instance of Logger, creating it if necessary.
     *
     * Double-Checked Locking pattern:
     *   - First null check avoids expensive synchronization on every call once
     *     the instance is already created (common case).
     *   - The synchronized block + second null check ensures only one thread
     *     creates the instance even under concurrent access.
     *
     * @return the singleton Logger instance
     */
    public static Singleton getInstance() {
        if (instance == null) {                     // First check — no locking
            synchronized (Singleton.class) {
                if (instance == null) {             // Second check — with lock
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Logger Business Logic
    // -------------------------------------------------------------------------

    /**
     * Logs an INFO-level message.
     *
     * @param message the message to log
     */
    public void log(String message) {
        String entry = "[INFO][" + applicationName + "] " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    /**
     * Logs a WARNING-level message.
     *
     * @param message the message to log
     */
    public void warn(String message) {
        String entry = "[WARN][" + applicationName + "] " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    /**
     * Logs an ERROR-level message.
     *
     * @param message the message to log
     */
    public void error(String message) {
        String entry = "[ERROR][" + applicationName + "] " + message;
        logs.add(entry);
        System.out.println(entry);
    }

    /**
     * Returns all collected log entries.
     *
     * @return an unmodifiable view of the log entries
     */
    public List<String> getLogs() {
        return List.copyOf(logs);
    }

    /**
     * Returns the number of log entries recorded.
     *
     * @return log count
     */
    public int getLogCount() {
        return logs.size();
    }

    // -------------------------------------------------------------------------
    // Client Code / Demo
    // -------------------------------------------------------------------------

    /**
     * Demonstrates that multiple calls to getInstance() return the exact
     * same object, and shows basic logging functionality.
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Singleton Pattern Demo ===\n");

        // --- Basic Usage ---
        Singleton logger1 = Singleton.getInstance();
        Singleton logger2 = Singleton.getInstance();

        // Both variables point to the SAME instance
        System.out.println("\nAre logger1 and logger2 the same instance? "
                + (logger1 == logger2));          // true
        System.out.println("logger1 hashCode: " + System.identityHashCode(logger1));
        System.out.println("logger2 hashCode: " + System.identityHashCode(logger2));

        // --- Using the logger from different "modules" ---
        System.out.println("\n--- Module A ---");
        Singleton.getInstance().log("Module A started.");
        Singleton.getInstance().log("Module A processed 42 records.");

        System.out.println("\n--- Module B ---");
        Singleton.getInstance().warn("Module B: disk usage above 80%.");
        Singleton.getInstance().error("Module B: failed to connect to cache.");

        // --- Verify all logs are in one place ---
        System.out.println("\n--- All Logs (" + logger1.getLogCount() + " entries) ---");
        logger1.getLogs().forEach(entry -> System.out.println("  " + entry));

        // --- Thread-Safety Demo ---
        System.out.println("\n--- Thread-Safety Demo ---");
        Runnable task = () -> {
            Singleton s = Singleton.getInstance();
            s.log(Thread.currentThread().getName() + " obtained instance: "
                    + System.identityHashCode(s));
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        Thread t3 = new Thread(task, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("\nAll threads received the same instance (same hashCode).");
    }
}
