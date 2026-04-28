import java.util.HashMap;
import java.util.Map;

/**
 * Proxy Pattern
 *
 * Intent: Provide a surrogate or placeholder for another object to control
 *         access to it.
 *
 * This file demonstrates two types of proxy:
 *
 * 1. Virtual Proxy  — Delays expensive object creation until it's needed
 *                     (lazy-loading database records)
 *
 * 2. Protection Proxy — Controls access based on user roles/permissions
 *
 * Roles:
 *   Subject        -> DataService (interface)
 *   RealSubject    -> RealDataService
 *   Proxy 1        -> LazyLoadingProxy (virtual proxy)
 *   Proxy 2        -> ProtectionProxy
 *   Client         -> main()
 */
public class Proxy {

    // =========================================================================
    // EXAMPLE 1: Virtual Proxy (Lazy Loading)
    // =========================================================================

    /** Subject interface — the common contract for real service and proxy. */
    interface ImageLoader {
        void display();
        int getWidth();
        int getHeight();
        String getFilename();
    }

    /**
     * RealSubject — loading this image is expensive (simulated).
     * In reality, this might involve disk I/O or network access.
     */
    static class HighResImage implements ImageLoader {
        private final String filename;
        private final int width;
        private final int height;
        private final byte[] imageData;  // simulates large data in memory

        HighResImage(String filename) {
            this.filename = filename;
            System.out.println("[HighResImage] Loading from disk: " + filename + " ... (expensive!)");
            // Simulate slow loading
            this.width  = 3840;
            this.height = 2160;
            this.imageData = new byte[width * height * 3];  // 24 MB for a 4K image
            System.out.println("[HighResImage] Loaded: " + filename
                    + " (" + (imageData.length / 1_000_000) + " MB)");
        }

        @Override
        public void display() {
            System.out.println("[HighResImage] Displaying: " + filename
                    + " (" + width + "x" + height + ")");
        }

        @Override
        public int getWidth()      { return width; }

        @Override
        public int getHeight()     { return height; }

        @Override
        public String getFilename() { return filename; }
    }

    /**
     * Virtual Proxy for HighResImage.
     *
     * Implements the same ImageLoader interface, so clients can't tell the
     * difference. The real HighResImage is created only when display() is
     * first called.
     */
    static class ImageProxy implements ImageLoader {
        private final String filename;
        private HighResImage realImage = null;  // null until first access

        ImageProxy(String filename) {
            this.filename = filename;
            System.out.println("[ImageProxy] Created placeholder for: " + filename);
        }

        /**
         * Loads the real image only on first call (lazy initialization).
         * Subsequent calls reuse the already-loaded image.
         */
        @Override
        public void display() {
            if (realImage == null) {
                realImage = new HighResImage(filename);  // lazy creation
            }
            realImage.display();
        }

        @Override
        public int getWidth() {
            // Can return metadata without loading the full image
            System.out.println("[ImageProxy] Returning cached width for: " + filename);
            return 3840;
        }

        @Override
        public int getHeight() {
            System.out.println("[ImageProxy] Returning cached height for: " + filename);
            return 2160;
        }

        @Override
        public String getFilename() { return filename; }

        /** Tells whether the real image has been loaded yet. */
        public boolean isLoaded() { return realImage != null; }
    }

    // =========================================================================
    // EXAMPLE 2: Protection Proxy
    // =========================================================================

    /** Subject interface for a document service. */
    interface DocumentService {
        String readDocument(String docId);
        void writeDocument(String docId, String content);
        void deleteDocument(String docId);
    }

    /** User roles for access control. */
    enum Role { VIEWER, EDITOR, ADMIN }

    /** A user with a name and role. */
    static class User {
        final String name;
        final Role role;

        User(String name, Role role) { this.name = name; this.role = role; }

        @Override
        public String toString() { return name + "[" + role + "]"; }
    }

    /**
     * RealSubject — the actual document store.
     * Has no access control — trusts all callers.
     */
    static class RealDocumentService implements DocumentService {
        private final Map<String, String> documents = new HashMap<>();

        RealDocumentService() {
            // Some initial documents
            documents.put("doc1", "Annual Report 2024");
            documents.put("doc2", "Confidential Strategy Plan");
            documents.put("doc3", "Public FAQ");
        }

