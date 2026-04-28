import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Builder Pattern
 *
 * Intent: Separate the construction of a complex object from its
 *         representation so the same construction process can create
 *         different representations.
 *
 * Example 1: Building a House with many optional features.
 * Example 2: Building a Pizza with a fluent API and a Director.
 */
public class Builder {

    // =========================================================================
    // EXAMPLE 1: House Builder
    // =========================================================================

    /**
     * The complex product. A House has many optional attributes.
     * It is immutable once built — all fields are set only through the Builder.
     */
    static final class House {
        // Required fields
        private final int floors;
        private final String foundationType;

        // Optional fields — have defaults
        private final int rooms;
        private final int bathrooms;
        private final boolean hasGarage;
        private final boolean hasGarden;
        private final boolean hasSwimmingPool;
        private final String roofType;

        /** Private constructor — only the inner Builder can create a House. */
        private House(HouseBuilder builder) {
            this.floors           = builder.floors;
            this.foundationType   = builder.foundationType;
            this.rooms            = builder.rooms;
            this.bathrooms        = builder.bathrooms;
            this.hasGarage        = builder.hasGarage;
            this.hasGarden        = builder.hasGarden;
            this.hasSwimmingPool  = builder.hasSwimmingPool;
            this.roofType         = builder.roofType;
        }

        @Override
        public String toString() {
            return "House {"
                    + "\n  floors="         + floors
                    + "\n  foundation='"    + foundationType + '\''
                    + "\n  rooms="          + rooms
                    + "\n  bathrooms="      + bathrooms
                    + "\n  garage="         + hasGarage
                    + "\n  garden="         + hasGarden
                    + "\n  swimmingPool="   + hasSwimmingPool
                    + "\n  roof='"          + roofType + '\''
                    + "\n}";
        }

        // ------------------------------------------------------------------
        // Static Inner Builder
        // ------------------------------------------------------------------

        /**
         * The Builder for House. Provides a fluent API for step-by-step
         * construction. The build() method validates and creates the House.
         */
        static class HouseBuilder {
            // Required
            private final int floors;
            private final String foundationType;

            // Optional — with defaults
            private int rooms           = 4;
            private int bathrooms       = 1;
            private boolean hasGarage   = false;
            private boolean hasGarden   = false;
            private boolean hasSwimmingPool = false;
            private String roofType     = "Gabled";

            /**
             * Required parameters are part of the Builder's constructor,
             * ensuring a House cannot be built without them.
             */
            HouseBuilder(int floors, String foundationType) {
                if (floors < 1) throw new IllegalArgumentException("At least 1 floor required.");
                if (foundationType == null || foundationType.isBlank())
                    throw new IllegalArgumentException("Foundation type required.");
                this.floors = floors;
                this.foundationType = foundationType;
            }

            public HouseBuilder rooms(int rooms) {
                if (rooms < 1) throw new IllegalArgumentException("At least 1 room required.");
                this.rooms = rooms;
                return this;
            }

            public HouseBuilder bathrooms(int bathrooms) {
                this.bathrooms = bathrooms;
                return this;
            }

            public HouseBuilder withGarage() {
                this.hasGarage = true;
                return this;
            }

            public HouseBuilder withGarden() {
                this.hasGarden = true;
                return this;
            }

            public HouseBuilder withSwimmingPool() {
                this.hasSwimmingPool = true;
                return this;
            }

            public HouseBuilder roofType(String roofType) {
                this.roofType = roofType;
                return this;
            }

            /** Builds and returns the final, immutable House object. */
            public House build() {
                return new House(this);
            }
        }
    }

    // =========================================================================
    // EXAMPLE 2: Pizza Builder with Director
    // =========================================================================

    /** The Pizza product. */
    static final class Pizza {
        private final String size;      // "Small", "Medium", "Large"
        private final String crust;     // "Thin", "Thick", "Stuffed"
        private final String sauce;
        private final List<String> toppings;

        private Pizza(PizzaBuilder b) {
            this.size     = b.size;
            this.crust    = b.crust;
            this.sauce    = b.sauce;
            this.toppings = Collections.unmodifiableList(new ArrayList<>(b.toppings));
        }

