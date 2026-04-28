/**
 * Factory Method Pattern
 *
 * Intent: Define an interface for creating an object, but let subclasses
 *         decide which class to instantiate.
 *
 * Example: A logistics platform that supports different transport modes.
 * The base Logistics class defines the workflow but delegates the creation
 * of the specific Transport (Truck, Ship, Drone) to subclasses.
 *
 * Roles:
 *   Product          -> Transport (interface)
 *   ConcreteProduct  -> Truck, Ship, Drone
 *   Creator          -> Logistics (abstract class with factory method)
 *   ConcreteCreator  -> RoadLogistics, SeaLogistics, AirLogistics
 */
public class FactoryMethod {

    // =========================================================================
    // Product Interface
    // =========================================================================

    /**
     * Defines the contract for all transport vehicles.
     * ConcreteCreators produce objects that implement this interface.
     */
    interface Transport {
        /** Delivers the cargo to the destination. */
        void deliver(String cargo, String destination);

        /** Returns a description of this transport type. */
        String getDescription();
    }

    // =========================================================================
    // Concrete Products
    // =========================================================================

    /** A land-based truck for road delivery. */
    static class Truck implements Transport {
        @Override
        public void deliver(String cargo, String destination) {
            System.out.println("[Truck] Driving cargo '" + cargo
                    + "' to " + destination + " by road.");
        }

        @Override
        public String getDescription() {
            return "Truck (road transport, up to 20 tonnes)";
        }
    }

    /** A cargo ship for sea delivery. */
    static class Ship implements Transport {
        @Override
        public void deliver(String cargo, String destination) {
            System.out.println("[Ship] Sailing cargo '" + cargo
                    + "' to " + destination + " by sea.");
        }

        @Override
        public String getDescription() {
            return "Cargo Ship (sea transport, up to 50,000 tonnes)";
        }
    }

    /** A delivery drone for small, urgent packages by air. */
    static class Drone implements Transport {
        @Override
        public void deliver(String cargo, String destination) {
            System.out.println("[Drone] Flying cargo '" + cargo
                    + "' to " + destination + " by air.");
        }

        @Override
        public String getDescription() {
            return "Drone (air transport, up to 5 kg)";
        }
    }

    // =========================================================================
    // Creator (Abstract)
    // =========================================================================

    /**
     * The abstract Creator class.
     *
     * It declares the factory method 'createTransport()' that subclasses
     * must implement. The Creator also contains core business logic that
     * relies on the Product interface — it never references concrete products.
     */
    static abstract class Logistics {

        /**
         * The Factory Method.
         * Subclasses override this to create the appropriate Transport.
         *
         * @return a Transport instance suitable for this logistics type
         */
        protected abstract Transport createTransport();

        /**
         * Core business logic that uses the product created by the factory
         * method. This method is decoupled from any concrete product type.
         *
         * @param cargo       the item to ship
         * @param destination where to deliver it
         */
        public void planDelivery(String cargo, String destination) {
            // Create the transport using the factory method
            Transport transport = createTransport();

            System.out.println("\n--- Planning Delivery ---");
            System.out.println("Transport: " + transport.getDescription());
            transport.deliver(cargo, destination);
            System.out.println("Delivery planned successfully.");
        }
    }

    // =========================================================================
    // Concrete Creators
    // =========================================================================

    /**
     * Creates Truck transports for road-based logistics.
     */
    static class RoadLogistics extends Logistics {
        @Override
        protected Transport createTransport() {
            return new Truck();
        }
    }

    /**
     * Creates Ship transports for sea-based logistics.
     */
    static class SeaLogistics extends Logistics {
        @Override
        protected Transport createTransport() {
            return new Ship();
        }
    }

    /**
     * Creates Drone transports for air-based, small-package logistics.
     */
    static class AirLogistics extends Logistics {
        @Override
        protected Transport createTransport() {
            return new Drone();
        }
    }

    // =========================================================================
    // Client Code
    // =========================================================================

    /**
     * The client works only with the abstract Logistics and Transport types.
     * It can be configured with any concrete logistics implementation.
     */
    static void clientCode(Logistics logistics, String cargo, String destination) {
        logistics.planDelivery(cargo, destination);
    }

    public static void main(String[] args) {
        System.out.println("=== Factory Method Pattern Demo ===");

        System.out.println("\n>> Road Logistics:");
        clientCode(new RoadLogistics(), "Electronics", "Berlin");

        System.out.println("\n>> Sea Logistics:");
        clientCode(new SeaLogistics(), "Automobiles", "Shanghai");

        System.out.println("\n>> Air Logistics (urgent small package):");
        clientCode(new AirLogistics(), "Medical Supplies", "London");

        // The client code can also switch implementations at runtime
        System.out.println("\n>> Runtime switch (config-driven):");
        String configuredMode = "road"; // could come from a config file
        Logistics logistics;
        switch (configuredMode) {
            case "sea"  -> logistics = new SeaLogistics();
            case "air"  -> logistics = new AirLogistics();
            default     -> logistics = new RoadLogistics();
        }
        clientCode(logistics, "Furniture", "Paris");
    }
}
