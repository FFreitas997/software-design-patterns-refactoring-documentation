# 📐 Software Development Fundamentals

> Four foundational acronyms every software developer should master: **CRUD**, **POJO**, **ACID**, and **SOLID**. Together they span database operations, data modeling, transaction integrity, and object-oriented code quality.

---

## 📚 Table of Contents

| Concept | Domain | Description |
|---|---|---|
| [CRUD](#-crud) | Database Operations | The four basic operations on persistent data |
| [POJO](#-pojo) | Data Modeling | Plain, framework-free Java objects |
| [ACID](#-acid) | Transaction Integrity | Guarantees for reliable database transactions |
| [SOLID](#-solid) | Code Quality | Five principles of robust object-oriented design |

---

## 🗄️ CRUD

**CRUD** stands for **Create, Read, Update, Delete** — the four fundamental operations performed on any persistent data store (relational databases, NoSQL stores, REST APIs, etc.).

| Letter | Operation | SQL Equivalent | HTTP Method |
|---|---|---|---|
| **C** | Create | `INSERT` | `POST` |
| **R** | Read   | `SELECT` | `GET`  |
| **U** | Update | `UPDATE` | `PUT` / `PATCH` |
| **D** | Delete | `DELETE` | `DELETE` |

### Why CRUD Matters

- Provides a **universal vocabulary** for thinking about data access regardless of technology.
- Drives the design of **REST APIs**, **repository classes**, and **DAO (Data Access Object)** layers.
- Aligns directly with **HTTP verbs** and **SQL statements**, making it a bridge between layers.

### CRUD in Practice (Java Example)

```java
// See: CrudExample.java
```

➡️ [View full Java example](crud/CrudExample.java)

---

## ☕ POJO

**POJO** stands for **Plain Old Java Object**. Coined by Martin Fowler, Rebecca Parsons, and Josh MacKenzie in 2000, it describes a simple Java object that:

- Does **not** extend framework-specific classes (e.g., `javax.servlet.HttpServlet`).
- Does **not** implement framework-specific interfaces.
- Has **no special annotations required** to function.
- Relies only on the **Java Language Specification**.

### Anatomy of a POJO

A typical POJO has:
1. **Private fields** — encapsulated state.
2. **No-argument constructor** — required by many frameworks (JPA, Jackson, etc.).
3. **Getters & Setters** — controlled access to state.
4. **`equals()`, `hashCode()`, `toString()`** — standard object contracts.

### POJO vs Related Concepts

| Term | Meaning |
|---|---|
| **POJO** | Any plain Java object with no framework dependency |
| **JavaBean** | A POJO that strictly follows the JavaBeans spec (serializable, no-arg constructor, getters/setters) |
| **DTO** (Data Transfer Object) | A POJO used specifically to carry data between layers/services |
| **Entity** | A POJO annotated with `@Entity` (JPA), representing a database table row |
| **Value Object** | An immutable POJO representing a descriptive aspect of a domain |

### POJO in Practice (Java Example)

```java
// See: PojoExample.java
```

➡️ [View full Java example](pojo/PojoExample.java)

---

## 🔒 ACID

**ACID** is a set of four properties that guarantee **database transactions** are processed reliably, even in the face of errors, crashes, or concurrent access.

| Letter | Property | Guarantee |
|---|---|---|
| **A** | Atomicity    | A transaction is **all-or-nothing** — it either completes fully or is completely rolled back. |
| **C** | Consistency  | A transaction brings the database from one **valid state to another**, never leaving it in an invalid state. |
| **I** | Isolation    | Concurrent transactions execute as if they were **serial** — one at a time. |
| **D** | Durability   | Once committed, a transaction's changes are **permanently persisted**, surviving crashes. |

### Deep Dive

#### ⚛️ Atomicity
If any step of a transaction fails, the entire transaction is rolled back. Example: transferring money between bank accounts — either both the debit *and* the credit happen, or neither does.

#### ✅ Consistency
All data integrity rules (constraints, cascades, triggers) are enforced. A transaction cannot leave a foreign key dangling or a balance negative if a `CHECK` constraint forbids it.

#### 🔀 Isolation Levels
SQL standard defines four isolation levels that trade safety for performance:

| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---|---|---|
| `READ UNCOMMITTED` | ✅ Possible | ✅ Possible | ✅ Possible |
| `READ COMMITTED`   | ❌ Prevented | ✅ Possible | ✅ Possible |
| `REPEATABLE READ`  | ❌ Prevented | ❌ Prevented | ✅ Possible |
| `SERIALIZABLE`     | ❌ Prevented | ❌ Prevented | ❌ Prevented |

#### 💾 Durability
Achieved through write-ahead logging (WAL) and persistent storage. Once a `COMMIT` succeeds, the data survives power loss or system crash.

### ACID in Practice (Java Example)

```java
// See: AcidExample.java
```

➡️ [View full Java example](acid/AcidExample.java)

---

## 🏛️ SOLID

**SOLID** is an acronym for five object-oriented design principles introduced by Robert C. Martin ("Uncle Bob"). They guide developers toward code that is **easy to maintain, extend, and understand**.

| Letter | Principle | One-Line Summary |
|---|---|---|
| **S** | Single Responsibility Principle (SRP) | A class should have **only one reason to change**. |
| **O** | Open/Closed Principle (OCP) | Classes should be **open for extension, closed for modification**. |
| **L** | Liskov Substitution Principle (LSP) | Subtypes must be **substitutable** for their base types. |
| **I** | Interface Segregation Principle (ISP) | Clients should not be **forced to depend on interfaces they don't use**. |
| **D** | Dependency Inversion Principle (DIP) | Depend on **abstractions**, not concretions. |

### Deep Dive

---

#### 🔵 S — Single Responsibility Principle (SRP)

> *"A class should have one, and only one, reason to change."*

A class with multiple responsibilities becomes fragile — changing one responsibility may break others.

**❌ Bad:**
```java
class UserService {
    public void saveUser(User user) { /* DB logic */ }
    public void sendWelcomeEmail(User user) { /* Email logic */ }
    public String generateReport(User user) { /* Report logic */ }
}
```

**✅ Good:**
```java
class UserRepository { public void save(User user) { /* DB logic */ } }
class EmailService    { public void sendWelcome(User user) { /* Email */ } }
class ReportGenerator { public String generate(User user) { /* Report */ } }
```

---

#### 🟢 O — Open/Closed Principle (OCP)

> *"Software entities should be open for extension, but closed for modification."*

New behavior should be added by writing new code, not by editing existing (tested) code.

**❌ Bad:**
```java
class DiscountCalculator {
    public double calculate(String customerType, double price) {
        if (customerType.equals("VIP")) return price * 0.8;
        if (customerType.equals("REGULAR")) return price * 0.95;
        return price; // Adding a new type requires editing this class
    }
}
```

**✅ Good:**
```java
interface DiscountStrategy { double apply(double price); }
class VipDiscount     implements DiscountStrategy { public double apply(double p) { return p * 0.80; } }
class RegularDiscount implements DiscountStrategy { public double apply(double p) { return p * 0.95; } }

class DiscountCalculator {
    public double calculate(DiscountStrategy strategy, double price) {
        return strategy.apply(price); // New types added without touching this class
    }
}
```

---

#### 🟡 L — Liskov Substitution Principle (LSP)

> *"Objects of a subclass should be replaceable with objects of the superclass without breaking the application."*

Subclasses must honor the contract of their parent. Overriding a method to throw an exception or do nothing violates LSP.

**❌ Bad:**
```java
class Rectangle {
    int width, height;
    void setWidth(int w)  { this.width = w; }
    void setHeight(int h) { this.height = h; }
    int area() { return width * height; }
}

class Square extends Rectangle {
    @Override void setWidth(int w)  { this.width = this.height = w; } // breaks Rectangle's contract
    @Override void setHeight(int h) { this.width = this.height = h; }
}
```

**✅ Good:** Model `Square` and `Rectangle` as separate classes implementing a common `Shape` interface, or use composition over inheritance.

---

#### 🟠 I — Interface Segregation Principle (ISP)

> *"No client should be forced to depend on methods it does not use."*

Prefer many small, focused interfaces over one large ("fat") interface.

**❌ Bad:**
```java
interface Worker {
    void work();
    void eat();
    void sleep();
}
// A Robot implementing Worker is forced to implement eat() and sleep()
```

**✅ Good:**
```java
interface Workable  { void work(); }
interface Feedable  { void eat();  }
interface Restable  { void sleep(); }

class Human implements Workable, Feedable, Restable { /* all make sense */ }
class Robot implements Workable                     { /* only work() needed */ }
```

---

#### 🔴 D — Dependency Inversion Principle (DIP)

> *"High-level modules should not depend on low-level modules. Both should depend on abstractions."*

Introduce interfaces/abstractions between layers so that high-level policy is decoupled from low-level implementation details.

**❌ Bad:**
```java
class OrderService {
    private MySQLOrderRepository repo = new MySQLOrderRepository(); // tightly coupled
}
```

**✅ Good:**
```java
interface OrderRepository { void save(Order order); }
class MySQLOrderRepository implements OrderRepository { /* ... */ }
class MongoOrderRepository implements OrderRepository { /* ... */ }

class OrderService {
    private final OrderRepository repo; // depends on abstraction
    public OrderService(OrderRepository repo) { this.repo = repo; } // injected
}
```

### SOLID in Practice (Java Example)

```java
// See: SolidExample.java
```

➡️ [View full Java example](solid/SolidExample.java)

---

## 🔗 How They Relate

```
┌─────────────────────────────────────────────────────────────────┐
│                   Software Development Stack                    │
├──────────────┬──────────────┬──────────────┬────────────────────┤
│    SOLID     │     POJO     │     CRUD     │       ACID         │
│  Code Quality│ Data Modeling│  DB Ops API  │  Transaction Safety│
│  (OOP Layer) │ (Model Layer)│ (Data Layer) │  (DB Engine Layer) │
└──────────────┴──────────────┴──────────────┴────────────────────┘
```

- A **POJO** is often both the subject of **CRUD** operations and the unit whose design is guided by **SOLID**.
- **CRUD** operations on a database should happen inside transactions that satisfy **ACID** properties.
- **SOLID** principles (especially DIP) help you design repository/service layers that abstract **CRUD** behind clean interfaces.
- **ACID** is the safety net that makes multi-step **CRUD** operations trustworthy in concurrent environments.

---

## 📖 References

- *Clean Code: A Handbook of Agile Software Craftsmanship* — Robert C. Martin
- *Agile Software Development, Principles, Patterns, and Practices* — Robert C. Martin
- *Patterns of Enterprise Application Architecture* — Martin Fowler
- *Database System Concepts* — Silberschatz, Korth, Sudarshan
- [SOLID Principles (Wikipedia)](https://en.wikipedia.org/wiki/SOLID)
- [ACID (Wikipedia)](https://en.wikipedia.org/wiki/ACID)