        @Override
        public String readDocument(String docId) {
            String content = documents.getOrDefault(docId, "[NOT FOUND]");
            System.out.println("[DocumentService] Read '" + docId + "': " + content);
            return content;
        }

        @Override
        public void writeDocument(String docId, String content) {
            documents.put(docId, content);
            System.out.println("[DocumentService] Written '" + docId + "': " + content);
        }

        @Override
        public void deleteDocument(String docId) {
            documents.remove(docId);
            System.out.println("[DocumentService] Deleted '" + docId + "'");
        }
    }

    /**
     * Protection Proxy — wraps the real service and enforces access control
     * based on the user's role before delegating to the real service.
     *
     * Viewers  → can only read
     * Editors  → can read and write
     * Admins   → can read, write, and delete
     */
    static class ProtectionProxy implements DocumentService {
        private final DocumentService realService;
        private final User currentUser;

        ProtectionProxy(DocumentService realService, User currentUser) {
            this.realService = realService;
            this.currentUser = currentUser;
        }

        @Override
        public String readDocument(String docId) {
            // All roles can read
            System.out.println("[Proxy] " + currentUser + " reading '" + docId + "'...");
            return realService.readDocument(docId);
        }

        @Override
        public void writeDocument(String docId, String content) {
            if (currentUser.role == Role.VIEWER) {
                System.out.println("[Proxy] ACCESS DENIED: " + currentUser
                        + " does not have write permission.");
                return;
            }
            System.out.println("[Proxy] " + currentUser + " writing '" + docId + "'...");
            realService.writeDocument(docId, content);
        }

        @Override
        public void deleteDocument(String docId) {
            if (currentUser.role != Role.ADMIN) {
                System.out.println("[Proxy] ACCESS DENIED: " + currentUser
                        + " does not have delete permission (admin only).");
                return;
            }
            System.out.println("[Proxy] " + currentUser + " deleting '" + docId + "'...");
            realService.deleteDocument(docId);
        }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Proxy Pattern Demo ===\n");

        // --- 1. Virtual Proxy ---
        System.out.println("--- 1. Virtual Proxy (Lazy Loading) ---");
        ImageLoader[] gallery = {
            new ImageProxy("photo1.jpg"),
            new ImageProxy("photo2.jpg"),
            new ImageProxy("photo3.jpg")
        };

        System.out.println("\nCreated gallery — images not yet loaded.");
        System.out.println("Getting dimensions (no load needed):");
        System.out.println("  photo1 size: " + gallery[0].getWidth() + "x" + gallery[0].getHeight());

        System.out.println("\nUser scrolls to photo1 — display() called:");
        gallery[0].display();  // loads image #1

        System.out.println("\nUser displays photo1 again (already loaded):");
        gallery[0].display();  // reuses existing image

        System.out.println("\nphoto2 and photo3 never displayed — never loaded.");
        System.out.println("  proxy.isLoaded() for photo2: " + ((ImageProxy) gallery[1]).isLoaded());

        // --- 2. Protection Proxy ---
        System.out.println("\n--- 2. Protection Proxy ---");
        DocumentService realService = new RealDocumentService();

        User viewer = new User("Alice", Role.VIEWER);
        User editor = new User("Bob",   Role.EDITOR);
        User admin  = new User("Carol", Role.ADMIN);

        System.out.println("\n[Viewer - Alice]:");
        DocumentService aliceService = new ProtectionProxy(realService, viewer);
        aliceService.readDocument("doc1");
        aliceService.writeDocument("doc4", "Attempt to write");   // DENIED
        aliceService.deleteDocument("doc3");                       // DENIED

        System.out.println("\n[Editor - Bob]:");
        DocumentService bobService = new ProtectionProxy(realService, editor);
        bobService.readDocument("doc2");
        bobService.writeDocument("doc2", "Updated Strategy Plan");  // ALLOWED
        bobService.deleteDocument("doc1");                           // DENIED

        System.out.println("\n[Admin - Carol]:");
        DocumentService carolService = new ProtectionProxy(realService, admin);
        carolService.readDocument("doc2");
        carolService.writeDocument("doc5", "New Document");
        carolService.deleteDocument("doc3");    // ALLOWED
    }
}
