# Code Smells

A **code smell** is a surface indication that usually corresponds to a deeper problem in the system. The term was popularised by Kent Beck and Martin Fowler. A smell is not a bug — the code may work correctly — but it signals that the design could be improved, making the code harder to maintain, extend, or understand over time.

Code smells are grouped into five categories, each reflecting a different kind of structural weakness:

---

## Categories

### 🐘 [Bloaters](./bloaters/README.md)
Code, methods, and classes that have grown so large they are hard to work with. Size is rarely a virtue in source code.

| Smell | Summary |
|-------|---------|
| Long Method | A method that has grown too long to understand easily |
| Large Class | A class that has accumulated too many responsibilities and fields |
| Primitive Obsession | Using primitives where a small object would be clearer |
| Long Parameter List | A method that requires too many arguments |
| Data Clumps | Groups of data that always appear together but have no class of their own |

---

### 🔧 [Object-Orientation Abusers](./object-orientation-abusers/README.md)
Code that applies OO principles incorrectly or incompletely.

| Smell | Summary |
|-------|---------|
| Switch Statements | Complex switch/if-else trees that should be replaced by polymorphism |
| Temporary Field | Fields that are only set in certain circumstances |
| Refused Bequest | A subclass that ignores inherited methods or data |
| Alternative Classes with Different Interfaces | Two classes that do the same thing via different method names |

---

### 🚧 [Change Preventers](./change-preventers/README.md)
Smells that make change difficult — touching one thing forces you to touch many other things.

| Smell | Summary |
|-------|---------|
| Divergent Change | One class is changed for many different reasons |
| Shotgun Surgery | One change requires many small changes across many classes |
| Parallel Inheritance Hierarchies | Adding a subclass in one hierarchy forces a parallel addition elsewhere |

---

### 🗑️ [Dispensables](./dispensables/README.md)
Things that are unnecessary — their removal would make the code cleaner and simpler.

| Smell | Summary |
|-------|---------|
| Comments | Over-reliance on comments to explain confusing code |
| Duplicate Code | The same code structure in more than one place |
| Lazy Class | A class that does too little to justify its existence |
| Data Class | A class with only fields and getters/setters, no real behaviour |
| Dead Code | Code that is never executed |
| Speculative Generality | Over-engineering for hypothetical future requirements |

---

### 🔗 [Couplers](./couplers/README.md)
Smells that contribute to excessive coupling between classes.

| Smell | Summary |
|-------|---------|
| Feature Envy | A method that seems more interested in another class than its own |
| Inappropriate Intimacy | Classes that are too knowledgeable about each other's internals |
| Message Chains | Long chains of `a.getB().getC().getD()` calls |
| Middle Man | A class that does nothing but delegate to another |
| Incomplete Library Class | A library class that is missing needed functionality |

---

## How to Use This Catalog

1. **Identify** the smell category that best describes the problem you see
2. **Navigate** to that category's README for detailed descriptions and Java examples
3. **Cross-reference** the corresponding [Refactoring Techniques](../refactoring-techniques/README.md) to see which technique cures the smell
4. **Apply** the technique in small, test-covered steps
