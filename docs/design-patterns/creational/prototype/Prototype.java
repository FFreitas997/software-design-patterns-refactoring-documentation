import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Prototype Pattern
 *
 * Intent: Specify the kinds of objects to create using a prototypical instance,
 *         and create new objects by copying (cloning) this prototype.
 *
 * Example: A graphic editor where shapes can be cloned.
 *   - Demonstrates both shallow and deep copy concerns
 *   - Includes a Prototype Registry for managing named prototypes
 *
 * Roles:
 *   Prototype         -> Shape (interface with clone())
 *   ConcretePrototype -> Circle, Rectangle, CompoundShape
 *   Client            -> GraphicEditor, PrototypeRegistry
 */
public class Prototype {

    // =========================================================================
    // Prototype Interface
    // =========================================================================

    /**
     * The Prototype interface declares the cloning method.
     * Using our own interface instead of Java's Cloneable gives us a
     * cleaner, type-safe API and better control over deep vs. shallow copy.
     */
    interface Shape {
        /** Creates and returns a deep copy of this shape. */
        Shape clone();

        /** Returns the area of the shape. */
        double area();

        /** Returns a description of the shape. */
        String describe();
    }

    // =========================================================================
    // Concrete Prototypes
    // =========================================================================

    /**
     * A mutable Color object — demonstrates why deep copy matters.
     * If we shallow-copy a Shape that contains a Color, both copies
     * share the same Color object, so changing one affects the other.
     */
    static class Color {
        int r, g, b;

        Color(int r, int g, int b) { this.r = r; this.g = g; this.b = b; }

        /** Returns a copy of this color. */
        Color copy() { return new Color(r, g, b); }

        @Override
        public String toString() { return "rgb(" + r + "," + g + "," + b + ")"; }
    }

    /** A Circle shape. */
    static class Circle implements Shape {
        private double x, y;       // center position
        private double radius;
        private Color color;        // mutable — must be deep-copied

        Circle(double x, double y, double radius, Color color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
        }

        /**
         * Deep copy: copies all fields including the mutable Color object.
         * If we used super.clone() without copying 'color', then changing
         * this.color in the original would affect the clone and vice versa.
         */
        @Override
        public Circle clone() {
            return new Circle(x, y, radius, color.copy());   // deep copy color
        }

        @Override
        public double area() { return Math.PI * radius * radius; }

        @Override
        public String describe() {
            return String.format("Circle[center=(%.1f,%.1f), r=%.1f, color=%s]",
                    x, y, radius, color);
        }

        public void setColor(Color color) { this.color = color; }
        public void move(double dx, double dy) { x += dx; y += dy; }
    }

    /** A Rectangle shape. */
    static class Rectangle implements Shape {
        private double x, y;       // top-left corner
        private double width, height;
        private Color color;

        Rectangle(double x, double y, double width, double height, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }

        @Override
        public Rectangle clone() {
            return new Rectangle(x, y, width, height, color.copy());  // deep copy
        }

        @Override
        public double area() { return width * height; }

        @Override
        public String describe() {
            return String.format("Rectangle[pos=(%.1f,%.1f), size=%.1fx%.1f, color=%s]",
                    x, y, width, height, color);
        }

        public void setColor(Color color) { this.color = color; }
        public void resize(double factor) { width *= factor; height *= factor; }
    }

    /**
     * A CompoundShape is a composite of other shapes.
     * Cloning it requires deep-copying the list and each contained shape.
     */
    static class CompoundShape implements Shape {
        private final List<Shape> children = new ArrayList<>();

        CompoundShape(Shape... shapes) {
            for (Shape s : shapes) children.add(s);
        }

        /** Deep copy: clones each child shape individually. */
        @Override
        public CompoundShape clone() {
            CompoundShape copy = new CompoundShape();
            for (Shape child : children) {
                copy.children.add(child.clone());  // recursively clone each child
            }
            return copy;
        }

        @Override
        public double area() {
            return children.stream().mapToDouble(Shape::area).sum();
        }

        @Override
        public String describe() {
            StringBuilder sb = new StringBuilder("CompoundShape[\n");
            for (Shape s : children) sb.append("  ").append(s.describe()).append("\n");
            sb.append("]");
            return sb.toString();
        }

        public void add(Shape s) { children.add(s); }
    }

    // =========================================================================
    // Prototype Registry
    // =========================================================================

    /**
     * A registry that stores named prototypes and clones them on demand.
     * This is the "Prototype Manager" variant of the pattern.
     */
    static class ShapeRegistry {
        private final Map<String, Shape> prototypes = new HashMap<>();

        /** Registers a prototype under a given name. */
        public void register(String name, Shape prototype) {
            prototypes.put(name, Objects.requireNonNull(prototype));
            System.out.println("[Registry] Registered prototype: " + name);
        }

        /**
         * Returns a clone of the registered prototype.
         *
         * @param name the registered prototype name
         * @return a fresh clone of the prototype
         * @throws IllegalArgumentException if no prototype is registered under that name
         */
        public Shape get(String name) {
            Shape prototype = prototypes.get(name);
            if (prototype == null)
                throw new IllegalArgumentException("No prototype registered: " + name);
            return prototype.clone();
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Prototype Pattern Demo ===\n");

        // --- 1. Basic Cloning ---
        System.out.println("--- 1. Basic Cloning ---");
        Circle original = new Circle(0, 0, 5.0, new Color(255, 0, 0));
        Circle clone1   = original.clone();

        System.out.println("Original: " + original.describe());
        System.out.println("Clone:    " + clone1.describe());

        // Modify the clone — should NOT affect the original
        clone1.move(10, 20);
        clone1.setColor(new Color(0, 0, 255));

        System.out.println("\nAfter modifying clone:");
        System.out.println("Original: " + original.describe());
        System.out.println("Clone:    " + clone1.describe());

        // --- 2. Deep Copy with CompoundShape ---
        System.out.println("\n--- 2. Deep Copy (CompoundShape) ---");
        CompoundShape logo = new CompoundShape(
                new Circle(0, 0, 3.0, new Color(255, 165, 0)),
                new Rectangle(5, 0, 4.0, 2.0, new Color(0, 128, 0))
        );
        CompoundShape logoCopy = logo.clone();

        System.out.println("Original logo:\n" + logo.describe());
        System.out.println("Cloned logo (independent copy):\n" + logoCopy.describe());

        // --- 3. Prototype Registry ---
        System.out.println("\n--- 3. Prototype Registry ---");
        ShapeRegistry registry = new ShapeRegistry();
        registry.register("red-circle",    new Circle(0, 0, 5.0, new Color(255, 0, 0)));
        registry.register("blue-rectangle", new Rectangle(0, 0, 10.0, 4.0, new Color(0, 0, 255)));
        registry.register("logo",           logo);

        Shape s1 = registry.get("red-circle");
        Shape s2 = registry.get("red-circle");   // another independent clone
        Shape s3 = registry.get("blue-rectangle");

        System.out.println("\nFrom registry:");
        System.out.println("s1: " + s1.describe() + " (area=" + String.format("%.2f", s1.area()) + ")");
        System.out.println("s2: " + s2.describe() + " (area=" + String.format("%.2f", s2.area()) + ")");
        System.out.println("s1 == s2? " + (s1 == s2));   // false — different objects
        System.out.println("s3: " + s3.describe() + " (area=" + String.format("%.2f", s3.area()) + ")");

        Shape clonedLogo = registry.get("logo");
        System.out.println("\nCloned logo from registry (area=" + String.format("%.2f", clonedLogo.area()) + "):\n"
                + clonedLogo.describe());
    }
}
