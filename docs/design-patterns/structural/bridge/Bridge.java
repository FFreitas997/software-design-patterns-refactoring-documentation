/**
 * Bridge Pattern
 *
 * Intent: Decouple an abstraction from its implementation so that the two
 *         can vary independently.
 *
 * Example: Shapes (Circle, Square, Triangle) can be rendered by different
 *          renderers (Raster/pixel-based, Vector/SVG-based, ASCII).
 *          Shapes and Renderers are two independent hierarchies.
 *
 * Without Bridge: 3 shapes × 3 renderers = 9 classes
 * With Bridge:    3 shapes + 3 renderers = 6 classes (+ any combinations)
 *
 * Roles:
 *   Implementor        -> Renderer (interface)
 *   ConcreteImplementor-> RasterRenderer, VectorRenderer, AsciiRenderer
 *   Abstraction        -> Shape (abstract class holding a Renderer)
 *   RefinedAbstraction -> Circle, Square, Triangle
 */
public class Bridge {

    // =========================================================================
    // Implementor Interface
    // =========================================================================

    /**
     * The Implementor interface. Declares rendering primitives that all
     * concrete renderers must implement.
     *
     * Note: This interface doesn't need to match the Abstraction's interface
     * exactly. In fact, the two interfaces can be very different.
     */
    interface Renderer {
        void renderCircle(double x, double y, double radius);
        void renderSquare(double x, double y, double side);
        void renderTriangle(double x1, double y1, double x2, double y2, double x3, double y3);
        String getRendererType();
    }

    // =========================================================================
    // Concrete Implementors
    // =========================================================================

    /**
     * Raster renderer — draws shapes as pixel arrays (e.g., for screen display).
     */
    static class RasterRenderer implements Renderer {
        @Override
        public void renderCircle(double x, double y, double radius) {
            System.out.printf("[Raster] Drawing circle at (%.0f,%.0f) radius=%.0f as pixels%n",
                    x, y, radius);
        }

        @Override
        public void renderSquare(double x, double y, double side) {
            System.out.printf("[Raster] Drawing square at (%.0f,%.0f) side=%.0f as pixels%n",
                    x, y, side);
        }

        @Override
        public void renderTriangle(double x1, double y1, double x2, double y2,
                                   double x3, double y3) {
            System.out.printf("[Raster] Drawing triangle (%.0f,%.0f)-(%.0f,%.0f)-(%.0f,%.0f) as pixels%n",
                    x1, y1, x2, y2, x3, y3);
        }

        @Override
        public String getRendererType() { return "Raster (PNG/BMP)"; }
    }

    /**
     * Vector renderer — draws shapes as mathematical descriptions (e.g., for SVG/PDF).
     */
    static class VectorRenderer implements Renderer {
        @Override
        public void renderCircle(double x, double y, double radius) {
            System.out.printf("[Vector] <circle cx=\"%.0f\" cy=\"%.0f\" r=\"%.0f\"/>%n",
                    x, y, radius);
        }

        @Override
        public void renderSquare(double x, double y, double side) {
            System.out.printf("[Vector] <rect x=\"%.0f\" y=\"%.0f\" width=\"%.0f\" height=\"%.0f\"/>%n",
                    x, y, side, side);
        }

        @Override
        public void renderTriangle(double x1, double y1, double x2, double y2,
                                   double x3, double y3) {
            System.out.printf("[Vector] <polygon points=\"%.0f,%.0f %.0f,%.0f %.0f,%.0f\"/>%n",
                    x1, y1, x2, y2, x3, y3);
        }

        @Override
        public String getRendererType() { return "Vector (SVG/PDF)"; }
    }

    /**
     * ASCII renderer — draws shapes using text characters (e.g., for terminals).
     */
    static class AsciiRenderer implements Renderer {
        @Override
        public void renderCircle(double x, double y, double radius) {
            System.out.printf("[ASCII] Circle at (%.0f,%.0f) r=%.0f: ( O )%n", x, y, radius);
        }

        @Override
        public void renderSquare(double x, double y, double side) {
            System.out.printf("[ASCII] Square at (%.0f,%.0f) s=%.0f: +--+%n", x, y, side);
        }

