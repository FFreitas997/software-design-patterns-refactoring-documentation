import java.util.ArrayList;
import java.util.List;

/**
 * Visitor Pattern
 *
 * Intent: Represent an operation to be performed on elements of an object
 *         structure. Visitor lets you define a new operation without changing
 *         the classes of the elements on which it operates.
 *
 * Example: A shape library with Circle, Rectangle, and Triangle.
 *   Multiple visitors perform different operations on the shapes:
 *     - AreaCalculator   : computes the area of each shape
 *     - PerimeterCalculator : computes the perimeter of each shape
 *     - SVGExportVisitor : exports each shape to SVG markup
 *     - JSONExportVisitor: exports each shape to JSON
 *
 * Key concept — Double Dispatch:
 *   shape.accept(visitor)     // dispatch #1 — calls the right accept() by shape type
 *   visitor.visitCircle(this) // dispatch #2 — calls the right visit() by visitor type
 *
 * Roles:
 *   Visitor          -> ShapeVisitor (interface)
 *   ConcreteVisitor  -> AreaCalculator, PerimeterCalculator, SVGExportVisitor, JSONExportVisitor
 *   Element          -> Shape (interface)
 *   ConcreteElement  -> Circle, Rectangle, Triangle
 *   ObjectStructure  -> Drawing (holds a list of shapes)
 */
public class Visitor {

    // =========================================================================
    // Visitor Interface
    // =========================================================================

    /**
     * Declares a visit method for each concrete element.
     * Every new operation on shapes becomes a new ConcreteVisitor.
     */
    interface ShapeVisitor {
        void visitCircle(Circle circle);
        void visitRectangle(Rectangle rectangle);
        void visitTriangle(Triangle triangle);
    }

    // =========================================================================
    // Element Interface
    // =========================================================================

    /**
     * All shapes implement accept(). This is the key enabler of double dispatch.
     */
    interface Shape {
        /** Accept a visitor. The concrete class calls visitor.visitXxx(this). */
        void accept(ShapeVisitor visitor);

        String getName();
    }

    // =========================================================================
    // Concrete Elements
    // =========================================================================

    /** A circle defined by its radius. */
    static class Circle implements Shape {
        final String name;
        final double radius;

        Circle(String name, double radius) {
            this.name   = name;
            this.radius = radius;
        }

        @Override public String getName() { return name; }

        /**
         * Double dispatch: this method delegates to the visitor's visitCircle(),
         * so the visitor knows at compile time it's dealing with a Circle.
         */
        @Override
        public void accept(ShapeVisitor visitor) {
            visitor.visitCircle(this);
        }
    }

    /** A rectangle defined by width and height. */
    static class Rectangle implements Shape {
        final String name;
        final double width, height;

        Rectangle(String name, double width, double height) {
            this.name   = name;
            this.width  = width;
            this.height = height;
        }

        @Override public String getName() { return name; }

        @Override
        public void accept(ShapeVisitor visitor) {
            visitor.visitRectangle(this);
        }
    }

    /** A triangle defined by three side lengths (a, b, c). */
    static class Triangle implements Shape {
        final String name;
        final double a, b, c;  // side lengths

        Triangle(String name, double a, double b, double c) {
            this.name = name;
            this.a = a; this.b = b; this.c = c;
        }

        @Override public String getName() { return name; }

        @Override
        public void accept(ShapeVisitor visitor) {
            visitor.visitTriangle(this);
        }
    }

    // =========================================================================
    // Object Structure
    // =========================================================================

    /**
     * Holds a collection of shapes and allows a visitor to traverse them.
     * The client doesn't need to iterate manually.
     */
    static class Drawing {
        private final List<Shape> shapes = new ArrayList<>();

        void add(Shape shape) { shapes.add(shape); }

        /** Apply a visitor to all shapes in this drawing. */
        void accept(ShapeVisitor visitor) {
            for (Shape shape : shapes) {
                shape.accept(visitor);
            }
        }

        int size() { return shapes.size(); }
    }

    // =========================================================================
    // Concrete Visitors
    // =========================================================================

    /**
     * Visitor 1: Area Calculator
     *
     * Each visitXxx method uses the shape-specific area formula.
     * Accumulated total area is stored as visitor state.
     */
    static class AreaCalculator implements ShapeVisitor {
        private double totalArea = 0;

        @Override
        public void visitCircle(Circle c) {
            double area = Math.PI * c.radius * c.radius;
            totalArea += area;
            System.out.printf("  Circle '%s' (r=%.1f): area = %.4f%n", c.name, c.radius, area);
        }

        @Override
        public void visitRectangle(Rectangle r) {
            double area = r.width * r.height;
            totalArea += area;
            System.out.printf("  Rectangle '%s' (%.1f x %.1f): area = %.4f%n", r.name, r.width, r.height, area);
        }

        @Override
        public void visitTriangle(Triangle t) {
            // Heron's formula: area = sqrt(s*(s-a)*(s-b)*(s-c)) where s = (a+b+c)/2
            double s    = (t.a + t.b + t.c) / 2;
            double area = Math.sqrt(s * (s - t.a) * (s - t.b) * (s - t.c));
            totalArea += area;
            System.out.printf("  Triangle '%s' (%.1f,%.1f,%.1f): area = %.4f%n", t.name, t.a, t.b, t.c, area);
        }

        double getTotalArea() { return totalArea; }
    }

    /**
     * Visitor 2: Perimeter Calculator
     *
     * Computes the perimeter of each shape without modifying shape classes.
     */
    static class PerimeterCalculator implements ShapeVisitor {
        private double totalPerimeter = 0;

