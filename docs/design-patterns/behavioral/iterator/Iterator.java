import java.util.NoSuchElementException;
import java.util.Arrays;

/**
 * Iterator Pattern
 *
 * Intent: Provide a way to access elements of an aggregate object
 *         sequentially without exposing its underlying representation.
 *
 * Example: A custom NumberCollection that supports:
 *   - Forward iteration (standard)
 *   - Reverse iteration
 *   - Filtered iteration (even numbers only)
 *
 * Also shows integration with Java's Iterable interface for use with
 * enhanced for-loops.
 *
 * Roles:
 *   Iterator          -> NumberIterator (interface)
 *   ConcreteIterator  -> ForwardIterator, ReverseIterator, FilteredIterator
 *   Aggregate         -> NumberCollection
 *   Client            -> main()
 */
public class Iterator {

    // =========================================================================
    // Custom Iterator Interface
    // =========================================================================

    /**
     * Our custom Iterator interface. Declares the navigation protocol.
     * Also extends java.util.Iterator<T> for compatibility with Java's
     * enhanced for-loop and standard library.
     */
    interface NumberIterator extends java.util.Iterator<Integer> {
        /** Moves to the first element and returns it, or null if empty. */
        void reset();

        /** Returns true if there are more elements in the traversal direction. */
        boolean hasNext();

        /** Returns the next element and advances the cursor. */
        Integer next();

        /** Returns the current element without advancing (peek). */
        Integer current();
    }

    // =========================================================================
    // Aggregate: NumberCollection
    // =========================================================================

    /**
     * A simple integer collection backed by an array.
     * Provides factory methods to create different iterators.
     */
    static class NumberCollection implements Iterable<Integer> {
        private int[] data;
        private int size;

        NumberCollection(int capacity) {
            this.data = new int[capacity];
            this.size = 0;
        }

        /** Adds a number to the collection (grows if needed). */
        public void add(int n) {
            if (size == data.length)
                data = Arrays.copyOf(data, data.length * 2);
            data[size++] = n;
        }

        public int get(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException(index);
            return data[index];
        }

        public int size() { return size; }

        /** Factory method — creates a forward iterator. */
        public NumberIterator forwardIterator() {
            return new ForwardIterator(this);
        }

        /** Factory method — creates a reverse iterator. */
        public NumberIterator reverseIterator() {
            return new ReverseIterator(this);
        }

        /** Factory method — creates a filtered iterator (only even numbers). */
        public NumberIterator evenIterator() {
            return new FilteredIterator(this, n -> n % 2 == 0);
        }

        /** Factory method — creates a filtered iterator with a custom predicate. */
        public NumberIterator filteredIterator(java.util.function.IntPredicate predicate) {
            return new FilteredIterator(this, predicate);
        }