        @Override
        public void renderTriangle(double x1, double y1, double x2, double y2,
                                   double x3, double y3) {
            System.out.printf("[ASCII] Triangle: /\\  (%.0f,%.0f)%n", x3, y3);
        }

        @Override
        public String getRendererType() { return "ASCII (terminal)"; }
    }

    // =========================================================================
    // Abstraction
    // =========================================================================

    /**
     * The Abstraction. Holds a reference to the Implementor (Renderer).
     * Defines the high-level operations in terms of Implementor primitives.
     *
     * The renderer can be injected at construction and even swapped at runtime.
     */
    static abstract class Shape {
        /** The bridge to the implementation. */
        protected Renderer renderer;

        Shape(Renderer renderer) {
            this.renderer = renderer;
        }

        /** Sets a different renderer at runtime (bridge is swappable). */
        public void setRenderer(Renderer renderer) {
            this.renderer = renderer;
        }

        /** Draws the shape using the current renderer. */
        public abstract void draw();

        /** Moves the shape to a new position. */
        public abstract void moveTo(double x, double y);

        /** Returns the shape type. */
        public abstract String getShapeType();

        @Override
        public String toString() {
            return getShapeType() + " rendered by " + renderer.getRendererType();
        }
    }

    // =========================================================================
    // Refined Abstractions
    // =========================================================================

    /** A circle shape. */
    static class Circle extends Shape {
        private double x, y, radius;

        Circle(double x, double y, double radius, Renderer renderer) {
            super(renderer);
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        @Override
        public void draw() {
            renderer.renderCircle(x, y, radius);
        }

        @Override
        public void moveTo(double x, double y) {
            System.out.printf("[Circle] Moving from (%.0f,%.0f) to (%.0f,%.0f)%n",
                    this.x, this.y, x, y);
            this.x = x;
            this.y = y;
        }

        @Override
        public String getShapeType() { return "Circle"; }
    }

    /** A square shape. */
    static class Square extends Shape {
        private double x, y, side;

        Square(double x, double y, double side, Renderer renderer) {
            super(renderer);
            this.x = x;
            this.y = y;
            this.side = side;
        }

        @Override
        public void draw() {
            renderer.renderSquare(x, y, side);
        }

        @Override
        public void moveTo(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String getShapeType() { return "Square"; }
    }

    /** A triangle shape. */
    static class Triangle extends Shape {
        private double x1, y1, x2, y2, x3, y3;

        Triangle(double x1, double y1, double x2, double y2,
                 double x3, double y3, Renderer renderer) {
            super(renderer);
            this.x1 = x1; this.y1 = y1;
            this.x2 = x2; this.y2 = y2;
            this.x3 = x3; this.y3 = y3;
        }

        @Override
        public void draw() {
            renderer.renderTriangle(x1, y1, x2, y2, x3, y3);
        }

        @Override
        public void moveTo(double x, double y) {
            double dx = x - x1, dy = y - y1;
            x1 += dx; y1 += dy;
            x2 += dx; y2 += dy;
            x3 += dx; y3 += dy;
        }

        @Override
        public String getShapeType() { return "Triangle"; }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Bridge Pattern Demo ===\n");

        Renderer raster = new RasterRenderer();
        Renderer vector = new VectorRenderer();
        Renderer ascii  = new AsciiRenderer();

        System.out.println("--- Raster Renderer ---");
        Shape c1 = new Circle(5, 10, 3, raster);
        Shape s1 = new Square(0, 0, 5, raster);
        Shape t1 = new Triangle(0, 0, 10, 0, 5, 8, raster);
        c1.draw();
        s1.draw();
        t1.draw();

        System.out.println("\n--- Vector Renderer ---");
        Shape c2 = new Circle(5, 10, 3, vector);
        Shape s2 = new Square(0, 0, 5, vector);
        Shape t2 = new Triangle(0, 0, 10, 0, 5, 8, vector);
        c2.draw();
        s2.draw();
        t2.draw();

        System.out.println("\n--- ASCII Renderer ---");
        Shape c3 = new Circle(5, 10, 3, ascii);
        c3.draw();

        // Bridge is swappable at runtime
        System.out.println("\n--- Switching renderer at runtime ---");
        System.out.println("Before: " + c1);
        c1.setRenderer(vector);
        System.out.println("After:  " + c1);
        c1.draw();
    }
}
