# Simplifying Method Calls

These refactoring techniques improve method signatures and the interactions between objects. They make interfaces more intention-revealing, reduce unnecessary coupling, and enforce good API design practices.

---

## 1. Rename Method

**Problem:** A method's name does not communicate its purpose.
**Solution:** Give the method a name that clearly describes what it does.

```java
// BEFORE
int getPNumber() { return phoneNumber; }

// AFTER
int getTelephoneNumber() { return phoneNumber; }
```

---

## 2. Add Parameter

**Problem:** A method needs more information from its caller.
**Solution:** Add a parameter with the new data.

```java
// BEFORE
void getContact() { /* uses only stored contact */ }

// AFTER
void getContact(Date date) { /* can now use date to find correct contact */ }
```

---

## 3. Remove Parameter

**Problem:** A parameter is no longer used by the method body.
**Solution:** Remove it.

```java
// BEFORE
double discount(Customer customer, boolean unused) { return customer.getLoyaltyDiscount(); }

// AFTER
double discount(Customer customer) { return customer.getLoyaltyDiscount(); }
```

---

## 4. Separate Query from Modifier

**Problem:** A method returns a value AND has a side effect.
**Solution:** Create two methods — one to query, one to modify. Callers compose them.

```java
// BEFORE — getTotalOutstandingAndSendBill() does two things
double getTotalOutstandingAndSendBill() {
    double result = getInvoices().stream().mapToDouble(Invoice::getAmount).sum();
    sendBill(); // side effect mixed with query
    return result;
}

// AFTER — two single-purpose methods
double getTotalOutstanding() { return getInvoices().stream().mapToDouble(Invoice::getAmount).sum(); }
void   sendBill()            { /* send */ }
// Caller: double total = getTotalOutstanding(); sendBill();
```

---

## 5. Parameterize Method

**Problem:** Several methods do similar things but with different values. Duplication results.
**Solution:** Merge them into one method that takes a parameter for the varying part.

```java
// BEFORE
void fivePercentRaise()  { salary *= 1.05; }
void tenPercentRaise()   { salary *= 1.10; }

// AFTER
void raise(double factor) { salary *= (1 + factor); }
```

---

## 6. Replace Parameter with Explicit Methods

**Problem:** A method has an enumerated parameter that runs completely different code for each value.
**Solution:** Create a separate method for each value.

```java
// BEFORE
void setValue(String name, int value) {
    if      (name.equals("height")) height = value;
    else if (name.equals("width"))  width  = value;
}

// AFTER
void setHeight(int value) { height = value; }
void setWidth(int value)  { width  = value; }
```

---

## 7. Preserve Whole Object

**Problem:** You are extracting several values from an object and passing them as separate parameters.
**Solution:** Pass the whole object instead.

```java
// BEFORE — extracting two values from a range just to pass them
int low  = daysTempRange.getLow();
int high = daysTempRange.getHigh();
boolean withinRange = plan.withinRange(low, high);

// AFTER — pass the whole object
boolean withinRange = plan.withinRange(daysTempRange);
```

---

## 8. Replace Parameter with Method Call

**Problem:** A parameter is the result of calling a method that is also accessible to the callee. The callee can call it directly.
**Solution:** Remove the parameter and have the method call the other method itself.

```java
// BEFORE
int basePrice   = quantity * itemPrice;
double discount = discountLevel();
double price    = discountedPrice(basePrice, discount); // discount passed as param

// AFTER
double price = discountedPrice(basePrice);
// discountedPrice calls discountLevel() internally
```

---

## 9. Introduce Parameter Object

**Problem:** A group of parameters naturally go together.
**Solution:** Replace them with an object.

```java
// BEFORE
double amountInvoicedIn(Date start, Date end)  { /* ... */ return 0; }
double amountReceivedIn(Date start, Date end)  { /* ... */ return 0; }

// AFTER
class DateRange {
    final Date start, end;
    DateRange(Date start, Date end) { this.start = start; this.end = end; }
}
double amountInvoicedIn(DateRange range) { /* ... */ return 0; }
double amountReceivedIn(DateRange range) { /* ... */ return 0; }
```

---

## 10. Remove Setting Method

**Problem:** A field should be set only once at construction time and never change.
**Solution:** Remove the setter and make the field final.

```java
// BEFORE
class Customer {
    private String customerCode;
    public void setCustomerCode(String code) { this.customerCode = code; }
}

// AFTER
class Customer {
    private final String customerCode; // immutable
    public Customer(String code) { this.customerCode = code; }
}
```

---

## 11. Hide Method

**Problem:** A method is not used by any other class — it only serves internal purposes.
**Solution:** Make it private.

```java
// BEFORE — unnecessarily public
public double computeInterest() { return balance * rate; }

// AFTER
private double computeInterest() { return balance * rate; }
```

---

## 12. Replace Constructor with Factory Method

**Problem:** You want to do more than a simple construction when creating an object (different subclasses, caching, named creation semantics).
**Solution:** Replace the constructor with a factory method.

```java
// BEFORE
Employee create(int type) { return new Employee(type); }

// AFTER
Employee create(int type) {
    switch (type) {
        case ENGINEER:    return new Engineer();
        case SALESPERSON: return new Salesperson();
        default: throw new IllegalArgumentException("Unknown type: " + type);
    }
}
```

---

## 13. Replace Error Code with Exception

**Problem:** A method returns a special error code to indicate failure.
**Solution:** Throw an exception instead.

```java
// BEFORE
int withdraw(int amount) {
    if (amount > balance) return -1; // error code
    balance -= amount;
    return 0;
}

// AFTER
void withdraw(int amount) {
    if (amount > balance)
        throw new InsufficientFundsException("Balance: " + balance + ", requested: " + amount);
    balance -= amount;
}
```

---

## 14. Replace Exception with Test

**Problem:** You throw an exception on a condition that the caller could check before calling.
**Solution:** Add a test so the caller checks the condition and the exception is no longer needed.

```java
// BEFORE — using exception for normal control flow
double getValueForPeriod(int period) {
    try {
        return values[period];
    } catch (ArrayIndexOutOfBoundsException e) {
        return 0;
    }
}

// AFTER — pre-condition test avoids exception-driven control flow
double getValueForPeriod(int period) {
    if (period >= values.length) return 0;
    return values[period];
}
```

---

## See Also
- [SimplifyingMethodCalls.java](./SimplifyingMethodCalls.java) — compilable before/after Java examples for all fourteen techniques
