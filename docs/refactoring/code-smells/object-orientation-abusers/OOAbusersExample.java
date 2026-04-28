import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OOAbusersExample.java
 *
 * Demonstrates all four Object-Orientation Abuser code smells
 * and their refactored counterparts.
 *
 * Smells covered:
 *   1. Switch Statements                        → Replace with Polymorphism
 *   2. Temporary Field                          → Extract Class / local variable
 *   3. Refused Bequest                          → Replace Inheritance with Delegation
 *   4. Alternative Classes with Different Interfaces → Extract Interface
 */
public class OOAbusersExample {

    // =========================================================================
    // 1. SWITCH STATEMENTS — before and after
    // =========================================================================

    // SMELL: switch on type string duplicated across multiple methods.
    static double calcShippingDirty(String method, double weight) {
        switch (method) {
            case "STANDARD":  return weight * 1.5;
            case "EXPRESS":   return weight * 3.0;
            case "OVERNIGHT": return weight * 5.0;
            default: throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

    static String describeShippingDirty(String method) {
        // Same switch, duplicated — every new method requires editing both.
        switch (method) {
            case "STANDARD":  return "3-5 business days";
            case "EXPRESS":   return "1-2 business days";
            case "OVERNIGHT": return "Next day delivery";
            default: throw new IllegalArgumentException("Unknown method: " + method);
        }
    }

    // REFACTORED: each method is a class; behaviour lives with the type.
    interface ShippingMethod {
        double   calculate(double weight);
        String   describe();
    }

    static class StandardShipping implements ShippingMethod {
        public double calculate(double weight) { return weight * 1.5; }
        public String describe()               { return "3-5 business days"; }
    }

    static class ExpressShipping implements ShippingMethod {
        public double calculate(double weight) { return weight * 3.0; }
        public String describe()               { return "1-2 business days"; }
    }

    static class OvernightShipping implements ShippingMethod {
        public double calculate(double weight) { return weight * 5.0; }
        public String describe()               { return "Next day delivery"; }
    }

    // =========================================================================
    // 2. TEMPORARY FIELD — before and after
    // =========================================================================

    // SMELL: tempDiscount is only meaningful inside processOrder().
    static class OrderProcessorDirty {
        private double tempDiscount; // ← temporary field; null/0 most of the time

        double processOrder(double orderTotal, boolean isPremium) {
            tempDiscount = isPremium ? 0.20 : 0.10;
            return orderTotal * (1 - tempDiscount);
        }
    }

    // REFACTORED: discount is a local variable — no dangling field.
    static class OrderProcessorClean {
        double processOrder(double orderTotal, boolean isPremium) {
            double discount = isPremium ? 0.20 : 0.10;
            return orderTotal * (1 - discount);
        }
    }

    // =========================================================================
    // 3. REFUSED BEQUEST — before and after
    // =========================================================================

    // SMELL: ReadOnlyListDirty inherits ArrayList but throws on mutating methods.
    static class ReadOnlyListDirty extends ArrayList<String> {
        @Override
        public boolean add(String item) {
            throw new UnsupportedOperationException("Read-only list");
        }
        @Override
        public String remove(int index) {
            throw new UnsupportedOperationException("Read-only list");
        }
        // Still inherits 30+ other methods it cannot honour safely.
    }

    // REFACTORED: compose rather than inherit; expose only the safe subset.
    static class ReadOnlyListClean {
        private final List<String> delegate;

        ReadOnlyListClean(List<String> source) {
            this.delegate = Collections.unmodifiableList(new ArrayList<>(source));
        }

        public String  get(int index)        { return delegate.get(index); }
        public int     size()                { return delegate.size(); }
        public boolean contains(Object item) { return delegate.contains(item); }

        @Override public String toString()   { return delegate.toString(); }
    }

    // =========================================================================
    // 4. ALTERNATIVE CLASSES WITH DIFFERENT INTERFACES — before and after
    // =========================================================================

    // Dummy model
    static class Report {
        final String title;
        Report(String title) { this.title = title; }
    }

    // SMELL: two classes do the same job but expose it through different names.
    static class XmlExporterDirty {
        public String exportToXml(Report report) {
            return "<report><title>" + report.title + "</title></report>";
        }
    }

    static class JsonExporterDirty {
        public String buildJson(Report report) {  // different method name!
            return "{\"title\":\"" + report.title + "\"}";
        }
    }

    // REFACTORED: unified interface makes both classes interchangeable.
    interface ReportExporter {
        String export(Report report);
    }

    static class XmlExporter implements ReportExporter {
        public String export(Report report) {
            return "<report><title>" + report.title + "</title></report>";
        }
    }

    static class JsonExporter implements ReportExporter {
        public String export(Report report) {
            return "{\"title\":\"" + report.title + "\"}";
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Switch Statements ===");
        ShippingMethod express = new ExpressShipping();
        System.out.printf("Cost: %.2f | ETA: %s%n", express.calculate(5.0), express.describe());

        System.out.println("\n=== 2. Temporary Field ===");
        OrderProcessorClean processor = new OrderProcessorClean();
        System.out.printf("Premium order total: %.2f%n", processor.processOrder(200.0, true));

        System.out.println("\n=== 3. Refused Bequest ===");
        List<String> source = List.of("apple", "banana", "cherry");
        ReadOnlyListClean readOnly = new ReadOnlyListClean(source);
        System.out.println("Read-only list: " + readOnly);
        System.out.println("Contains 'banana': " + readOnly.contains("banana"));

        System.out.println("\n=== 4. Alternative Classes with Different Interfaces ===");
        Report report = new Report("Q1 Results");
        List<ReportExporter> exporters = List.of(new XmlExporter(), new JsonExporter());
        for (ReportExporter exporter : exporters) {
            System.out.println(exporter.export(report));
        }
    }
}