        /** Default iterator for enhanced for-loop compatibility. */
        @Override
        public java.util.Iterator<Integer> iterator() {
            return forwardIterator();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < size; i++) {
                if (i > 0) sb.append(", ");
                sb.append(data[i]);
            }
            return sb.append("]").toString();
        }
    }

    // =========================================================================
    // Concrete Iterators
    // =========================================================================

    /** Iterates forward from index 0 to size-1. */
    static class ForwardIterator implements NumberIterator {
        private final NumberCollection collection;
        private int cursor;
        private Integer current;

        ForwardIterator(NumberCollection collection) {
            this.collection = collection;
            this.cursor = 0;
        }

        @Override
        public void reset() {
            cursor = 0;
            current = null;
        }

        @Override
        public boolean hasNext() {
            return cursor < collection.size();
        }

        @Override
        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            current = collection.get(cursor++);
            return current;
        }

        @Override
        public Integer current() {
            if (current == null) throw new IllegalStateException("Call next() first.");
            return current;
        }
    }

    /** Iterates in reverse from size-1 to 0. */
    static class ReverseIterator implements NumberIterator {
        private final NumberCollection collection;
        private int cursor;
        private Integer current;

        ReverseIterator(NumberCollection collection) {
            this.collection = collection;
            this.cursor = collection.size() - 1;
        }

        @Override
        public void reset() {
            cursor = collection.size() - 1;
            current = null;
        }

        @Override
        public boolean hasNext() {
            return cursor >= 0;
        }

        @Override
        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            current = collection.get(cursor--);
            return current;
        }

        @Override
        public Integer current() {
            if (current == null) throw new IllegalStateException("Call next() first.");
            return current;
        }
    }

    /**
     * Iterates forward but skips elements that don't match the predicate.
     * Examples: only even numbers, only primes, only numbers > 10, etc.
     */
    static class FilteredIterator implements NumberIterator {
        private final NumberCollection collection;
        private final java.util.function.IntPredicate predicate;
        private int cursor;
        private Integer current;
        private Integer nextCached = null;

        FilteredIterator(NumberCollection collection, java.util.function.IntPredicate predicate) {
            this.collection = collection;
            this.predicate  = predicate;
            this.cursor     = 0;
            advance();
        }

        /** Pre-loads the next matching element into nextCached. */
        private void advance() {
            nextCached = null;
            while (cursor < collection.size()) {
                int val = collection.get(cursor++);
                if (predicate.test(val)) {
                    nextCached = val;
                    break;
                }
            }
        }

        @Override
        public void reset() {
            cursor = 0;
            current = null;
            nextCached = null;
            advance();
        }

        @Override
        public boolean hasNext() {
            return nextCached != null;
        }

        @Override
        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            current = nextCached;
            advance();
            return current;
        }

        @Override
        public Integer current() {
            if (current == null) throw new IllegalStateException("Call next() first.");
            return current;
        }
    }

    // =========================================================================
    // Helper: print all elements from an iterator
    // =========================================================================

    static void printAll(String label, NumberIterator it) {
        System.out.print(label + ": [");
        boolean first = true;
        while (it.hasNext()) {
            if (!first) System.out.print(", ");
            System.out.print(it.next());
            first = false;
        }
        System.out.println("]");
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Iterator Pattern Demo ===\n");

        NumberCollection numbers = new NumberCollection(10);
        for (int n : new int[]{5, 12, 3, 8, 17, 24, 1, 10, 7, 16}) {
            numbers.add(n);
        }
        System.out.println("Collection: " + numbers);

        System.out.println();

        // Forward iteration
        printAll("Forward  ", numbers.forwardIterator());

        // Reverse iteration
        printAll("Reverse  ", numbers.reverseIterator());

        // Even numbers only
        printAll("Evens    ", numbers.evenIterator());

        // Custom filter: numbers > 10
        printAll("Over 10  ", numbers.filteredIterator(n -> n > 10));

        // Custom filter: prime numbers
        printAll("Primes   ", numbers.filteredIterator(Iterator::isPrime));

        // --- Enhanced for-loop (uses default forwardIterator via Iterable) ---
        System.out.println("\nEnhanced for-loop:");
        System.out.print("  ");
        for (int n : numbers) {
            System.out.print(n + " ");
        }
        System.out.println();

        // --- Multiple simultaneous iterators ---
        System.out.println("\nTwo simultaneous forward iterators (independent positions):");
        NumberIterator it1 = numbers.forwardIterator();
        NumberIterator it2 = numbers.forwardIterator();
        System.out.print("  it1 first 3: ");
        for (int i = 0; i < 3 && it1.hasNext(); i++) System.out.print(it1.next() + " ");
        System.out.print("\n  it2 first 3: ");
        for (int i = 0; i < 3 && it2.hasNext(); i++) System.out.print(it2.next() + " ");
        System.out.println("\n  (they progress independently)");

        // --- Reset ---
        System.out.println("\nReset forward iterator:");
        NumberIterator it3 = numbers.forwardIterator();
        System.out.print("  First 3: ");
        for (int i = 0; i < 3 && it3.hasNext(); i++) System.out.print(it3.next() + " ");
        it3.reset();
        System.out.print("\n  After reset, first 3: ");
        for (int i = 0; i < 3 && it3.hasNext(); i++) System.out.print(it3.next() + " ");
        System.out.println();
    }

    /** Simple primality check for the demo. */
    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2)
            if (n % i == 0) return false;
        return true;
    }
}
