import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Memento Pattern
 *
 * Intent: Without violating encapsulation, capture and externalize an
 *         object's internal state so that the object can be restored
 *         to this state later.
 *
 * Example: A text editor that supports unlimited undo/redo.
 *          The editor (Originator) creates snapshots (Mementos).
 *          The history manager (Caretaker) stores them.
 *
 * Roles:
 *   Originator -> TextEditor
 *   Memento    -> TextEditor.EditorMemento (static inner class — opaque to outsiders)
 *   Caretaker  -> EditorHistory
 */
public class Memento {

    // =========================================================================
    // Originator: TextEditor
    // =========================================================================

    /**
     * The Originator. Manages the editor state and knows how to create and
     * restore from snapshots. The inner Memento class is the only class that
     * can access the private state fields.
     */
    static class TextEditor {
        private String text;
        private int cursorPosition;
        private String selectedText;
        private String fontStyle;   // "Normal", "Bold", "Italic"

        TextEditor() {
            this.text           = "";
            this.cursorPosition = 0;
            this.selectedText   = "";
            this.fontStyle      = "Normal";
        }

        // --- Editing operations ---

        public void type(String str) {
            text = text.substring(0, cursorPosition) + str + text.substring(cursorPosition);
            cursorPosition += str.length();
        }

        public void delete(int count) {
            int start = Math.max(0, cursorPosition - count);
            text = text.substring(0, start) + text.substring(cursorPosition);
            cursorPosition = start;
        }

        public void moveCursor(int position) {
            cursorPosition = Math.max(0, Math.min(position, text.length()));
        }

        public void selectText(String selection) {
            this.selectedText = selection;
        }

        public void setFontStyle(String style) {
            this.fontStyle = style;
        }

        // --- Memento operations ---

        /**
         * Creates a snapshot (Memento) of the current state.
         * The snapshot is an opaque EditorMemento object — only the TextEditor
         * knows how to create/use it.
         */
        public EditorMemento save() {
            return new EditorMemento(text, cursorPosition, selectedText, fontStyle);
        }

        /**
         * Restores the editor's state from a snapshot.
         *
         * @param memento a previously saved snapshot
         */
        public void restore(EditorMemento memento) {
            this.text           = memento.text;
            this.cursorPosition = memento.cursorPosition;
            this.selectedText   = memento.selectedText;
            this.fontStyle      = memento.fontStyle;
        }

        public String getText()           { return text; }
        public int    getCursorPosition() { return cursorPosition; }
        public String getSelectedText()   { return selectedText; }
        public String getFontStyle()      { return fontStyle; }

        @Override
        public String toString() {
            String display = text.substring(0, cursorPosition)
                    + "|"
                    + text.substring(cursorPosition);
            return "TextEditor{text=\"" + display + "\", font=" + fontStyle + "}";
        }

        // ===========================================================
        // Memento (static inner class)
        // ===========================================================

        /**
         * The Memento. Stores a snapshot of the TextEditor's state.
         *
         * It is a static inner class of TextEditor, which means:
         * - TextEditor can access its private constructor and fields
         * - External code (Caretaker) cannot access the state directly
         *   (only through the outer class)
         *
         * The Caretaker stores EditorMemento objects but cannot read
         * or modify the state inside them.
         */
        static final class EditorMemento {
            // All fields are private — only TextEditor can access them
            // via the static inner class relationship in Java
            private final String text;
            private final int    cursorPosition;
            private final String selectedText;
            private final String fontStyle;
            private final long   timestamp;

            private EditorMemento(String text, int cursorPosition,
                                  String selectedText, String fontStyle) {
                this.text           = text;
                this.cursorPosition = cursorPosition;
                this.selectedText   = selectedText;
                this.fontStyle      = fontStyle;
                this.timestamp      = System.currentTimeMillis();
            }

            /** Returns a brief label for display (caretaker can see this). */
            public String getLabel() {
                String preview = text.length() > 20 ? text.substring(0, 20) + "..." : text;
                return "Snapshot[\"" + preview + "\", cursor=" + cursorPosition
                        + ", font=" + fontStyle + "]";
            }

            public long getTimestamp() { return timestamp; }
        }
    }

    // =========================================================================
    // Caretaker: EditorHistory
    // =========================================================================

    /**
     * The Caretaker. Manages the undo/redo stacks of Memento objects.
     *
     * The Caretaker holds EditorMemento objects but cannot inspect or
     * modify the editor state stored inside them — it only knows their
     * opaque labels.
     */
    static class EditorHistory {
        private final Deque<TextEditor.EditorMemento> undoStack = new ArrayDeque<>();
        private final Deque<TextEditor.EditorMemento> redoStack = new ArrayDeque<>();
        private final TextEditor editor;

        EditorHistory(TextEditor editor) {
            this.editor = editor;
        }

        /**
         * Saves the current editor state onto the undo stack.
         * Should be called before every edit operation.
         */
        public void save() {
            TextEditor.EditorMemento snapshot = editor.save();
            undoStack.push(snapshot);
            redoStack.clear();   // new edit invalidates redo history
            System.out.println("  💾 Saved: " + snapshot.getLabel());
        }

        /** Undoes the last operation by restoring the previous snapshot. */
        public boolean undo() {
            if (undoStack.isEmpty()) {
                System.out.println("  ↩ Nothing to undo.");
                return false;
            }
            // Save current state to redo stack before restoring
            redoStack.push(editor.save());

            TextEditor.EditorMemento snapshot = undoStack.pop();
            editor.restore(snapshot);
            System.out.println("  ↩ Undid to: " + snapshot.getLabel());
            return true;
        }

        /** Redoes the most recently undone operation. */
        public boolean redo() {
            if (redoStack.isEmpty()) {
                System.out.println("  ↪ Nothing to redo.");
                return false;
            }
            undoStack.push(editor.save());

            TextEditor.EditorMemento snapshot = redoStack.pop();
            editor.restore(snapshot);
            System.out.println("  ↪ Redid to: " + snapshot.getLabel());
            return true;
        }

        public int undoLevels() { return undoStack.size(); }
        public int redoLevels() { return redoStack.size(); }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    static void printState(TextEditor editor, EditorHistory history) {
        System.out.println("    State: " + editor
                + " [undo:" + history.undoLevels()
                + " redo:" + history.redoLevels() + "]");
    }

    public static void main(String[] args) {
        System.out.println("=== Memento Pattern Demo — Text Editor ===\n");

        TextEditor editor   = new TextEditor();
        EditorHistory history = new EditorHistory(editor);

        System.out.println("--- Typing with undo/redo ---\n");

        history.save();                       // save before edit
        editor.type("Hello");
        printState(editor, history);

        history.save();
        editor.type(", World");
        printState(editor, history);

        history.save();
        editor.setFontStyle("Bold");
        editor.type("!");
        printState(editor, history);

        System.out.println("\n--- Undo ---");
        history.undo();
        printState(editor, history);

        history.undo();
        printState(editor, history);

        System.out.println("\n--- Redo ---");
        history.redo();
        printState(editor, history);

        System.out.println("\n--- New edit clears redo stack ---");
        history.save();
        editor.type(" Java");
        printState(editor, history);

        System.out.println("\n--- Can't redo after new edit ---");
        history.redo();  // nothing to redo

        System.out.println("\n--- Undo everything ---");
        while (history.undo()) {
            printState(editor, history);
        }
    }
}
