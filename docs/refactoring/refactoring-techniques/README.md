# Refactoring Techniques

A **refactoring technique** is a named, well-defined transformation of code that preserves observable behaviour while improving internal structure. Each technique has a precise description, a motivation, and a step-by-step mechanical procedure that can often be automated by IDE tools.

The techniques are grouped into five categories reflecting the structural concern they address:

---

## Categories

### 🔨 [Composing Methods](./composing-methods/README.md)
Techniques for creating well-composed methods — methods that are the right size, do one thing, and communicate their intent clearly.

| Technique | Summary |
|-----------|---------|
| Extract Method | Turn a code fragment into a new named method |
| Inline Method | Replace a method call with the method body |
| Extract Variable | Give a name to a complex expression |
| Inline Temp | Replace a temp variable with the expression it holds |
| Replace Temp with Query | Replace a local variable with a method call |
| Split Temporary Variable | Give each use of a temp variable its own variable |
| Remove Assignments to Parameters | Use a local variable instead of re-assigning parameters |
| Replace Method with Method Object | Turn a complex method into its own class |
| Substitute Algorithm | Replace the body of a method with a cleaner algorithm |

---

### 🚚 [Moving Features Between Objects](./moving-features/README.md)
Techniques for placing responsibilities in the right classes.

| Technique | Summary |
|-----------|---------|
| Move Method | Move a method to the class it uses most |
| Move Field | Move a field to the class that needs it most |
| Extract Class | Move related features into a new, focused class |
| Inline Class | Absorb a tiny class back into its caller |
| Hide Delegate | Add a shortcut method to avoid message chains |
| Remove Middle Man | Make clients call the delegate directly |
| Introduce Foreign Method | Add a utility method for a class you cannot modify |
| Introduce Local Extension | Subclass or wrap a library class to add missing behaviour |

---

### 📦 [Organizing Data](./organizing-data/README.md)
Techniques for improving how data is represented and accessed.

| Technique | Summary |
|-----------|---------|
| Self Encapsulate Field | Access own fields via getters/setters |
| Replace Data Value with Object | Replace a primitive with a small value object |
| Change Value to Reference | Make identical objects share a single instance |
| Change Reference to Value | Replace a reference object with an immutable value object |
| Replace Array with Object | Replace a multi-purpose array with a class |
| Duplicate Observed Data | Keep domain data in a domain object, not the UI |
| Replace Magic Number with Symbolic Constant | Name the number |
| Encapsulate Field | Make a public field private and add accessor methods |
| Encapsulate Collection | Return an unmodifiable view of a collection field |
| Replace Type Code with Class / Subclasses / State / Strategy | Replace an integer type code with a proper type |

---

### 🔀 [Simplifying Conditional Expressions](./simplifying-conditional-expressions/README.md)
Techniques for making conditional logic clearer.

| Technique | Summary |
|-----------|---------|
| Decompose Conditional | Extract complex conditions into named methods |
| Consolidate Conditional Expression | Merge several conditions with the same result |
| Consolidate Duplicate Conditional Fragments | Move identical code out of all branches |
| Remove Control Flag | Replace a boolean flag variable with `break`/`return` |
| Replace Nested Conditional with Guard Clauses | Use early returns to flatten nesting |
| Replace Conditional with Polymorphism | Move switch/if on type to subclass methods |
| Introduce Null Object | Replace null checks with a do-nothing object |
| Introduce Assertion | Document and verify assumptions with assertions |

---

### 📞 [Simplifying Method Calls](./simplifying-method-calls/README.md)
Techniques for improving method signatures and the interaction between objects.

| Technique | Summary |
|-----------|---------|
| Rename Method | Give a method a name that communicates its intent |
| Add / Remove Parameter | Adjust the method signature |
| Separate Query from Modifier | Split methods that both return values and change state |
| Parameterize Method | Merge similar methods into one with a parameter |
| Replace Parameter with Explicit Methods | Split one parameterized method into several |
| Preserve Whole Object | Pass the whole object instead of extracting fields |
| Replace Parameter with Method Call | Call the method instead of passing its result as a parameter |
| Introduce Parameter Object | Replace a group of parameters with an object |
| Remove Setting Method | Remove a setter to make a field immutable |
| Hide Method | Change public methods that are only used internally to private |
| Replace Constructor with Factory Method | Use a factory for richer creation semantics |
| Replace Error Code with Exception | Throw exceptions instead of returning error codes |
| Replace Exception with Test | Use a pre-check instead of catching an exception for control flow |

---

### 🧬 [Dealing with Generalization](./dealing-with-generalization/README.md)
Techniques for working with inheritance hierarchies.

| Technique | Summary |
|-----------|---------|
| Pull Up Field / Method / Constructor Body | Move common elements to a superclass |
| Push Down Method / Field | Move specialised elements to a subclass |
| Extract Subclass | Create a subclass for a subset of behaviours |
| Extract Superclass | Move shared features into a new superclass |
| Extract Interface | Extract a subset of features into an interface |
| Collapse Hierarchy | Merge a subclass back into its superclass |
| Form Template Method | Pull common algorithm skeleton to superclass |
| Replace Inheritance with Delegation | Favour composition over inheritance |
| Replace Delegation with Inheritance | Inherit instead of delegating when appropriate |
