import java.util.ArrayList;
import java.util.List;

/**
 * SOLID — Five Object-Oriented Design Principles (Robert C. Martin)
 *
 * S — Single Responsibility Principle (SRP)
 * O — Open/Closed Principle            (OCP)
 * L — Liskov Substitution Principle    (LSP)
 * I — Interface Segregation Principle  (ISP)
 * D — Dependency Inversion Principle   (DIP)
 *
 * Each section shows a BEFORE (violating) and AFTER (compliant) example.
 */
public class SolidExample {

    // ═══════════════════════════════════════════════════════════════
    // S — Single Responsibility Principle
    //     A class should have ONE reason to change.
    // ═══════════════════════════════════════════════════════════════

    // --- BEFORE: one class handles persistence, emailing, and reporting ---
    static class UserServiceBad {
        public void saveUser(String name) { System.out.println("[DB]     Saving user: " + name); }
        public void sendEmail(String name) { System.out.println("[Email]  Sending welcome to: " + name); }
        public String generateReport(String name) { return "[Report] User report for: " + name; }
    }

    // --- AFTER: each class has a single responsibility ---
    static class UserRepository      { public void save(String name)           { System.out.println("[DB]     Saved: " + name); } }
    static class EmailService        { public void sendWelcome(String name)    { System.out.println("[Email]  Sent welcome to: " + name); } }
    static class UserReportGenerator { public String generate(String name)     { return "[Report] User: " + name; } }

    // ═══════════════════════════════════════════════════════════════
    // O — Open / Closed Principle
    //     Open for extension, closed for modification.
    // ═══════════════════════════════════════════════════════════════

    // --- BEFORE: adding a new type requires editing existing code ---
    static class DiscountCalculatorBad {
        public double calculate(String type, double price) {
            if ("VIP".equals(type))     return price * 0.80;
            if ("REGULAR".equals(type)) return price * 0.95;
            return price; // Every new type requires editing here
        }
    }

    // --- AFTER: new discount types extend without touching existing code ---
    interface DiscountStrategy        { double apply(double price); }
    static class VipDiscount     implements DiscountStrategy { public double apply(double p) { return p * 0.80; } }
    static class RegularDiscount implements DiscountStrategy { public double apply(double p) { return p * 0.95; } }
    static class StudentDiscount implements DiscountStrategy { public double apply(double p) { return p * 0.85; } } // added without touching calculator

    static class DiscountCalculator {
        public double calculate(DiscountStrategy strategy, double price) {
            return strategy.apply(price);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // L — Liskov Substitution Principle
    //     Subtypes must be substitutable for their base types.
    // ═══════════════════════════════════════════════════════════════

    // --- BEFORE: Square breaks Rectangle's contract ---
    static class Rectangle {
        protected int width, height;
        public void setWidth(int w)  { this.width  = w; }
        public void setHeight(int h) { this.height = h; }
        public int  area()           { return width * height; }
    }

    static class SquareBad extends Rectangle {
        @Override public void setWidth(int w)  { this.width = this.height = w; } // breaks Rectangle contract
        @Override public void setHeight(int h) { this.width = this.height = h; }
    }

    // --- AFTER: use a common abstraction; don't force inheritance  ---
    interface Shape { int area(); }

    static class RectangleGood implements Shape {
        private final int width, height;
        RectangleGood(int w, int h) { this.width = w; this.height = h; }
        public int area() { return width * height; }
    }

    static class SquareGood implements Shape {
        private final int side;
        SquareGood(int s) { this.side = s; }
        public int area() { return side * side; }
    }

    static void printArea(Shape s) {
        // Works correctly regardless of whether s is a Rectangle or Square (LSP satisfied)
        System.out.println("  Area: " + s.area());
    }

    // ═══════════════════════════════════════════════════════════════
    // I — Interface Segregation Principle
    //     Clients should not depend on methods they do not use.
    // ═══════════════════════════════════════════════════════════════

    // --- BEFORE: fat interface forces implementations to stub methods ---
    interface WorkerBad {
        void work();
        void eat();   // makes no sense for a Robot
        void sleep(); // makes no sense for a Robot
    }

    // --- AFTER: split into focused interfaces ---
    interface Workable { void work();  }
    interface Feedable { void eat();   }
    interface Restable { void sleep(); }

    static class Human implements Workable, Feedable, Restable {
        public void work()  { System.out.println("  Human: working"); }
        public void eat()   { System.out.println("  Human: eating");  }
        public void sleep() { System.out.println("  Human: sleeping");}
    }

    static class Robot implements Workable {
        public void work() { System.out.println("  Robot: working (no eat/sleep needed)"); }
    }

    // ═══════════════════════════════════════════════════════════════
    // D — Dependency Inversion Principle
    //     Depend on abstractions, not on concretions.
    // ═══════════════════════════════════════════════════════════════

    // --- BEFORE: high-level class is tightly coupled to a low-level class ---
    static class MySQLOrderRepository {
        public void save(String order) { System.out.println("  [MySQL] Saved order: " + order); }
    }

    static class OrderServiceBad {
        private final MySQLOrderRepository repo = new MySQLOrderRepository(); // coupled!
        public void placeOrder(String order) { repo.save(order); }
    }

    // --- AFTER: high-level class depends on the abstraction ---
    interface OrderRepository              { void save(String order); }
    static class MySQLRepo  implements OrderRepository { public void save(String o) { System.out.println("  [MySQL] Saved: " + o); } }
    static class MongoRepo  implements OrderRepository { public void save(String o) { System.out.println("  [Mongo] Saved: " + o); } }

    static class OrderService {
        private final OrderRepository repo;
        public OrderService(OrderRepository repo) { this.repo = repo; } // injected
        public void placeOrder(String order)       { repo.save(order); }
    }

    // ═══════════════════════════════════════════════════════════════
    // Demo
    // ═══════════════════════════════════════════════════════════════

    public static void main(String[] args) {

        System.out.println("=== S: Single Responsibility Principle ===");
        UserRepository      userRepo      = new UserRepository();
        EmailService        emailSvc      = new EmailService();
        UserReportGenerator reportGen     = new UserReportGenerator();
        userRepo.save("Alice");
        emailSvc.sendWelcome("Alice");
        System.out.println(reportGen.generate("Alice"));

        System.out.println("\n=== O: Open/Closed Principle ===");
        DiscountCalculator calc = new DiscountCalculator();
        double price = 100.0;
        System.out.println("  VIP     discount: $" + calc.calculate(new VipDiscount(),     price));
        System.out.println("  Regular discount: $" + calc.calculate(new RegularDiscount(), price));
        System.out.println("  Student discount: $" + calc.calculate(new StudentDiscount(), price));

        System.out.println("\n=== L: Liskov Substitution Principle ===");
        List<Shape> shapes = List.of(new RectangleGood(4, 5), new SquareGood(4));
        shapes.forEach(SolidExample::printArea); // same code, correct results for both

        System.out.println("\n=== I: Interface Segregation Principle ===");
        new Human().work(); new Human().eat(); new Human().sleep();
        new Robot().work(); // robot does NOT implement Feedable/Restable

        System.out.println("\n=== D: Dependency Inversion Principle ===");
        OrderService mysqlService = new OrderService(new MySQLRepo());
        OrderService mongoService = new OrderService(new MongoRepo());
        mysqlService.placeOrder("Order#1");
        mongoService.placeOrder("Order#2");
        // Swapping the database requires zero changes to OrderService
    }
}