        @Override
        public String toString() {
            return "Pizza {size=" + size + ", crust=" + crust
                    + ", sauce=" + sauce + ", toppings=" + toppings + "}";
        }
    }

    /** Abstract builder interface for Pizza. */
    interface PizzaBuilderInterface {
        PizzaBuilderInterface size(String size);
        PizzaBuilderInterface crust(String crust);
        PizzaBuilderInterface sauce(String sauce);
        PizzaBuilderInterface topping(String topping);
        Pizza build();
    }

    /** Concrete Pizza builder. */
    static class PizzaBuilder implements PizzaBuilderInterface {
        private String size    = "Medium";
        private String crust   = "Thin";
        private String sauce   = "Tomato";
        private final List<String> toppings = new ArrayList<>();

        @Override
        public PizzaBuilder size(String size)   { this.size = size;   return this; }

        @Override
        public PizzaBuilder crust(String crust) { this.crust = crust; return this; }

        @Override
        public PizzaBuilder sauce(String sauce) { this.sauce = sauce; return this; }

        @Override
        public PizzaBuilder topping(String t)   { toppings.add(t);    return this; }

        @Override
        public Pizza build() { return new Pizza(this); }
    }

    /**
     * The Director knows how to construct specific standard configurations.
     * Clients don't need to know the exact steps — they just call a named
     * method on the Director.
     */
    static class PizzaDirector {
        private final PizzaBuilderInterface builder;

        PizzaDirector(PizzaBuilderInterface builder) {
            this.builder = builder;
        }

        /** Constructs a classic Margherita pizza. */
        public Pizza makeMargherita() {
            return builder
                    .size("Medium")
                    .crust("Thin")
                    .sauce("Tomato")
                    .topping("Mozzarella")
                    .topping("Fresh Basil")
                    .build();
        }

        /** Constructs a loaded meat feast pizza. */
        public Pizza makeMeatFeast() {
            return builder
                    .size("Large")
                    .crust("Thick")
                    .sauce("BBQ")
                    .topping("Pepperoni")
                    .topping("Italian Sausage")
                    .topping("Bacon")
                    .topping("Ground Beef")
                    .build();
        }

        /** Constructs a vegetarian pizza. */
        public Pizza makeVegetarian() {
            return builder
                    .size("Medium")
                    .crust("Thin")
                    .sauce("Pesto")
                    .topping("Mushrooms")
                    .topping("Bell Peppers")
                    .topping("Olives")
                    .topping("Sun-dried Tomatoes")
                    .build();
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Builder Pattern Demo ===");

        // --- House Builder (Fluent API) ---
        System.out.println("\n--- Building Houses ---");

        House simpleHouse = new House.HouseBuilder(1, "Concrete Slab")
                .rooms(3)
                .bathrooms(1)
                .build();
        System.out.println("Simple House:\n" + simpleHouse);

        House luxuryHouse = new House.HouseBuilder(3, "Reinforced Concrete")
                .rooms(8)
                .bathrooms(4)
                .withGarage()
                .withGarden()
                .withSwimmingPool()
                .roofType("Hip Roof")
                .build();
        System.out.println("\nLuxury House:\n" + luxuryHouse);

        // --- Pizza Builder with Director ---
        System.out.println("\n--- Building Pizzas (with Director) ---");

        PizzaDirector director = new PizzaDirector(new PizzaBuilder());

        System.out.println("Margherita:  " + director.makeMargherita());
        System.out.println("Meat Feast:  " + director.makeMeatFeast());
        System.out.println("Vegetarian:  " + director.makeVegetarian());

        // --- Custom Pizza (without Director) ---
        System.out.println("\n--- Custom Pizza (direct builder use) ---");
        Pizza customPizza = new PizzaBuilder()
                .size("Large")
                .crust("Stuffed")
                .sauce("Alfredo")
                .topping("Chicken")
                .topping("Spinach")
                .topping("Garlic")
                .build();
        System.out.println("Custom: " + customPizza);
    }
}
