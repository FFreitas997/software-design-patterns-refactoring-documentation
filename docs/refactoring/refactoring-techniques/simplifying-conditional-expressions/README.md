# Simplifying Conditional Expressions

Conditional logic is among the most common sources of complexity in programs. These refactoring techniques make conditions easier to read, understand, and maintain.

---

## 1. Decompose Conditional

**Problem:** A complex `if/else` condition is hard to read because the condition and the branches are long.
**Solution:** Extract the condition into a method, and extract each branch body into its own method.

```java
// BEFORE
if (date.before(SUMMER_START) || date.after(SUMMER_END)) {
    charge = quantity * winterRate + winterServiceCharge;
} else {
    charge = quantity * summerRate;
}

// AFTER
charge = isSummer(date) ? summerCharge(quantity) : winterCharge(quantity);

boolean isSummer(Date date) { return !date.before(SUMMER_START) && !date.after(SUMMER_END); }
double  summerCharge(int q) { return q * summerRate; }
double  winterCharge(int q) { return q * winterRate + winterServiceCharge; }
```

---

## 2. Consolidate Conditional Expression

**Problem:** Several conditions all lead to the same result.
**Solution:** Combine them into a single condition expression and extract it into a method.

```java
// BEFORE
double disabilityAmount() {
    if (seniority < 2)     return 0;
    if (monthsDisabled > 12) return 0;
    if (isPartTime)        return 0;
    return /* calculate */ 1.0;
}

// AFTER
double disabilityAmount() {
    if (isNotEligibleForDisability()) return 0;
    return /* calculate */ 1.0;
}
boolean isNotEligibleForDisability() {
    return seniority < 2 || monthsDisabled > 12 || isPartTime;
}
```

---

## 3. Consolidate Duplicate Conditional Fragments

**Problem:** The same code appears in every branch of a conditional.
**Solution:** Move it outside the conditional.

```java
// BEFORE
if (isSpecialDeal()) {
    total = price * 0.95;
    send();        // ← duplicated in both branches
} else {
    total = price * 0.98;
    send();        // ← duplicated
}

// AFTER
total = isSpecialDeal() ? price * 0.95 : price * 0.98;
send(); // moved outside — executed in both cases anyway
```

---

## 4. Remove Control Flag

**Problem:** A boolean variable acting as a control flag for a series of boolean expressions makes the logic hard to follow.
**Solution:** Use `break`, `continue`, or `return` instead.

```java
// BEFORE
boolean found = false;
for (String name : names) {
    if (!found) {
        if (name.equals("Don"))  { sendAlert(); found = true; }
        if (name.equals("John")) { sendAlert(); found = true; }
    }
}

// AFTER
for (String name : names) {
    if (name.equals("Don") || name.equals("John")) {
        sendAlert();
        break; // natural exit — no flag needed
    }
}
```

---

## 5. Replace Nested Conditional with Guard Clauses

**Problem:** A method has conditional behaviour that does not make clear which is the "normal" path.
**Solution:** Use guard clauses for all special cases, then let the normal case flow through without nesting.

```java
// BEFORE — normal case buried under layers of nesting
double getPayAmount() {
    double result;
    if (isDead) {
        result = deadAmount();
    } else {
        if (isSeparated) {
            result = separatedAmount();
        } else {
            if (isRetired) {
                result = retiredAmount();
            } else {
                result = normalPayAmount();
            }
        }
    }
    return result;
}

// AFTER — guard clauses handle exceptions upfront; normal case is obvious
double getPayAmount() {
    if (isDead)      return deadAmount();
    if (isSeparated) return separatedAmount();
    if (isRetired)   return retiredAmount();
    return normalPayAmount();
}
```

---

## 6. Replace Conditional with Polymorphism

**Problem:** A conditional checks the type or state of an object to decide what to do.
**Solution:** Move each branch into an overriding method in a subclass.

```java
// BEFORE
double getSpeed() {
    switch (type) {
        case EUROPEAN: return baseSpeed();
        case AFRICAN:  return baseSpeed() - loadFactor() * numberOfCoconuts;
        case NORWEGIAN_BLUE: return isNailed ? 0 : baseSpeed();
    }
    throw new RuntimeException("Unreachable");
}

// AFTER
abstract class Bird  { abstract double getSpeed(); }
class European extends Bird      { double getSpeed() { return baseSpeed(); } }
class African  extends Bird      { double getSpeed() { return baseSpeed() - loadFactor() * numberOfCoconuts; } }
class NorwegianBlue extends Bird { double getSpeed() { return isNailed ? 0 : baseSpeed(); } }
```

---

## 7. Introduce Null Object

**Problem:** Null checks of the same form are scattered throughout the code.
**Solution:** Create a null object that implements the same interface as the real object but with do-nothing or default behaviour.

```java
// BEFORE — null check before every use
if (customer != null)
    plan = customer.getPlan();
else
    plan = BillingPlan.basic();

// AFTER — NullCustomer implements the same interface
class NullCustomer extends Customer {
    @Override BillingPlan getPlan() { return BillingPlan.basic(); }
    @Override boolean isNull()      { return true; }
}
// Customer can never be null — use NullCustomer instead
BillingPlan plan = customer.getPlan(); // no null check needed
```

---

## 8. Introduce Assertion

**Problem:** A section of code works only if a certain condition is true, but this assumption is not documented.
**Solution:** Make the assumption explicit with an assertion.

```java
// BEFORE — silent assumption that discountRate >= 0
double applyDiscount(double amount) {
    return amount - (amount * discountRate);
}

// AFTER — assumption is stated explicitly
double applyDiscount(double amount) {
    assert discountRate >= 0 : "Discount rate must be non-negative, was: " + discountRate;
    return amount - (amount * discountRate);
}
```

---

## See Also
- [SimplifyingConditionals.java](./SimplifyingConditionals.java) — compilable before/after Java examples for all eight techniques
