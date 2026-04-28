import java.util.ArrayList;
import java.util.List;

/**
 * DirtyCodeExample.java
 *
 * This class deliberately demonstrates multiple dirty code anti-patterns.
 * Each problem is annotated with a comment explaining WHY it is bad.
 *
 * DO NOT use this style in production code.
 * See CleanCodeExample.java for the refactored version.
 */
public class DirtyCodeExample {

    // BAD: Magic numbers with no explanation. What is 0.1? What is 500? What is 3?
    // BAD: Public mutable fields — no encapsulation whatsoever.
    public double x;
    public int y;
    public String s;
    public List d = new ArrayList();

    // BAD: Constructor does too much — mixing initialization with business logic.
    // BAD: Parameter names 'a', 'b', 'c' are completely meaningless.
    public DirtyCodeExample(double a, int b, String c) {
        this.x = a;
        this.y = b;
        this.s = c;
        // BAD: Side-effect in constructor — loads data silently.
        for (int i = 0; i < 5; i++) {
            d.add("item" + i);
        }
    }

    /**
     * BAD METHOD — exhibits nearly every dirty code smell simultaneously:
     *
     *   1. Long Method: does order validation, discount calculation,
     *      tax computation, inventory check, and receipt printing — all in one method.
     *   2. Bad variable names: t, disc, r, tmp, i, j, flag.
     *   3. Magic numbers: 0.1, 0.2, 0.15, 500, 1000, 100.
     *   4. Deep nesting: 5 levels deep at worst.
     *   5. Duplicated code: the discount printing block appears twice.
     *   6. No separation of concerns: UI (printing), business logic, and
     *      data access are all mixed together.
     */
    public double calc(String t, int y, boolean flag, boolean flag2) {
        double r = 0;
        double disc = 0;

        // BAD: Magic number 0 used as sentinel — unclear meaning.
        if (x > 0) {
            if (t != null) {
                if (t.equals("PREMIUM")) {
                    if (y > 500) {
                        // BAD: Magic number 0.2 — what discount is this? Why 500?
                        disc = 0.2;
                        // BAD: Duplicated output block (see below).
                        System.out.println("Discount applied: " + disc);
                        System.out.println("Customer: " + s);
                    } else {
                        disc = 0.1;
                        // BAD: Exact same print logic duplicated.
                        System.out.println("Discount applied: " + disc);
                        System.out.println("Customer: " + s);
                    }
                } else if (t.equals("STANDARD")) {
                    if (y > 1000) {
                        disc = 0.15;
                    }
                    // BAD: Silently does nothing for standard customers below 1000 — no comment.
                }
                // BAD: Falls through silently for unknown customer types.
            }

            // BAD: Temp variable 'r' keeps getting reassigned — hard to track state.
            r = x - (x * disc);

            // BAD: Magic number 100 and 0.15 with no named constant.
            if (flag) {
                r = r + (r * 0.15);
            }

            // BAD: 'flag2' is a boolean parameter — what does it mean? True or false what?
            if (flag2) {
                // BAD: Deep nesting to do a simple inventory loop check.
                for (int i = 0; i < d.size(); i++) {
                    for (int j = 0; j < 3; j++) {
                        // BAD: Magic number 3 — what are we doing 3 times per item?
                        String tmp = (String) d.get(i);
                        // BAD: Casting without checking type — will throw ClassCastException on non-String.
                        if (tmp != null) {
                            if (tmp.startsWith("item")) {
                                // BAD: Printing inside business logic — mixing concerns.
                                System.out.println("Processing inventory: " + tmp + " pass " + j);
                            }
                        }
                    }
                }
            }

            // BAD: Inline tax logic duplicated from the block above — copy-paste programming.
            double tmp2 = r * 0.15;
            System.out.println("Tax amount: " + tmp2);
            r = r + tmp2;

        } else {
            // BAD: Silent failure — returns 0 with no indication of why.
            System.out.println("ERR");
        }

        // BAD: No documentation on what this return value represents.
        return r;
    }

    /**
     * BAD: Another method that does something completely unrelated to the class
     * (string utilities) — the class has no coherent purpose.
     *
     * BAD: The method name 'proc' tells you nothing.
     * BAD: Uses indexed loops when a for-each would be clearer.
     */
    public String proc(String[] arr) {
        String res = "";
        // BAD: String concatenation in a loop — O(n²) performance.
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                if (!arr[i].equals("")) {
                    // BAD: Magic string " | " separator hardcoded.
                    if (i == arr.length - 1) {
                        res = res + arr[i];
                    } else {
                        res = res + arr[i] + " | ";
                    }
                }
            }
        }
        return res;
    }

    /**
     * BAD: Method called 'checkAndDoStuff' — vague name hiding multiple responsibilities.
     * BAD: Modifies external state (the 'd' list) as a side effect with no indication in the signature.
     * BAD: Returns an integer code (0, 1, 2) instead of a meaningful result or exception.
     */
    public int checkAndDoStuff(String inp) {
        if (inp == null) {
            return 0; // BAD: Magic return code — callers must know 0 means "null input".
        }
        if (inp.length() < 3) {
            return 1; // BAD: Magic return code — callers must know 1 means "too short".
        }
        // BAD: Silently mutates shared state.
        d.add(inp);
        d.add(inp.toUpperCase());
        // BAD: Magic return code.
        return 2;
    }

    public static void main(String[] args) {
        // BAD: Object created with cryptic arguments — reader has no idea what 99.99, 600, "PREMIUM" mean.
        DirtyCodeExample e = new DirtyCodeExample(99.99, 600, "PREMIUM");

        // BAD: Boolean arguments in call site are completely unreadable.
        // What does 'true, false' mean here?
        double result = e.calc("PREMIUM", 600, true, false);
        System.out.println("Result: " + result);

        String joined = e.proc(new String[]{"apple", "banana", null, "", "cherry"});
        System.out.println("Joined: " + joined);

        int code = e.checkAndDoStuff("hello");
        // BAD: Caller must know magic code meanings.
        if (code == 2) {
            System.out.println("Added successfully");
        }
    }
}
