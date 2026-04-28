import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Template Method Pattern
 *
 * Intent: Define the skeleton of an algorithm in an operation, deferring
 *         some steps to subclasses. Template Method lets subclasses redefine
 *         certain steps of an algorithm without changing the algorithm's structure.
 *
 * Example: A data mining pipeline that processes different document formats.
 *   The mining workflow is always:
 *     openFile → extractRawData → parseData → analyzeData → generateReport → closeFile
 *   Only extractRawData and parseData differ per format.
 *
 * Roles:
 *   AbstractClass -> DataMiner (abstract template)
 *   ConcreteClass -> CSVMiner, XMLMiner, JSONMiner
 */
public class TemplateMethod {

    // =========================================================================
    // Abstract Class (Template)
    // =========================================================================

    /**
     * The AbstractClass. Defines the template method 'mine()' which is the
     * skeleton of the data mining algorithm. Steps that are common to all
     * formats are implemented here. Steps that vary are declared abstract.
     *
     * Protected steps can be overridden; private steps cannot be changed.
     */
    static abstract class DataMiner {
        private String filePath;

        DataMiner(String filePath) { this.filePath = filePath; }

        /**
         * THE TEMPLATE METHOD.
         *
         * Defines the invariant algorithm structure. This method is final to
         * prevent subclasses from altering the workflow order.
         *
         * @return the analysis report
         */
        public final MiningReport mine() {
            System.out.println("\n[" + getFormatName() + "] Mining: " + filePath);

            String rawContent = openFile(filePath);          // common step
            String[] rawData  = extractRawData(rawContent);  // varies per format (abstract)
            List<DataRecord> records = parseData(rawData);   // varies per format (abstract)

            if (shouldFilter()) {
                records = filterRecords(records);             // optional hook
            }

            MiningReport report = analyzeData(records);      // common step
            generateReport(report);                          // common step (can be overridden)
            closeFile();                                     // common step

            return report;
        }

        // ------------------------------------------------------------------
        // Common steps (implemented here)
        // ------------------------------------------------------------------

        /** Opens the file and returns its raw content. */
        private String openFile(String path) {
            System.out.println("  [1] Opening file: " + path);
            // Simulate reading file content
            return simulateFileContent();
        }

        /** Analyzes parsed records and produces a report. */
        private MiningReport analyzeData(List<DataRecord> records) {
            System.out.println("  [4] Analyzing " + records.size() + " records...");
            double sum = records.stream().mapToDouble(r -> r.value).sum();
            double avg = records.isEmpty() ? 0 : sum / records.size();
            double max = records.stream().mapToDouble(r -> r.value).max().orElse(0);
            double min = records.stream().mapToDouble(r -> r.value).min().orElse(0);
            return new MiningReport(getFormatName(), records.size(), sum, avg, max, min);
        }

        /** Generates a textual report. Subclasses may override for custom formatting. */
        protected void generateReport(MiningReport report) {
            System.out.println("  [5] Report:\n" + report);
        }

        /** Closes the file (cleanup). */
        private void closeFile() {
            System.out.println("  [6] File closed.");
        }

        // ------------------------------------------------------------------
        // Abstract steps (must be implemented by subclasses)
        // ------------------------------------------------------------------

        /** Returns the name of the format this miner handles. */
        protected abstract String getFormatName();

        /**
         * Extracts raw data tokens from the file content.
         * Different for CSV (split by comma), XML (parse tags), JSON (parse JSON), etc.
         */
        protected abstract String[] extractRawData(String rawContent);

        /**
         * Parses extracted tokens into DataRecord objects.
         * Different per format.
         */
        protected abstract List<DataRecord> parseData(String[] rawData);

        // ------------------------------------------------------------------
        // Hook methods (optional — subclasses may override)
        // ------------------------------------------------------------------

        /**
         * Hook: Returns true if records should be filtered before analysis.
         * Default: no filtering. Subclasses can override to enable filtering.
         */
        protected boolean shouldFilter() { return false; }

        /**
         * Hook: Filters records. Only called if shouldFilter() returns true.
         * Default implementation removes records with negative values.
         */
        protected List<DataRecord> filterRecords(List<DataRecord> records) {
            System.out.println("  [hook] Filtering records...");
            List<DataRecord> filtered = new ArrayList<>();
            for (DataRecord r : records) {
                if (r.value >= 0) filtered.add(r);
            }
            System.out.println("  [hook] Kept " + filtered.size() + " of " + records.size() + " records.");
            return filtered;
        }

        /** Returns simulated file content (different per format in subclasses). */
        protected abstract String simulateFileContent();
    }

    // =========================================================================
    // Supporting types
    // =========================================================================

    /** A parsed data record with a label and numeric value. */
    static class DataRecord {
        final String label;
        final double value;

        DataRecord(String label, double value) {
            this.label = label;
            this.value = value;
        }

        @Override
        public String toString() { return label + "=" + value; }
    }

    /** The output report from a mining operation. */
    static class MiningReport {
        private final String format;
        private final int count;
        private final double sum, avg, max, min;

        MiningReport(String format, int count, double sum, double avg, double max, double min) {
            this.format = format;
            this.count  = count;
            this.sum    = sum;
            this.avg    = avg;
            this.max    = max;
            this.min    = min;
        }

        @Override
        public String toString() {
            return String.format("     Format: %s | Count: %d | Sum: %.2f | Avg: %.2f | Min: %.2f | Max: %.2f",
                    format, count, sum, avg, min, max);
        }
    }

    // =========================================================================
    // Concrete Classes
    // =========================================================================

