import java.util.ArrayList;
import java.util.List;

/**
 * Composite Pattern
 *
 * Intent: Compose objects into tree structures to represent part-whole
 *         hierarchies. Lets clients treat individual objects and compositions
 *         uniformly.
 *
 * Example: A file system where both files (leaves) and directories (composites)
 *          are treated as FileSystemEntry objects. Operations like getSize(),
 *          print(), and search() work the same way on both.
 *
 * Roles:
 *   Component -> FileSystemEntry (interface)
 *   Leaf      -> File
 *   Composite -> Directory
 *   Client    -> main() method
 */
public class Composite {

    // =========================================================================
    // Component Interface
    // =========================================================================

    /**
     * The Component interface declares operations common to both simple
     * (Leaf) and complex (Composite) elements of the tree.
     */
    interface FileSystemEntry {
        /** Returns the name of this entry. */
        String getName();

        /** Returns the total size in bytes (for Directory: sum of all children). */
        long getSize();

        /** Prints the tree structure with indentation. */
        void print(String indent);

        /** Returns true if this entry contains (or is) the given name. */
        boolean contains(String name);

        /** Returns the depth of this entry in the tree (leaves = 0). */
        int depth();
    }

    // =========================================================================
    // Leaf
    // =========================================================================

    /**
     * Leaf node — represents a single file with a fixed size.
     * Leaves have no children.
     */
    static class File implements FileSystemEntry {
        private final String name;
        private final long size;        // size in bytes
        private final String mimeType;

        File(String name, long size, String mimeType) {
            this.name     = name;
            this.size     = size;
            this.mimeType = mimeType;
        }

        @Override
        public String getName() { return name; }

        @Override
        public long getSize() { return size; }

        @Override
        public void print(String indent) {
            System.out.printf("%s📄 %s  [%s, %s]%n",
                    indent, name, formatSize(size), mimeType);
        }

        @Override
        public boolean contains(String name) {
            return this.name.equals(name);
        }

        @Override
        public int depth() { return 0; }

        private static String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
            return (bytes / (1024 * 1024)) + " MB";
        }
    }

    // =========================================================================
    // Composite
    // =========================================================================

    /**
     * Composite node — represents a directory that can contain files or
     * other directories. Implements FileSystemEntry, so it's treated exactly
     * like a File from the client's perspective.
     */
    static class Directory implements FileSystemEntry {
        private final String name;
        private final List<FileSystemEntry> children = new ArrayList<>();

        Directory(String name) { this.name = name; }

        // --- Child management ---

        /** Adds an entry to this directory. */
        public Directory add(FileSystemEntry entry) {
            children.add(entry);
            return this;  // fluent API
        }

        /** Removes an entry from this directory. */
        public boolean remove(FileSystemEntry entry) {
            return children.remove(entry);
        }

        /** Returns the child at the given index. */
        public FileSystemEntry getChild(int index) {
            return children.get(index);
        }

        public List<FileSystemEntry> getChildren() {
            return List.copyOf(children);
        }

        // --- Component Interface ---

        @Override
        public String getName() { return name; }

        /**
         * Recursively sums the sizes of all children.
         * Clients don't need to know this is a directory vs. a file —
         * the call is the same either way.
         */
        @Override
        public long getSize() {
            return children.stream().mapToLong(FileSystemEntry::getSize).sum();
        }

        /**
         * Prints this directory and recursively all its contents.
         */
        @Override
        public void print(String indent) {
            long totalSize = getSize();
            System.out.printf("%s📁 %s/  [%d items, total: %s]%n",
                    indent, name, children.size(), formatSize(totalSize));
            for (FileSystemEntry child : children) {
                child.print(indent + "   ");   // increase indentation for children
            }
        }

        @Override
        public boolean contains(String name) {
            if (this.name.equals(name)) return true;
            return children.stream().anyMatch(c -> c.contains(name));
        }

        /** Depth is the maximum depth among all children + 1. */
        @Override
        public int depth() {
            return children.stream().mapToInt(FileSystemEntry::depth).max().orElse(0) + 1;
        }

        private static String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
            return (bytes / (1024 * 1024)) + " MB";
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Composite Pattern Demo — File System ===\n");

        // Build the file system tree
        Directory root = new Directory("root");

        // /root/home
        Directory home = new Directory("home");
        Directory alice = new Directory("alice");
        alice.add(new File("resume.pdf",    204800, "application/pdf"))
             .add(new File("photo.jpg",    1048576, "image/jpeg"))
             .add(new File("notes.txt",       1024, "text/plain"));

        Directory bob = new Directory("bob");
        bob.add(new File("project.zip",  5242880, "application/zip"))
           .add(new File("README.md",       2048, "text/markdown"));

        home.add(alice).add(bob);

        // /root/etc
        Directory etc = new Directory("etc");
        etc.add(new File("hosts",         512, "text/plain"))
           .add(new File("passwd",        256, "text/plain"))
           .add(new File("nginx.conf",   8192, "text/plain"));

        // /root/var/log
        Directory var = new Directory("var");
        Directory log = new Directory("log");
        log.add(new File("syslog",     2097152, "text/plain"))
           .add(new File("auth.log",    524288, "text/plain"));
        var.add(log);

        root.add(home).add(etc).add(var);

        // --- Print the entire tree ---
        System.out.println("Full file system tree:");
        root.print("");

        // --- Uniform operations ---
        System.out.println("\n--- Uniform operations (same call for file and directory) ---");

        FileSystemEntry[] entries = {
            root,
            home,
            alice,
            alice.getChild(0)   // resume.pdf (a Leaf)
        };

        for (FileSystemEntry entry : entries) {
            System.out.printf("%-20s size=%-12s depth=%d  contains('notes.txt')=%b%n",
                    entry.getName() + "/".repeat(entry.depth() > 0 ? 1 : 0),
                    formatSize(entry.getSize()),
                    entry.depth(),
                    entry.contains("notes.txt"));
        }
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}
