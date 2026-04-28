# Moving Features Between Objects

These refactoring techniques help you place each responsibility in the right class. When a method or field "belongs" more in another class — or when a class has grown too large — these techniques let you reorganise without breaking behaviour.

---

## 1. Move Method

**Problem:** A method uses features of another class more than its own.
**Solution:** Create a new method with a similar body in the class it uses most. Either turn the old method into a simple delegation or remove it entirely.

```java
// BEFORE — AccountType logic lives in Account
class Account {
    AccountType type;
    double overdraftCharge() {
        if (type.isPremium()) return Math.max(10, daysOverdrawn * 2.5);
        return daysOverdrawn * 1.75;
    }
}

// AFTER — calculation moved to AccountType where the type data lives
class AccountType {
    boolean isPremium() { return /* ... */ true; }
    double overdraftCharge(int daysOverdrawn) {
        return isPremium()
                ? Math.max(10, daysOverdrawn * 2.5)
                : daysOverdrawn * 1.75;
    }
}
class Account {
    AccountType type;
    int daysOverdrawn;
    double overdraftCharge() { return type.overdraftCharge(daysOverdrawn); }
}
```

---

## 2. Move Field

**Problem:** A field is used more by another class than by its own class.
**Solution:** Create a new field in the target class and redirect all users of the old field.

```java
// BEFORE — interestRate lives in Account but belongs to AccountType
class Account {
    AccountType type;
    double interestRate; // used primarily to determine account type behaviour
}

// AFTER — interestRate moved to AccountType
class AccountType {
    private double interestRate;
    double getInterestRate() { return interestRate; }
}
class Account {
    AccountType type;
    double getInterestRate() { return type.getInterestRate(); }
}
```

---

## 3. Extract Class

**Problem:** One class is doing the work of two.
**Solution:** Create a new class, and move the relevant fields and methods into the new class.

```java
// BEFORE — Person holds both personal and phone number concerns
class Person {
    String name;
    String officeAreaCode, officeNumber;

    String telephoneNumber() { return "(" + officeAreaCode + ") " + officeNumber; }
}

// AFTER — telephone concerns live in TelephoneNumber
class TelephoneNumber {
    private String areaCode, number;
    TelephoneNumber(String areaCode, String number) { this.areaCode = areaCode; this.number = number; }
    String toString() { return "(" + areaCode + ") " + number; }
}

class Person {
    String name;
    TelephoneNumber officeTelephone;
    String telephoneNumber() { return officeTelephone.toString(); }
}
```

---

## 4. Inline Class

**Problem:** A class is no longer pulling its weight — it does too little.
**Solution:** Move all features of the class into another class, and delete the empty shell.

```java
// BEFORE — TelephoneNumber is now too small to justify existing
class TelephoneNumber { String number; }
class Person { TelephoneNumber phone; }

// AFTER — inlined
class Person {
    String phoneNumber; // field absorbed directly
}
```

---

## 5. Hide Delegate

**Problem:** A client calls into a delegate through a chain: `person.getDepartment().getManager()`.
**Solution:** Add a delegate method on the server class to hide the chain.

```java
// BEFORE — client must know Person has a Department
Manager manager = john.getDepartment().getManager();

// AFTER — Person hides the delegation chain
class Person {
    private Department department;
    Manager getManager() { return department.getManager(); } // hidden delegate
}
// Client:
Manager manager = john.getManager();
```

---

## 6. Remove Middle Man

**Problem:** A class is doing nothing but forwarding calls to a delegate — too much delegation.
**Solution:** Make the client call the delegate directly.

```java
// BEFORE — Person just delegates getManager() to Department
class Person {
    Department department;
    Manager getManager() { return department.getManager(); } // pure forwarding
}
// Client: john.getManager()

// AFTER — client calls Department directly
// Client: john.getDepartment().getManager()
// Person.getManager() method deleted
```

---

## 7. Introduce Foreign Method

**Problem:** A server class needs an additional method, but you cannot modify the class (e.g., a library class).
**Solution:** Create a static utility method in the client class that takes an instance of the server class as its first parameter.

```java
// BEFORE — date arithmetic repeated at the call site
Date newStart = new Date(previousEnd.getYear(), previousEnd.getMonth(), previousEnd.getDate() + 1);

// AFTER — static utility method used as a "foreign" method on Date
static Date nextDay(Date date) {
    return new Date(date.getYear(), date.getMonth(), date.getDate() + 1);
}
// Client:
Date newStart = nextDay(previousEnd);
```

---

## 8. Introduce Local Extension

**Problem:** A server class needs several additional methods, but you cannot modify it.
**Solution:** Create a new class (subclass or wrapper) that contains the extra methods and extends or wraps the original.

```java
// Subclass approach
class MfDate extends java.util.Date {
    MfDate(long time) { super(time); }

    MfDate nextDay() {
        return new MfDate(this.getTime() + 86_400_000L);
    }

    boolean isWeekend() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(this);
        int day = c.get(java.util.Calendar.DAY_OF_WEEK);
        return day == java.util.Calendar.SATURDAY || day == java.util.Calendar.SUNDAY;
    }
}
```

---

## See Also
- [MovingFeatures.java](./MovingFeatures.java) — compilable before/after Java examples for all eight techniques
