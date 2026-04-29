import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CRUD — Create, Read, Update, Delete
 *
 * Demonstrates the four fundamental data operations through an in-memory
 * UserRepository, simulating what a real database-backed repository would do.
 *
 * C → create(User)   — INSERT
 * R → findById(id)   — SELECT
 * U → update(User)   — UPDATE
 * D → delete(id)     — DELETE
 */
public class CrudExample {

    // ─────────────────────────────────────────────────────────────
    // Model
    // ─────────────────────────────────────────────────────────────

    static class User {
        private final int id;
        private String name;
        private String email;

        public User(int id, String name, String email) {
            this.id    = id;
            this.name  = name;
            this.email = email;
        }

        public int    getId()    { return id; }
        public String getName()  { return name; }
        public String getEmail() { return email; }

        public void setName(String name)   { this.name  = name; }
        public void setEmail(String email) { this.email = email; }

        @Override
        public String toString() {
            return String.format("User{id=%d, name='%s', email='%s'}", id, name, email);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Repository Interface (abstracts the data source)
    // ─────────────────────────────────────────────────────────────

    interface UserRepository {
        User         create(User user);          // CREATE
        Optional<User> findById(int id);         // READ (single)
        List<User>   findAll();                  // READ (all)
        User         update(User user);          // UPDATE
        boolean      delete(int id);             // DELETE
    }

    // ─────────────────────────────────────────────────────────────
    // In-Memory Implementation
    // ─────────────────────────────────────────────────────────────

    static class InMemoryUserRepository implements UserRepository {

        private final Map<Integer, User> store = new HashMap<>();

        // ── CREATE ──────────────────────────────────────────────
        @Override
        public User create(User user) {
            if (store.containsKey(user.getId())) {
                throw new IllegalArgumentException("User with id " + user.getId() + " already exists.");
            }
            store.put(user.getId(), user);
            System.out.println("[CREATE] Inserted: " + user);
            return user;
        }

        // ── READ ─────────────────────────────────────────────────
        @Override
        public Optional<User> findById(int id) {
            Optional<User> result = Optional.ofNullable(store.get(id));
            result.ifPresentOrElse(
                u -> System.out.println("[READ]   Found:    " + u),
                () -> System.out.println("[READ]   Not found for id=" + id)
            );
            return result;
        }

        @Override
        public List<User> findAll() {
            List<User> all = new ArrayList<>(store.values());
            System.out.println("[READ]   All users (" + all.size() + "): " + all);
            return all;
        }

        // ── UPDATE ───────────────────────────────────────────────
        @Override
        public User update(User user) {
            if (!store.containsKey(user.getId())) {
                throw new IllegalArgumentException("Cannot update — user not found: id=" + user.getId());
            }
            store.put(user.getId(), user);
            System.out.println("[UPDATE] Updated:  " + user);
            return user;
        }

        // ── DELETE ───────────────────────────────────────────────
        @Override
        public boolean delete(int id) {
            User removed = store.remove(id);
            if (removed != null) {
                System.out.println("[DELETE] Deleted:  " + removed);
                return true;
            }
            System.out.println("[DELETE] Nothing to delete for id=" + id);
            return false;
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Demo
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        UserRepository repo = new InMemoryUserRepository();

        System.out.println("=== CREATE ===");
        repo.create(new User(1, "Alice",   "alice@example.com"));
        repo.create(new User(2, "Bob",     "bob@example.com"));
        repo.create(new User(3, "Charlie", "charlie@example.com"));

        System.out.println("\n=== READ ===");
        repo.findById(2);
        repo.findById(99); // not found

        System.out.println("\n=== READ ALL ===");
        repo.findAll();

        System.out.println("\n=== UPDATE ===");
        repo.update(new User(1, "Alice Smith", "alice.smith@example.com"));
        repo.findById(1);

        System.out.println("\n=== DELETE ===");
        repo.delete(3);
        repo.delete(99); // nothing to delete

        System.out.println("\n=== FINAL STATE ===");
        repo.findAll();
    }
}

