/**
 * Decorator Pattern
 *
 * Intent: Attach additional responsibilities to an object dynamically.
 *         Decorators provide a flexible alternative to subclassing for
 *         extending functionality.
 *
 * Example: A coffee shop where beverages can have any combination of
 *          condiments (milk, soy, mocha, whip, caramel, etc.) added.
 *
 * Without Decorator: 3 drinks × 2^5 condiments = 96 subclasses
 * With Decorator:    3 drinks + 5 condiments = 8 classes, unlimited combos
 *
 * Roles:
 *   Component        -> Beverage (interface)
 *   ConcreteComponent-> Espresso, DarkRoast, HouseBlend
 *   Decorator        -> CondimentDecorator (abstract)
 *   ConcreteDecorator-> Milk, Soy, Mocha, Whip, Caramel
 */
public class Decorator {

    // =========================================================================
    // Component Interface
    // =========================================================================

    /**
     * The Component interface. Both base beverages and decorators implement
     * this, so they can be used interchangeably.
     */
    interface Beverage {
        String getDescription();
        double getCost();
        int getCalories();
    }

    // =========================================================================
    // Concrete Components (base beverages)
    // =========================================================================

    static class Espresso implements Beverage {
        @Override public String getDescription() { return "Espresso"; }
        @Override public double getCost()        { return 1.99; }
        @Override public int    getCalories()    { return 5; }
    }

    static class DarkRoast implements Beverage {
        @Override public String getDescription() { return "Dark Roast Coffee"; }
        @Override public double getCost()        { return 0.99; }
        @Override public int    getCalories()    { return 10; }
    }

    static class HouseBlend implements Beverage {
        @Override public String getDescription() { return "House Blend Coffee"; }
        @Override public double getCost()        { return 0.89; }
        @Override public int    getCalories()    { return 8; }
    }

    static class Decaf implements Beverage {
        @Override public String getDescription() { return "Decaf Coffee"; }
        @Override public double getCost()        { return 1.05; }
        @Override public int    getCalories()    { return 6; }
    }

    // =========================================================================
    // Abstract Decorator
    // =========================================================================

    /**
     * The abstract Decorator class. Wraps a Beverage and delegates all
     * operations to it by default. Concrete decorators override methods
     * to add their own behavior on top.
     */
    static abstract class CondimentDecorator implements Beverage {
        /** The wrapped component — could be a base beverage or another decorator. */
        protected final Beverage beverage;

        CondimentDecorator(Beverage beverage) {
            this.beverage = beverage;
        }

        /** By default, delegate description to the wrapped beverage. */
        @Override
        public String getDescription() {
            return beverage.getDescription();
        }

        @Override
        public double getCost() {
            return beverage.getCost();
        }

        @Override
        public int getCalories() {
            return beverage.getCalories();
        }
    }

    // =========================================================================
    // Concrete Decorators (condiments)
    // =========================================================================

    /** Adds steamed milk. */
    static class Milk extends CondimentDecorator {
        Milk(Beverage beverage) { super(beverage); }

        @Override
        public String getDescription() { return beverage.getDescription() + ", Milk"; }

        @Override
        public double getCost() { return beverage.getCost() + 0.25; }

        @Override
        public int getCalories() { return beverage.getCalories() + 35; }
    }

    /** Adds soy milk. */
    static class Soy extends CondimentDecorator {
        Soy(Beverage beverage) { super(beverage); }

        @Override
        public String getDescription() { return beverage.getDescription() + ", Soy"; }

        @Override
        public double getCost() { return beverage.getCost() + 0.20; }

        @Override
        public int getCalories() { return beverage.getCalories() + 25; }
    }

    /** Adds mocha (chocolate) syrup. */
    static class Mocha extends CondimentDecorator {
        Mocha(Beverage beverage) { super(beverage); }

        @Override
        public String getDescription() { return beverage.getDescription() + ", Mocha"; }

        @Override
        public double getCost() { return beverage.getCost() + 0.20; }

        @Override
        public int getCalories() { return beverage.getCalories() + 60; }
    }

    /** Adds whipped cream. */
    static class Whip extends CondimentDecorator {
        Whip(Beverage beverage) { super(beverage); }

        @Override
        public String getDescription() { return beverage.getDescription() + ", Whip"; }

        @Override
        public double getCost() { return beverage.getCost() + 0.10; }

        @Override
        public int getCalories() { return beverage.getCalories() + 80; }
    }

    /** Adds caramel drizzle. */
    static class Caramel extends CondimentDecorator {
        Caramel(Beverage beverage) { super(beverage); }

        @Override
        public String getDescription() { return beverage.getDescription() + ", Caramel"; }

        @Override
        public double getCost() { return beverage.getCost() + 0.15; }

        @Override
        public int getCalories() { return beverage.getCalories() + 45; }
    }

    /**
     * A "size upgrade" decorator that multiplies the cost and calories.
     * Shows decorators aren't limited to additive changes.
     */
    static class SizeUpgrade extends CondimentDecorator {
        private final String sizeLabel;
        private final double costMultiplier;
        private final double calorieMultiplier;

        SizeUpgrade(Beverage beverage, String sizeLabel,
                    double costMultiplier, double calorieMultiplier) {
            super(beverage);
            this.sizeLabel         = sizeLabel;
            this.costMultiplier    = costMultiplier;
            this.calorieMultiplier = calorieMultiplier;
        }

        @Override
        public String getDescription() {
            return sizeLabel + " " + beverage.getDescription();
        }

        @Override
        public double getCost() {
            return beverage.getCost() * costMultiplier;
        }

        @Override
        public int getCalories() {
            return (int)(beverage.getCalories() * calorieMultiplier);
        }
    }

    // =========================================================================
    // Helper to print a beverage receipt
    // =========================================================================

    static void printReceipt(Beverage b) {
        System.out.printf("  %-50s  $%.2f  (%d cal)%n",
                b.getDescription(), b.getCost(), b.getCalories());
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Decorator Pattern Demo — Coffee Shop ===\n");

        System.out.println("--- Simple orders ---");
        printReceipt(new Espresso());
        printReceipt(new DarkRoast());

        System.out.println("\n--- Decorated orders ---");

        // Espresso with double Mocha and Whip
        Beverage order1 = new Espresso();
        order1 = new Mocha(order1);
        order1 = new Mocha(order1);   // double mocha!
        order1 = new Whip(order1);
        printReceipt(order1);

        // HouseBlend with Soy, Mocha, Whip
        Beverage order2 = new HouseBlend();
        order2 = new Soy(order2);
        order2 = new Mocha(order2);
        order2 = new Whip(order2);
        printReceipt(order2);

        // Dark Roast with every condiment
        Beverage order3 = new DarkRoast();
        order3 = new Milk(order3);
        order3 = new Soy(order3);
        order3 = new Mocha(order3);
        order3 = new Caramel(order3);
        order3 = new Whip(order3);
        printReceipt(order3);

        // Large Latte (size upgrade decorator)
        Beverage order4 = new HouseBlend();
        order4 = new Milk(order4);
        order4 = new Caramel(order4);
        order4 = new SizeUpgrade(order4, "Large", 1.5, 1.5);
        printReceipt(order4);

        System.out.println("\n--- Decorators are stackable in any order ---");
        // Note: Caramel then Whip vs Whip then Caramel
        Beverage v1 = new Whip(new Caramel(new Espresso()));
        Beverage v2 = new Caramel(new Whip(new Espresso()));
        printReceipt(v1);
        printReceipt(v2);
        System.out.println("  Same cost? " + (v1.getCost() == v2.getCost()));
    }
}
