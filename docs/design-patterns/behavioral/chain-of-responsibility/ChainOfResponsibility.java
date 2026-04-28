/**
 * Chain of Responsibility Pattern
 *
 * Intent: Avoid coupling the sender of a request to its receiver by giving
 *         more than one object a chance to handle the request. Chain the
 *         receiving objects and pass the request along the chain.
 *
 * Example: A technical support ticket system with escalation levels.
 *   Level 1 (Basic)    -> handles password resets, billing questions
 *   Level 2 (Technical) -> handles software bugs, configuration issues
 *   Level 3 (Expert)   -> handles critical outages, security breaches
 *   Manager             -> handles everything escalated beyond Level 3
 *
 * Roles:
 *   Handler         -> SupportHandler (abstract)
 *   ConcreteHandler -> BasicSupport, TechnicalSupport, ExpertSupport, ManagerSupport
 *   Client          -> main()
 */
public class ChainOfResponsibility {

    // =========================================================================
    // Request (Support Ticket)
    // =========================================================================

    /** Severity levels for support tickets. */
    enum Severity { LOW, MEDIUM, HIGH, CRITICAL }

    /** Represents a customer support ticket. */
    static class SupportTicket {
        private final String id;
        private final String description;
        private final Severity severity;
        private final String category;   // "billing", "technical", "security", "outage"

        SupportTicket(String id, String description, Severity severity, String category) {
            this.id          = id;
            this.description = description;
            this.severity    = severity;
            this.category    = category;
        }

        @Override
        public String toString() {
            return "[Ticket " + id + "] " + severity + "/" + category
                    + ": \"" + description + "\"";
        }
    }

    // =========================================================================
    // Handler (Abstract)
    // =========================================================================

    /**
     * The abstract Handler. Defines the interface for handling tickets and
     * maintains a reference to the next handler in the chain (successor).
     *
     * The base implementation of handle() forwards to the next handler if set.
     * Subclasses call super.handle() when they can't handle the ticket themselves.
     */
    static abstract class SupportHandler {
        private SupportHandler next;
        protected final String handlerName;

        SupportHandler(String handlerName) {
            this.handlerName = handlerName;
        }

        /**
         * Sets the next handler in the chain. Returns 'this' for fluent chaining:
         *   handler1.setNext(handler2).setNext(handler3)
         */
        public SupportHandler setNext(SupportHandler next) {
            this.next = next;
            return next;   // return next, enabling .setNext(a).setNext(b) chaining
        }

        /**
         * Handles the ticket or passes it to the next handler.
         * Subclasses should call super.handle(ticket) to pass the request up.
         */
        public void handle(SupportTicket ticket) {
            if (next != null) {
                System.out.println("  [" + handlerName + "] Escalating to next handler...");
                next.handle(ticket);
            } else {
                System.out.println("  [" + handlerName + "] ⚠️  No more handlers! Ticket unresolved: " + ticket.id);
            }
        }

        /** Convenience to print when a handler accepts a ticket. */
        protected void accept(SupportTicket ticket, String resolution) {
            System.out.println("  ✅ [" + handlerName + "] Resolved " + ticket.id
                    + ": " + resolution);
        }
    }

    // =========================================================================
    // Concrete Handlers
    // =========================================================================

    /**
     * Level 1: Basic Support
     * Handles LOW severity billing and password issues.
     */
    static class BasicSupport extends SupportHandler {
        BasicSupport() { super("Basic Support (L1)"); }

        @Override
        public void handle(SupportTicket ticket) {
            if (ticket.severity == Severity.LOW
                    && (ticket.category.equals("billing") || ticket.category.equals("password"))) {
                accept(ticket, "Guided user through self-service portal.");
            } else {
                super.handle(ticket);  // pass to next handler
            }
        }
    }

    /**
     * Level 2: Technical Support
     * Handles LOW/MEDIUM severity technical and configuration issues.
     */
    static class TechnicalSupport extends SupportHandler {
        TechnicalSupport() { super("Technical Support (L2)"); }

        @Override
        public void handle(SupportTicket ticket) {
            if ((ticket.severity == Severity.LOW || ticket.severity == Severity.MEDIUM)
                    && (ticket.category.equals("technical") || ticket.category.equals("configuration"))) {
                accept(ticket, "Diagnosed and applied technical fix.");
            } else {
                super.handle(ticket);
            }
        }
    }

    /**
     * Level 3: Expert Support
     * Handles HIGH severity issues of any technical category.
     */
    static class ExpertSupport extends SupportHandler {
        ExpertSupport() { super("Expert Support (L3)"); }

        @Override
        public void handle(SupportTicket ticket) {
            if (ticket.severity == Severity.HIGH
                    && !ticket.category.equals("security")) {
                accept(ticket, "Deep-dive analysis and patch applied.");
            } else {
                super.handle(ticket);
            }
        }
    }

    /**
     * Manager: Handles CRITICAL issues and security incidents.
     * Last resort in the chain.
     */
    static class ManagerSupport extends SupportHandler {
        ManagerSupport() { super("Manager (Escalation)"); }

        @Override
        public void handle(SupportTicket ticket) {
            if (ticket.severity == Severity.CRITICAL || ticket.category.equals("security")) {
                accept(ticket, "Crisis team mobilized. Executive briefing prepared.");
            } else {
                // If the manager can't handle it either, call base (unhandled)
                super.handle(ticket);
            }
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Chain of Responsibility — Support Tickets ===\n");

        // Build the chain: Basic → Technical → Expert → Manager
        SupportHandler basic     = new BasicSupport();
        SupportHandler technical = new TechnicalSupport();
        SupportHandler expert    = new ExpertSupport();
        SupportHandler manager   = new ManagerSupport();

        // Set up the chain using fluent setNext()
        basic.setNext(technical)
             .setNext(expert)
             .setNext(manager);

        // Define some tickets
        SupportTicket[] tickets = {
            new SupportTicket("T001", "Can't log in — password forgotten",   Severity.LOW,      "password"),
            new SupportTicket("T002", "Incorrect charge on my invoice",       Severity.LOW,      "billing"),
            new SupportTicket("T003", "App crashes on startup — v2.3.1",      Severity.MEDIUM,   "technical"),
            new SupportTicket("T004", "API misconfigured after deployment",    Severity.MEDIUM,   "configuration"),
            new SupportTicket("T005", "Database performance degraded 80%",     Severity.HIGH,     "technical"),
            new SupportTicket("T006", "Suspected unauthorized data access",    Severity.HIGH,     "security"),
            new SupportTicket("T007", "Complete service outage — all regions", Severity.CRITICAL, "outage"),
        };

        // Send each ticket into the chain starting at BasicSupport
        for (SupportTicket t : tickets) {
            System.out.println("📩 Incoming: " + t);
            basic.handle(t);
            System.out.println();
        }

        // --- Show that chain order matters ---
        System.out.println("--- Reversed chain (Manager first) ---");
        ManagerSupport m2  = new ManagerSupport();
        ExpertSupport  e2  = new ExpertSupport();
        m2.setNext(e2);  // Manager → Expert (no Basic/Technical)

        SupportTicket lowTicket = new SupportTicket("T008",
                "Billing question", Severity.LOW, "billing");
        System.out.println("📩 Incoming: " + lowTicket);
        m2.handle(lowTicket);  // Manager doesn't handle it, Expert doesn't either → unhandled
    }
}
