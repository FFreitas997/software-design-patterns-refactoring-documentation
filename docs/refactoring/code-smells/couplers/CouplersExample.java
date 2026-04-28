import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * CouplersExample.java
 *
 * Demonstrates all five Coupler code smells and their refactored counterparts.
 *
 * Smells covered:
 *   1. Feature Envy              → Move Method to the envied class
 *   2. Inappropriate Intimacy    → Encapsulate internals, remove direct field access
 *   3. Message Chains            → Hide Delegate behind a shortcut method
 *   4. Middle Man                → Remove Middle Man; call delegate directly
 *   5. Incomplete Library Class  → Introduce Foreign Method / Local Extension
 */
public class CouplersExample {

    // =========================================================================
    // 1. FEATURE ENVY — before and after
    // =========================================================================

    static class CarDirty {
        double dailyRate;
        boolean isLuxury;

        CarDirty(double dailyRate, boolean isLuxury) {
            this.dailyRate = dailyRate;
            this.isLuxury  = isLuxury;
        }
    }

    // SMELL: RentalDirty uses CarDirty's data more than its own.
    static class RentalDirty {
        CarDirty car;
        int days;

        RentalDirty(CarDirty car, int days) { this.car = car; this.days = days; }

        // Feature Envy: all computation uses car's fields
        double calculateCost() {
            double base      = car.dailyRate * days;
            double surcharge = car.isLuxury  ? base * 0.20 : 0;
            return base + surcharge;
        }
    }

    // REFACTORED: cost calculation moved into Car.
    static class Car {
        private final double  dailyRate;
        private final boolean isLuxury;

        Car(double dailyRate, boolean isLuxury) {
            this.dailyRate = dailyRate;
            this.isLuxury  = isLuxury;
        }

        double calculateRentalCost(int days) {
            double base      = dailyRate * days;
            double surcharge = isLuxury  ? base * 0.20 : 0;
            return base + surcharge;
        }
    }

    static class Rental {
        private final Car car;
        private final int days;

        Rental(Car car, int days) { this.car = car; this.days = days; }

        double calculateCost() { return car.calculateRentalCost(days); }
    }

    // =========================================================================
    // 2. INAPPROPRIATE INTIMACY — before and after
    // =========================================================================

    // SMELL: OrderDirty reaches into CustomerDirty's package-private list.
    static class CustomerDirty {
        List<Double> specialDiscounts = new ArrayList<>(); // exposed internals
    }

    static class OrderDirty {
        CustomerDirty customer;
        OrderDirty(CustomerDirty c) { this.customer = c; }

        double applyBestDiscount(double price) {
            // Inappropriate intimacy — directly touching customer's internal list
            double best = customer.specialDiscounts.stream()
                    .mapToDouble(Double::doubleValue).max().orElse(0);
            return price * (1 - best);
        }
    }

    // REFACTORED: Customer owns its discount logic.
    static class Customer {
        private final List<Double> specialDiscounts = new ArrayList<>();

        void addDiscount(double rate) { specialDiscounts.add(rate); }

        double getBestDiscount() {
            return specialDiscounts.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        }
    }

    static class Order {
        private final Customer customer;
        Order(Customer c) { this.customer = c; }

        double applyBestDiscount(double price) {
            return price * (1 - customer.getBestDiscount());
        }
    }

    // =========================================================================
    // 3. MESSAGE CHAINS — before and after
    // =========================================================================

    static class Address {
        private final String city;
        Address(String city) { this.city = city; }
        String getCity() { return city; }
    }

    static class CustomerWithAddress {
        private final Address address;
        CustomerWithAddress(Address a) { this.address = a; }
        Address getAddress() { return address; }
    }

    // SMELL: caller navigates a.getB().getC() chain.
    static class OrderWithChainDirty {
        private final CustomerWithAddress customer;
        OrderWithChainDirty(CustomerWithAddress c) { this.customer = c; }

        void printCustomerCity() {
            // Message chain — caller knows internal structure
            System.out.println(customer.getAddress().getCity());
        }
    }

    // REFACTORED: Hide the chain behind a shortcut on OrderWithChain.
    static class OrderWithChain {
        private final CustomerWithAddress customer;
        OrderWithChain(CustomerWithAddress c) { this.customer = c; }

        String getCustomerCity() { return customer.getAddress().getCity(); } // hides the chain

        void printCustomerCity() {
            System.out.println(getCustomerCity());
        }
    }

    // =========================================================================
    // 4. MIDDLE MAN — before and after
    // =========================================================================

    static class Department {
        String getManager() { return "Alice"; }
        int    headCount()  { return 12; }
    }

    // SMELL: PersonMiddleMan does nothing but delegate — it is pure overhead.
    static class PersonMiddleMan {
        private final Department department;
        PersonMiddleMan(Department d) { this.department = d; }

        String getManager() { return department.getManager(); } // pure delegation
        int    headCount()  { return department.headCount();  } // pure delegation
    }

    // REFACTORED: remove the middle man — callers use Department directly.
    // (No new class needed — just call department.getManager() at the call site)

    // =========================================================================
    // 5. INCOMPLETE LIBRARY CLASS — before and after
    // =========================================================================

    // SMELL: date arithmetic repeated wherever a "next week" date is needed.
    @SuppressWarnings("deprecation")
    static Date nextWeekDirty(Date date) {
        // Magic number, duplicated everywhere
        return new Date(date.getTime() + 7L * 24 * 60 * 60 * 1000);
    }

    // FIX A: Introduce a Foreign Method — static utility in one authoritative place.
    static class DateUtils {
        private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

        static Date addDays(Date date, int days) {
            return new Date(date.getTime() + (long) days * MILLIS_PER_DAY);
        }
    }

    // FIX B: Introduce a Local Extension — subclass adds the missing capability.
    @SuppressWarnings("deprecation")
    static class ExtendedDate extends Date {
        private static final long MILLIS_PER_DAY = 24L * 60 * 60 * 1000;

        ExtendedDate(long time) { super(time); }

        ExtendedDate addDays(int days) {
            return new ExtendedDate(this.getTime() + (long) days * MILLIS_PER_DAY);
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Feature Envy ===");
        Car    car    = new Car(50.0, true);
        Rental rental = new Rental(car, 3);
        System.out.printf("Rental cost: %.2f%n", rental.calculateCost());

        System.out.println("\n=== 2. Inappropriate Intimacy ===");
        Customer customer = new Customer();
        customer.addDiscount(0.10);
        customer.addDiscount(0.25);
        Order order = new Order(customer);
        System.out.printf("Price after best discount: %.2f%n", order.applyBestDiscount(200.0));

        System.out.println("\n=== 3. Message Chains ===");
        Address              addr     = new Address("London");
        CustomerWithAddress  cust     = new CustomerWithAddress(addr);
        OrderWithChain       chainOrder = new OrderWithChain(cust);
        chainOrder.printCustomerCity();

        System.out.println("\n=== 4. Middle Man ===");
        Department dept = new Department();
        // Call Department directly — no need for PersonMiddleMan
        System.out.println("Manager  : " + dept.getManager());
        System.out.println("HeadCount: " + dept.headCount());

        System.out.println("\n=== 5. Incomplete Library Class ===");
        Date today    = new Date();
        Date nextWeek = DateUtils.addDays(today, 7);
        System.out.println("Today    : " + today);
        System.out.println("Next week: " + nextWeek);

        ExtendedDate extToday = new ExtendedDate(today.getTime());
        System.out.println("Extended +7: " + extToday.addDays(7));
    }
}
