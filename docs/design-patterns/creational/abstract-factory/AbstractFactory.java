/**
 * Abstract Factory Pattern
 *
 * Intent: Provide an interface for creating families of related objects
 *         without specifying their concrete classes.
 *
 * Example: A cross-platform UI toolkit.
 * The factory creates consistent families of UI widgets (Button, Checkbox)
 * for different operating systems (Windows, macOS).
 *
 * Roles:
 *   AbstractFactory   -> UIFactory
 *   ConcreteFactory   -> WindowsFactory, MacOSFactory
 *   AbstractProductA  -> Button
 *   AbstractProductB  -> Checkbox
 *   ConcreteProductA1 -> WindowsButton
 *   ConcreteProductA2 -> MacOSButton
 *   ConcreteProductB1 -> WindowsCheckbox
 *   ConcreteProductB2 -> MacOSCheckbox
 *   Client            -> Application
 */
public class AbstractFactory {

    // =========================================================================
    // Abstract Products
    // =========================================================================

    /** Abstract product: Button. Each OS provides its own concrete version. */
    interface Button {
        void render();
        void onClick(Runnable action);
    }

    /** Abstract product: Checkbox. Each OS provides its own concrete version. */
    interface Checkbox {
        void render();
        void onToggle(Runnable action);
    }

    /** Abstract product: TextField. Each OS provides its own concrete version. */
    interface TextField {
        void render();
        String getText();
    }

    // =========================================================================
    // Concrete Products — Windows Family
    // =========================================================================

    static class WindowsButton implements Button {
        private String label;

        WindowsButton(String label) { this.label = label; }

        @Override
        public void render() {
            System.out.println("[Windows] Rendering rectangular Button: [" + label + "]");
        }

        @Override
        public void onClick(Runnable action) {
            System.out.println("[Windows] Button '" + label + "' clicked.");
            action.run();
        }
    }

    static class WindowsCheckbox implements Checkbox {
        private String label;
        private boolean checked = false;

        WindowsCheckbox(String label) { this.label = label; }

        @Override
        public void render() {
            System.out.println("[Windows] Rendering Checkbox: [" + (checked ? "X" : " ") + "] " + label);
        }

        @Override
        public void onToggle(Runnable action) {
            checked = !checked;
            System.out.println("[Windows] Checkbox '" + label + "' toggled to: " + checked);
            action.run();
        }
    }

    static class WindowsTextField implements TextField {
        private String text = "";

        @Override
        public void render() {
            System.out.println("[Windows] Rendering TextField: |" + text + "|");
        }

        @Override
        public String getText() { return text; }
    }

    // =========================================================================
    // Concrete Products — macOS Family
    // =========================================================================

    static class MacOSButton implements Button {
        private String label;

        MacOSButton(String label) { this.label = label; }

        @Override
        public void render() {
            System.out.println("[macOS] Rendering rounded Button: (" + label + ")");
        }

        @Override
        public void onClick(Runnable action) {
            System.out.println("[macOS] Button '" + label + "' clicked with animation.");
            action.run();
        }
    }

    static class MacOSCheckbox implements Checkbox {
        private String label;
        private boolean checked = false;

        MacOSCheckbox(String label) { this.label = label; }

        @Override
        public void render() {
            System.out.println("[macOS] Rendering Checkbox: [" + (checked ? "✓" : "○") + "] " + label);
        }

        @Override
        public void onToggle(Runnable action) {
            checked = !checked;
            System.out.println("[macOS] Checkbox '" + label + "' toggled with slide animation: " + checked);
            action.run();
        }
    }

    static class MacOSTextField implements TextField {
        private String text = "";

        @Override
        public void render() {
            System.out.println("[macOS] Rendering TextField with shadow: ╔" + text + "╗");
        }

        @Override
        public String getText() { return text; }
    }

    // =========================================================================
    // Abstract Factory
    // =========================================================================

    /**
     * The Abstract Factory interface declares creation methods for each
     * distinct product type. Concrete factories implement these to produce
     * products belonging to a consistent family.
     */
    interface UIFactory {
        Button createButton(String label);
        Checkbox createCheckbox(String label);
        TextField createTextField();
        String getPlatformName();
    }

    // =========================================================================
    // Concrete Factories
    // =========================================================================

    /**
     * Creates the Windows family of UI components.
     * All products created here share Windows look-and-feel.
     */
    static class WindowsFactory implements UIFactory {
        @Override
        public Button createButton(String label)    { return new WindowsButton(label); }

        @Override
        public Checkbox createCheckbox(String label) { return new WindowsCheckbox(label); }

        @Override
        public TextField createTextField()           { return new WindowsTextField(); }

        @Override
        public String getPlatformName()              { return "Windows"; }
    }

    /**
     * Creates the macOS family of UI components.
     * All products created here share macOS look-and-feel.
     */
    static class MacOSFactory implements UIFactory {
        @Override
        public Button createButton(String label)    { return new MacOSButton(label); }

        @Override
        public Checkbox createCheckbox(String label) { return new MacOSCheckbox(label); }

        @Override
        public TextField createTextField()           { return new MacOSTextField(); }

        @Override
        public String getPlatformName()              { return "macOS"; }
    }

    // =========================================================================
    // Client
    // =========================================================================

    /**
     * The Application class uses only abstract types (UIFactory, Button, etc.).
     * It never references any concrete class — fully decoupled from the OS.
     * The concrete factory is injected at construction time.
     */
    static class Application {
        private final UIFactory factory;
        private Button submitButton;
        private Checkbox agreeCheckbox;
        private TextField nameField;

        Application(UIFactory factory) {
            this.factory = factory;
            buildUI();
        }

        /** Creates UI components using the injected factory. */
        private void buildUI() {
            System.out.println("\n[App] Building UI for platform: " + factory.getPlatformName());
            submitButton  = factory.createButton("Submit");
            agreeCheckbox = factory.createCheckbox("I agree to the terms");
            nameField     = factory.createTextField();
        }

        /** Renders all UI components. */
        public void render() {
            System.out.println("[App] Rendering UI...");
            nameField.render();
            agreeCheckbox.render();
            submitButton.render();
        }

        /** Simulates user interactions. */
        public void simulateUserInteraction() {
            System.out.println("[App] Simulating user interaction...");
            agreeCheckbox.onToggle(() -> System.out.println("  -> Terms accepted."));
            submitButton.onClick(() -> System.out.println("  -> Form submitted!"));
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Abstract Factory Pattern Demo ===");

        // Determine platform (in a real app, detect OS at startup)
        String os = System.getProperty("os.name", "Windows").toLowerCase();

        UIFactory factory;
        if (os.contains("mac")) {
            System.out.println("\nDetected macOS — using MacOSFactory");
            factory = new MacOSFactory();
        } else {
            System.out.println("\nDetected Windows/Other — using WindowsFactory");
            factory = new WindowsFactory();
        }

        Application app = new Application(factory);
        app.render();
        app.simulateUserInteraction();

        // ---- Show both families side-by-side ----
        System.out.println("\n--- Windows Family ---");
        Application winApp = new Application(new WindowsFactory());
        winApp.render();

        System.out.println("\n--- macOS Family ---");
        Application macApp = new Application(new MacOSFactory());
        macApp.render();
    }
}
