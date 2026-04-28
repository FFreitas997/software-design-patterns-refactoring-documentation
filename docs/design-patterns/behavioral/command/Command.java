import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command Pattern
 *
 * Intent: Encapsulate a request as an object, thereby letting you
 *         parameterize clients with different requests, queue or log
 *         requests, and support undoable operations.
 *
 * Example: A text editor with typed commands, clipboard operations,
 *          and full undo/redo support.
 *
 * Roles:
 *   Command          -> EditorCommand (interface)
 *   ConcreteCommand  -> TypeCommand, DeleteCommand, CopyCommand, PasteCommand
 *   Receiver         -> TextEditor
 *   Invoker          -> CommandHistory (manages undo/redo stacks)
 *   Client           -> main()
 */
public class Command {

    // =========================================================================
    // Command Interface
    // =========================================================================

    /**
     * The Command interface declares execute() and undo() operations.
     * Every concrete command must implement both to support undo.
     */
    interface EditorCommand {
        /** Executes the command, performing the action. */
        void execute();

        /** Undoes the effect of execute(). */
        void undo();

        /** Returns a human-readable description for logging/display. */
        String describe();
    }

    // =========================================================================
    // Receiver: TextEditor
    // =========================================================================

    /**
     * The Receiver knows how to perform the actual editing operations.
     * Commands invoke methods on this object.
     */
    static class TextEditor {
        private StringBuilder text = new StringBuilder();
        private String clipboard = "";

        public void insert(int position, String str) {
            if (position < 0 || position > text.length())
                position = text.length();
            text.insert(position, str);
        }

        public String delete(int position, int length) {
            if (position < 0 || position >= text.length()) return "";
            int end = Math.min(position + length, text.length());
            String deleted = text.substring(position, end);
            text.delete(position, end);
            return deleted;
        }

        public String getText() { return text.toString(); }

        public String getClipboard() { return clipboard; }

        public void setClipboard(String s) { this.clipboard = s; }

        public int length() { return text.length(); }

        @Override
        public String toString() { return "\"" + text + "\""; }
    }

    // =========================================================================
    // Concrete Commands
    // =========================================================================

    /**
     * Types (inserts) a string at a given position.
     * Undo: deletes the inserted text.
     */
    static class TypeCommand implements EditorCommand {
        private final TextEditor editor;
        private final int position;
        private final String text;

        TypeCommand(TextEditor editor, int position, String text) {
            this.editor   = editor;
            this.position = position;
            this.text     = text;
        }

        @Override
        public void execute() {
            editor.insert(position, text);
        }

        @Override
        public void undo() {
            editor.delete(position, text.length());
        }

        @Override
        public String describe() {
            return "Type(\"" + text + "\" at " + position + ")";
        }
    }

    /**
     * Deletes a range of characters.
     * Undo: re-inserts the deleted text.
     */
    static class DeleteCommand implements EditorCommand {
        private final TextEditor editor;
        private final int position;
        private final int length;
        private String deletedText = "";  // saved for undo

        DeleteCommand(TextEditor editor, int position, int length) {
            this.editor   = editor;
            this.position = position;
            this.length   = length;
        }

        @Override
        public void execute() {
            deletedText = editor.delete(position, length);
        }

        @Override
        public void undo() {
            editor.insert(position, deletedText);
        }

        @Override
        public String describe() {
            return "Delete(" + length + " chars at " + position + ")";
        }
    }

    /**
     * Copies selected text to the clipboard.
     * Undo: restores the old clipboard value.
     */
    static class CopyCommand implements EditorCommand {
        private final TextEditor editor;
        private final int start, length;
        private String previousClipboard = "";

        CopyCommand(TextEditor editor, int start, int length) {
            this.editor = editor;
            this.start  = start;
            this.length = length;
        }

        @Override
        public void execute() {
            previousClipboard = editor.getClipboard();
            String text = editor.getText();
            int end = Math.min(start + length, text.length());
            editor.setClipboard(text.substring(Math.min(start, text.length()), end));
        }

