import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Flyweight Pattern
 *
 * Intent: Use sharing to support large numbers of fine-grained objects efficiently.
 *
 * Example: A text editor rendering millions of characters.
 *   - Intrinsic state  (shared):   character code, font name, font size, color
 *   - Extrinsic state (per-use):   x/y position on screen, bold/italic override
 *
 * Memory comparison:
 *   Without Flyweight: 1,000,000 characters × 64 bytes = ~64 MB
 *   With Flyweight:    26 flyweights × 64 bytes + 1,000,000 × 8 bytes ≈ ~8 MB
 *
 * Roles:
 *   Flyweight        -> CharacterFlyweight (interface)
 *   ConcreteFlyweight-> ConcreteCharacter
 *   FlyweightFactory -> CharacterFactory
 *   Client           -> TextEditor
 */
public class Flyweight {

    // =========================================================================
    // Flyweight Interface
    // =========================================================================

    /**
     * The Flyweight interface. The operation() method accepts extrinsic state
     * (position, formatting overrides) that varies per use.
     */
    interface CharacterFlyweight {
        /**
         * Renders the character at a specific position with optional overrides.
         *
         * @param x    column position (extrinsic)
         * @param y    row position (extrinsic)
         * @param bold additional bold override (extrinsic)
         */
        void render(int x, int y, boolean bold);

        /** Returns the intrinsic state key used to identify this flyweight. */
        String getKey();
    }

    // =========================================================================
    // Concrete Flyweight
    // =========================================================================

    /**
     * Stores intrinsic (shared, context-independent) state.
     * One instance exists per unique (character, font, size, color) combination.
     */
    static class ConcreteCharacter implements CharacterFlyweight {
        // --- Intrinsic state — stored in the flyweight, NEVER changes ---
        private final char   character;
        private final String font;
        private final int    size;
        private final String color;

        ConcreteCharacter(char character, String font, int size, String color) {
            this.character = character;
            this.font      = font;
            this.size      = size;
            this.color     = color;
        }

        /**
         * Uses the intrinsic state stored here + extrinsic state passed in
         * to perform the rendering operation.
         */
        @Override
        public void render(int x, int y, boolean bold) {
            // In a real system this would call a graphics API
            String weight = bold ? "Bold" : "Regular";
            System.out.printf("  Char='%c' at(%3d,%3d) font=%s-%dpt-%s %s%n",
                    character, x, y, font, size, color, weight);
        }

        @Override
        public String getKey() {
            return character + ":" + font + ":" + size + ":" + color;
        }

        @Override
        public String toString() {
            return "CharFlyweight{'" + character + "', " + font + ", " + size + "pt, " + color + "}";
        }
    }

    // =========================================================================
    // Flyweight Factory
    // =========================================================================

    /**
     * Creates and manages flyweight objects.
     * Returns an existing flyweight if one with the same intrinsic state
     * already exists; creates a new one otherwise.
     *
     * This ensures sharing — the same ConcreteCharacter is reused
     * for all occurrences of (char, font, size, color).
     */
    static class CharacterFactory {
        private final Map<String, CharacterFlyweight> pool = new HashMap<>();
        private int createCount = 0;

        /**
         * Returns a flyweight for the given intrinsic state.
         * Creates a new one only if none exists yet.
         */
        public CharacterFlyweight getCharacter(char c, String font, int size, String color) {
            String key = c + ":" + font + ":" + size + ":" + color;
            return pool.computeIfAbsent(key, k -> {
                createCount++;
                System.out.println("[Factory] Creating new flyweight: " + k);
                return new ConcreteCharacter(c, font, size, color);
            });
        }

        /** Returns the number of unique flyweight objects in the pool. */
        public int getPoolSize() { return pool.size(); }

        /** Returns the total number of flyweights created (never decreases). */
        public int getCreateCount() { return createCount; }

        /** Prints all flyweights currently in the pool. */
        public void printPool() {
            System.out.println("Flyweight pool (" + pool.size() + " objects):");
            pool.values().forEach(fw -> System.out.println("  " + fw));
        }
    }

    // =========================================================================
    // Client: Context Object
    // =========================================================================

    /**
     * Stores extrinsic state for one character placement.
     * Holds a reference to a flyweight but doesn't own it — the flyweight
     * is shared with all other placements of the same character style.
     */
    static class CharacterContext {
        private final CharacterFlyweight flyweight;  // shared (intrinsic)
        private final int x, y;                      // extrinsic: position
        private final boolean bold;                  // extrinsic: formatting