    /**
     * Mines CSV files.
     * extractRawData: splits by newline
     * parseData: splits each row by comma and reads (label, value)
     */
    static class CSVMiner extends DataMiner {
        CSVMiner(String filePath) { super(filePath); }

        @Override
        protected String getFormatName() { return "CSV"; }

        @Override
        protected String[] extractRawData(String rawContent) {
            System.out.println("  [2] Extracting CSV rows...");
            return rawContent.split("\n");
        }

        @Override
        protected List<DataRecord> parseData(String[] rawData) {
            System.out.println("  [3] Parsing CSV data...");
            List<DataRecord> records = new ArrayList<>();
            for (String row : rawData) {
                String[] parts = row.trim().split(",");
                if (parts.length == 2) {
                    try {
                        records.add(new DataRecord(parts[0].trim(), Double.parseDouble(parts[1].trim())));
                    } catch (NumberFormatException ignored) {}
                }
            }
            return records;
        }

        @Override
        protected String simulateFileContent() {
            return "Jan,100.5\nFeb,200.0\nMar,150.75\nApr,-20.0\nMay,300.0";
        }

        // Enable filtering (will remove the negative Apr value)
        @Override
        protected boolean shouldFilter() { return true; }
    }

    /**
     * Mines XML files.
     * extractRawData: extracts content from <record> tags
     * parseData: parses name and value attributes
     */
    static class XMLMiner extends DataMiner {
        XMLMiner(String filePath) { super(filePath); }

        @Override
        protected String getFormatName() { return "XML"; }

        @Override
        protected String[] extractRawData(String rawContent) {
            System.out.println("  [2] Extracting XML elements...");
            // Split on the opening tag prefix (which always has attributes after it)
            List<String> entries = new ArrayList<>();
            String[] parts = rawContent.split("<record ");
            for (int i = 1; i < parts.length; i++) {
                // Grab everything up to the end of the opening tag
                int end = parts[i].indexOf(">");
                if (end >= 0) entries.add(parts[i].substring(0, end).replace("/>", "").trim());
            }
            return entries.toArray(new String[0]);
        }

        @Override
        protected List<DataRecord> parseData(String[] rawData) {
            System.out.println("  [3] Parsing XML records...");
            List<DataRecord> records = new ArrayList<>();
            for (String xml : rawData) {
                String name  = extractAttr(xml, "name");
                String value = extractAttr(xml, "value");
                if (!name.isEmpty() && !value.isEmpty()) {
                    try {
                        records.add(new DataRecord(name, Double.parseDouble(value)));
                    } catch (NumberFormatException ignored) {}
                }
            }
            return records;
        }

        private String extractAttr(String xml, String attr) {
            String tag = attr + "=\"";
            int start = xml.indexOf(tag);
            if (start < 0) return "";
            start += tag.length();
            int end = xml.indexOf("\"", start);
            return end < 0 ? "" : xml.substring(start, end);
        }

        @Override
        protected String simulateFileContent() {
            return "<data>" +
                    "<record name=\"Q1\" value=\"500.0\"></record>" +
                    "<record name=\"Q2\" value=\"620.5\"></record>" +
                    "<record name=\"Q3\" value=\"480.0\"></record>" +
                    "<record name=\"Q4\" value=\"710.25\"></record>" +
                    "</data>";
        }
    }

    /**
     * Mines JSON-like files.
     * Uses simplified parsing suitable for demo purposes.
     */
    static class JSONMiner extends DataMiner {
        JSONMiner(String filePath) { super(filePath); }

        @Override
        protected String getFormatName() { return "JSON"; }

        @Override
        protected String[] extractRawData(String rawContent) {
            System.out.println("  [2] Extracting JSON objects...");
            // Remove outer array brackets and split each object by "},{"
            String inner = rawContent.replaceAll("^\\[\\{|\\}\\]$", "").trim();
            return inner.split("\\},\\{");
        }

        @Override
        protected List<DataRecord> parseData(String[] rawData) {
            System.out.println("  [3] Parsing JSON fields...");
            List<DataRecord> records = new ArrayList<>();
            for (String entry : rawData) {
                // Each entry looks like: "label":"alpha","value":1.5
                try {
                    String[] kv = entry.replaceAll("\"", "").split(",");
                    String label = null;
                    double value = Double.NaN;
                    for (String pair : kv) {
                        String[] parts = pair.split(":", 2);
                        if (parts.length == 2) {
                            if (parts[0].trim().equals("label")) label = parts[1].trim();
                            else if (parts[0].trim().equals("value")) value = Double.parseDouble(parts[1].trim());
                        }
                    }
                    if (label != null && !Double.isNaN(value)) {
                        records.add(new DataRecord(label, value));
                    }
                } catch (Exception ignored) {}
            }
            return records;
        }

        @Override
        protected String simulateFileContent() {
            return "[{\"label\":\"alpha\",\"value\":1.5}," +
                    "{\"label\":\"beta\",\"value\":2.5}," +
                    "{\"label\":\"gamma\",\"value\":3.5}," +
                    "{\"label\":\"delta\",\"value\":4.5}]";
        }

        /** Custom report formatting for JSON output. */
        @Override
        protected void generateReport(MiningReport report) {
            System.out.println("  [5] JSON Report (custom format): " + report);
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Template Method Pattern Demo — Data Mining ===");

        DataMiner[] miners = {
            new CSVMiner("sales_2024.csv"),
            new XMLMiner("quarterly_report.xml"),
            new JSONMiner("metrics.json")
        };

        List<MiningReport> reports = new ArrayList<>();
        for (DataMiner miner : miners) {
            reports.add(miner.mine());
        }

        System.out.println("\n=== Summary of all reports ===");
        for (MiningReport r : reports) {
            System.out.println(r);
        }
    }
}