        @Override
        public void visitCircle(Circle c) {
            double perimeter = 2 * Math.PI * c.radius;
            totalPerimeter += perimeter;
            System.out.printf("  Circle '%s' (r=%.1f): perimeter = %.4f%n", c.name, c.radius, perimeter);
        }

        @Override
        public void visitRectangle(Rectangle r) {
            double perimeter = 2 * (r.width + r.height);
            totalPerimeter += perimeter;
            System.out.printf("  Rectangle '%s' (%.1f x %.1f): perimeter = %.4f%n", r.name, r.width, r.height, perimeter);
        }

        @Override
        public void visitTriangle(Triangle t) {
            double perimeter = t.a + t.b + t.c;
            totalPerimeter += perimeter;
            System.out.printf("  Triangle '%s' (%.1f+%.1f+%.1f): perimeter = %.4f%n", t.name, t.a, t.b, t.c, perimeter);
        }

        double getTotalPerimeter() { return totalPerimeter; }
    }

    /**
     * Visitor 3: SVG Export Visitor
     *
     * Generates SVG markup for each shape. New capability added without
     * touching any shape class.
     */
    static class SVGExportVisitor implements ShapeVisitor {
        private final StringBuilder svg = new StringBuilder();

        SVGExportVisitor() {
            svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\">\n");
        }

        @Override
        public void visitCircle(Circle c) {
            // SVG circle: cx, cy (center) and r (radius)
            int cx = 100, cy = 100;
            svg.append(String.format(
                "  <circle id=\"%s\" cx=\"%d\" cy=\"%d\" r=\"%.1f\" fill=\"steelblue\"/>\n",
                c.name, cx, cy, c.radius));
        }

        @Override
        public void visitRectangle(Rectangle r) {
            svg.append(String.format(
                "  <rect id=\"%s\" x=\"10\" y=\"10\" width=\"%.1f\" height=\"%.1f\" fill=\"tomato\"/>\n",
                r.name, r.width, r.height));
        }

        @Override
        public void visitTriangle(Triangle t) {
            // Approximate equilateral-like triangle in SVG using a polygon
            svg.append(String.format(
                "  <polygon id=\"%s\" points=\"50,10 90,80 10,80\" fill=\"mediumseagreen\"/><!-- sides: %.1f,%.1f,%.1f -->\n",
                t.name, t.a, t.b, t.c));
        }

        String getSVG() {
            return svg.toString() + "</svg>";
        }
    }

    /**
     * Visitor 4: JSON Export Visitor
     *
     * Exports each shape to a JSON array entry.
     */
    static class JSONExportVisitor implements ShapeVisitor {
        private final List<String> entries = new ArrayList<>();

        @Override
        public void visitCircle(Circle c) {
            entries.add(String.format(
                "{\"type\":\"circle\",\"name\":\"%s\",\"radius\":%.1f}",
                c.name, c.radius));
        }

        @Override
        public void visitRectangle(Rectangle r) {
            entries.add(String.format(
                "{\"type\":\"rectangle\",\"name\":\"%s\",\"width\":%.1f,\"height\":%.1f}",
                r.name, r.width, r.height));
        }

        @Override
        public void visitTriangle(Triangle t) {
            entries.add(String.format(
                "{\"type\":\"triangle\",\"name\":\"%s\",\"a\":%.1f,\"b\":%.1f,\"c\":%.1f}",
                t.name, t.a, t.b, t.c));
        }

        String getJSON() {
            return "[\n  " + String.join(",\n  ", entries) + "\n]";
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        // Build the object structure (Drawing)
        Drawing drawing = new Drawing();
        drawing.add(new Circle("smallCircle", 5.0));
        drawing.add(new Circle("bigCircle", 10.0));
        drawing.add(new Rectangle("square", 8.0, 8.0));
        drawing.add(new Rectangle("banner", 20.0, 4.0));
        drawing.add(new Triangle("rightTri", 3.0, 4.0, 5.0));
        drawing.add(new Triangle("equalTri", 6.0, 6.0, 6.0));

        System.out.println("=== Visitor Pattern Demo ===");
        System.out.println("Drawing contains " + drawing.size() + " shapes.\n");

        // Visitor 1: Area calculation
        System.out.println("--- Area Calculator ---");
        AreaCalculator areaVisitor = new AreaCalculator();
        drawing.accept(areaVisitor);
        System.out.printf("  >> TOTAL AREA: %.4f%n%n", areaVisitor.getTotalArea());

        // Visitor 2: Perimeter calculation
        System.out.println("--- Perimeter Calculator ---");
        PerimeterCalculator perimVisitor = new PerimeterCalculator();
        drawing.accept(perimVisitor);
        System.out.printf("  >> TOTAL PERIMETER: %.4f%n%n", perimVisitor.getTotalPerimeter());

        // Visitor 3: SVG export
        System.out.println("--- SVG Export ---");
        SVGExportVisitor svgVisitor = new SVGExportVisitor();
        drawing.accept(svgVisitor);
        System.out.println(svgVisitor.getSVG());

        // Visitor 4: JSON export
        System.out.println("--- JSON Export ---");
        JSONExportVisitor jsonVisitor = new JSONExportVisitor();
        drawing.accept(jsonVisitor);
        System.out.println(jsonVisitor.getJSON());

        // Demonstrate double dispatch
        System.out.println("\n--- Double Dispatch Proof ---");
        System.out.println("Calling shape.accept(visitor) dispatches to the correct visit method");
        System.out.println("based on BOTH the shape type AND the visitor type — no instanceof needed.");
    }
}