        CharacterContext(CharacterFlyweight flyweight, int x, int y, boolean bold) {
            this.flyweight = flyweight;
            this.x    = x;
            this.y    = y;
            this.bold = bold;
        }

        public void render() {
            flyweight.render(x, y, bold);
        }
    }

    // =========================================================================
    // Client: Text Editor
    // =========================================================================

    /**
     * Simulates a text editor that places many characters on a canvas.
     * Uses the Flyweight Factory to avoid creating redundant character objects.
     */
    static class TextEditor {
        private final CharacterFactory factory = new CharacterFactory();
        private final List<CharacterContext> placedCharacters = new ArrayList<>();

        /**
         * Adds a character to the canvas at the given position.
         *
         * @param c     the character to render
         * @param font  font name
         * @param size  font size in points
         * @param color color name
         * @param x     column
         * @param y     row
         * @param bold  whether to render bold
         */
        public void addCharacter(char c, String font, int size, String color,
                                 int x, int y, boolean bold) {
            CharacterFlyweight fw = factory.getCharacter(c, font, size, color);
            placedCharacters.add(new CharacterContext(fw, x, y, bold));
        }

        /** Renders all characters on the canvas. */
        public void renderAll() {
            System.out.println("Rendering " + placedCharacters.size() + " characters:");
            placedCharacters.forEach(CharacterContext::render);
        }

        public CharacterFactory getFactory() { return factory; }

        public int getTotalPlacements() { return placedCharacters.size(); }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Flyweight Pattern Demo — Text Editor ===\n");

        TextEditor editor = new TextEditor();

        // Place several characters — many will share flyweights
        // Line 1: "Hello" in Arial 12pt Black
        editor.addCharacter('H', "Arial", 12, "Black", 0, 0, false);
        editor.addCharacter('e', "Arial", 12, "Black", 1, 0, false);
        editor.addCharacter('l', "Arial", 12, "Black", 2, 0, false);
        editor.addCharacter('l', "Arial", 12, "Black", 3, 0, false);  // reuses 'l'
        editor.addCharacter('o', "Arial", 12, "Black", 4, 0, false);

        // Line 2: "World" — some characters are NEW flyweights
        editor.addCharacter('W', "Arial", 12, "Black", 0, 1, false);
        editor.addCharacter('o', "Arial", 12, "Black", 1, 1, false);  // reuses 'o'
        editor.addCharacter('r', "Arial", 12, "Black", 2, 1, false);
        editor.addCharacter('l', "Arial", 12, "Black", 3, 1, false);  // reuses 'l'
        editor.addCharacter('d', "Arial", 12, "Black", 4, 1, false);

        // Bold heading with different font — new flyweights
        editor.addCharacter('H', "Arial", 18, "Blue", 0, 3, true);   // new: size 18
        editor.addCharacter('i', "Arial", 18, "Blue", 1, 3, true);

        System.out.println("--- Rendering ---");
        editor.renderAll();

        System.out.println("\n--- Flyweight Pool Statistics ---");
        editor.getFactory().printPool();

        System.out.printf("%nTotal character placements: %d%n", editor.getTotalPlacements());
        System.out.printf("Unique flyweight objects:   %d%n", editor.getFactory().getPoolSize());
        System.out.printf("Flyweights created:         %d%n", editor.getFactory().getCreateCount());
        System.out.printf("Memory saved (approx):      %d fewer objects%n",
                editor.getTotalPlacements() - editor.getFactory().getPoolSize());

        // Demonstrate scale — simulate 1000 more 'e' characters (same flyweight)
        System.out.println("\n--- Simulating 1000 more 'e' characters ---");
        int before = editor.getFactory().getPoolSize();
        for (int i = 0; i < 1000; i++) {
            editor.addCharacter('e', "Arial", 12, "Black", i % 80, 10 + i / 80, false);
        }
        int after = editor.getFactory().getPoolSize();
        System.out.printf("Pool size before: %d, after adding 1000 'e': %d  (no new flyweights!)%n",
                before, after);
        System.out.printf("Total placements now: %d  (shared by %d flyweight objects)%n",
                editor.getTotalPlacements(), editor.getFactory().getPoolSize());
    }
}