        @Override
        public void undo() {
            editor.setClipboard(previousClipboard);
        }

        @Override
        public String describe() {
            return "Copy(" + length + " chars at " + start + ")";
        }
    }

    /**
     * Pastes clipboard content at a given position.
     * Undo: deletes the pasted text.
     */
    static class PasteCommand implements EditorCommand {
        private final TextEditor editor;
        private final int position;
        private String pastedText = "";

        PasteCommand(TextEditor editor, int position) {
            this.editor   = editor;
            this.position = position;
        }

        @Override
        public void execute() {
            pastedText = editor.getClipboard();
            editor.insert(position, pastedText);
        }

        @Override
        public void undo() {
            editor.delete(position, pastedText.length());
        }

        @Override
        public String describe() {
            return "Paste(\"" + pastedText + "\" at " + position + ")";
        }
    }

    // =========================================================================
    // Invoker: CommandHistory (supports undo/redo)
    // =========================================================================

    /**
     * The Invoker. Stores executed commands in an undo stack.
     * When undo is called, it pops from the undo stack and pushes to the redo stack.
     * Executing a new command clears the redo stack.
     */
    static class CommandHistory {
        private final Deque<EditorCommand> undoStack = new ArrayDeque<>();
        private final Deque<EditorCommand> redoStack = new ArrayDeque<>();

        /**
         * Executes a command and records it for potential undo.
         * Clears the redo stack (new action invalidates old redo history).
         */
        public void execute(EditorCommand command) {
            command.execute();
            undoStack.push(command);
            redoStack.clear();
            System.out.println("  ▶ Executed: " + command.describe());
        }

        /**
         * Undoes the most recently executed command.
         * The command is moved to the redo stack.
         */
        public boolean undo() {
            if (undoStack.isEmpty()) {
                System.out.println("  ↩ Nothing to undo.");
                return false;
            }
            EditorCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            System.out.println("  ↩ Undid: " + command.describe());
            return true;
        }

        /**
         * Re-executes the most recently undone command.
         */
        public boolean redo() {
            if (redoStack.isEmpty()) {
                System.out.println("  ↪ Nothing to redo.");
                return false;
            }
            EditorCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            System.out.println("  ↪ Redid: " + command.describe());
            return true;
        }

        public int undoCount() { return undoStack.size(); }
        public int redoCount() { return redoStack.size(); }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    static void printState(TextEditor editor, CommandHistory history) {
        System.out.println("  Editor: " + editor
                + "  [undo:" + history.undoCount()
                + " redo:" + history.redoCount() + "]");
    }

    public static void main(String[] args) {
        System.out.println("=== Command Pattern Demo — Text Editor ===\n");

        TextEditor editor  = new TextEditor();
        CommandHistory history = new CommandHistory();

        System.out.println("--- Typing ---");
        history.execute(new TypeCommand(editor, 0, "Hello"));
        printState(editor, history);

        history.execute(new TypeCommand(editor, 5, ", World"));
        printState(editor, history);

        history.execute(new TypeCommand(editor, 12, "!"));
        printState(editor, history);

        System.out.println("\n--- Undo/Redo ---");
        history.undo();   // undo '!'
        printState(editor, history);

        history.undo();   // undo ", World"
        printState(editor, history);

        history.redo();   // redo ", World"
        printState(editor, history);

        System.out.println("\n--- Copy/Paste ---");
        history.execute(new CopyCommand(editor, 0, 5));   // copy "Hello"
        System.out.println("  Clipboard: \"" + editor.getClipboard() + "\"");

        history.execute(new PasteCommand(editor, editor.length()));  // paste at end
        printState(editor, history);

        System.out.println("\n--- Delete ---");
        history.execute(new DeleteCommand(editor, 5, 7));  // delete ", World"
        printState(editor, history);

        history.undo();   // undo delete
        printState(editor, history);

        System.out.println("\n--- Undo everything ---");
        while (history.undo()) {
            printState(editor, history);
        }
    }
}
