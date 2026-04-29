import java.util.Objects;

/**
 * POJO — Plain Old Java Object
 *
 * Demonstrates what a POJO is and how it compares to related concepts:
 *   - Raw POJO
 *   - JavaBean (strict POJO + Serializable + no-arg constructor + getters/setters)
 *   - DTO        (POJO used to carry data between layers)
 *   - Value Object (immutable POJO)
 *
 * None of these classes extend or implement any framework-specific type.
 */
public class PojoExample {

    // ─────────────────────────────────────────────────────────────
    // 1. Basic POJO
    //    No rules — just a plain Java class with no framework ties.
    // ─────────────────────────────────────────────────────────────

    static class UserPojo {
        String name;
        String email;
        int    age;

        UserPojo(String name, String email, int age) {
            this.name  = name;
            this.email = email;
            this.age   = age;
        }

        @Override
        public String toString() {
            return "UserPojo{name='" + name + "', email='" + email + "', age=" + age + "}";
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. JavaBean
    //    Stricter POJO: Serializable, no-arg constructor, getters/setters.
    //    Required by many frameworks (JPA, JSF, Spring MVC form binding, etc.)
    // ─────────────────────────────────────────────────────────────

    static class UserBean implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private String email;
        private int    age;

        // No-arg constructor (mandatory for JavaBean)
        public UserBean() {}

        public UserBean(String name, String email, int age) {
            this.name  = name;
            this.email = email;
            this.age   = age;
        }

        public String getName()        { return name;  }
        public void   setName(String n){ this.name = n; }

        public String getEmail()        { return email;  }
        public void   setEmail(String e){ this.email = e; }

        public int  getAge()      { return age;  }
        public void setAge(int a) { this.age = a; }

        @Override public String toString() {
            return "UserBean{name='" + name + "', email='" + email + "', age=" + age + "}";
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 3. DTO — Data Transfer Object
    //    A POJO whose sole purpose is to carry data between layers
    //    (e.g., from service layer to REST controller response).
    //    Often has no business logic whatsoever.
    // ─────────────────────────────────────────────────────────────

    static class UserResponseDto {
        private final int    id;
        private final String name;
        private final String email;

        public UserResponseDto(int id, String name, String email) {
            this.id    = id;
            this.name  = name;
            this.email = email;
        }

        public int    getId()    { return id;    }
        public String getName()  { return name;  }
        public String getEmail() { return email; }

        @Override public String toString() {
            return "UserResponseDto{id=" + id + ", name='" + name + "', email='" + email + "'}";
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. Value Object
    //    An immutable POJO whose identity is defined by its values,
    //    not by a reference or ID.  equals() and hashCode() are based
    //    on field values, not object identity.
    // ─────────────────────────────────────────────────────────────

    static final class Money {
        private final double amount;
        private final String currency;

        public Money(double amount, String currency) {
            if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative.");
            this.amount   = amount;
            this.currency = currency;
        }

        public double getAmount()   { return amount;   }
        public String getCurrency() { return currency; }

        public Money add(Money other) {
            if (!this.currency.equals(other.currency))
                throw new IllegalArgumentException("Currency mismatch: " + this.currency + " vs " + other.currency);
            return new Money(this.amount + other.amount, this.currency);
        }

        // Value equality — two Money objects are equal if amount AND currency match
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Money)) return false;
            Money m = (Money) o;
            return Double.compare(m.amount, amount) == 0 && Objects.equals(currency, m.currency);
        }

        @Override
        public int hashCode() { return Objects.hash(amount, currency); }

        @Override
        public String toString() { return amount + " " + currency; }
    }

    // ─────────────────────────────────────────────────────────────
    // Demo
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        System.out.println("=== 1. Basic POJO ===");
        UserPojo pojo = new UserPojo("Alice", "alice@example.com", 30);
        System.out.println(pojo);

        System.out.println("\n=== 2. JavaBean ===");
        UserBean bean = new UserBean();     // no-arg constructor used by frameworks
        bean.setName("Bob");
        bean.setEmail("bob@example.com");
        bean.setAge(25);
        System.out.println(bean);

        System.out.println("\n=== 3. DTO ===");
        UserResponseDto dto = new UserResponseDto(42, "Charlie", "charlie@example.com");
        System.out.println("Serialized to response: " + dto);

        System.out.println("\n=== 4. Value Object (Money) ===");
        Money price    = new Money(19.99, "USD");
        Money tax      = new Money(2.00,  "USD");
        Money total    = price.add(tax);
        Money sameTotal = new Money(21.99, "USD");
        System.out.println("Price:       " + price);
        System.out.println("Tax:         " + tax);
        System.out.println("Total:       " + total);
        System.out.println("Equal check (21.99 USD == 21.99 USD): " + total.equals(sameTotal));
    }
}

