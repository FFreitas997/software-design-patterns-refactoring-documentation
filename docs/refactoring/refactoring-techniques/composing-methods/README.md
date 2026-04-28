# Composing Methods

These refactoring techniques deal with the internal structure of methods — making them the right length, giving names to complex expressions, and ensuring each method does one clearly-named thing.

---

## 1. Extract Method

**Problem:** A code fragment can be grouped together and named.
**Solution:** Move the fragment into a new method and replace the original code with a call to the new method.

```java
// BEFORE
void printOwing(double amount) {
    System.out.println("*****");
    System.out.println("* Customer Owes *");
    System.out.println("*****");            // banner printing inline

    System.out.println("name: " + name);
    System.out.println("amount: " + amount);
}

// AFTER
void printOwing(double amount) {
    printBanner();
    printDetails(amount);
}
private void printBanner()              { System.out.println("***** Customer Owes *****"); }
private void printDetails(double amount){ System.out.println("name: " + name + ", amount: " + amount); }
```

---

## 2. Inline Method

**Problem:** A method body is just as clear as the method name — the indirection adds nothing.
**Solution:** Replace calls to the method with the method body, then delete the method.

```java
// BEFORE
int getRating()     { return moreThanFiveLateDeliveries() ? 2 : 1; }
boolean moreThanFiveLateDeliveries() { return numberOfLateDeliveries > 5; }

// AFTER
int getRating() { return numberOfLateDeliveries > 5 ? 2 : 1; }
```

---

## 3. Extract Variable

**Problem:** A complex expression is hard to understand.
**Solution:** Give the expression (or parts of it) an explanatory name.

```java
// BEFORE
return order.quantity * order.itemPrice
       - Math.max(0, order.quantity - 500) * order.itemPrice * 0.05
       + Math.min(order.quantity * order.itemPrice * 0.1, 100);

// AFTER
double basePrice       = order.quantity * order.itemPrice;
double quantityDiscount = Math.max(0, order.quantity - 500) * order.itemPrice * 0.05;
double shippingCap     = Math.min(basePrice * 0.1, 100);
return basePrice - quantityDiscount + shippingCap;
```

---

## 4. Inline Temp

**Problem:** A temporary variable is assigned once and used only in a simple expression — the variable adds no clarity.
**Solution:** Replace all references with the expression directly.

```java
// BEFORE
double basePrice = order.basePrice();
return basePrice > 1000;

// AFTER
return order.basePrice() > 1000;
```

---

## 5. Replace Temp with Query

**Problem:** You are using a local variable to hold the result of an expression. That expression can be extracted into a method, allowing the method to be shared and tested independently.
**Solution:** Extract the expression into a method; replace the variable with a call to that method.

```java
// BEFORE
double calculateTotal() {
    double basePrice = quantity * unitPrice;
    if (basePrice > 1000) return basePrice * 0.95;
    return basePrice * 0.98;
}

// AFTER
double calculateTotal() {
    if (basePrice() > 1000) return basePrice() * 0.95;
    return basePrice() * 0.98;
}
private double basePrice() { return quantity * unitPrice; }
```

---

## 6. Split Temporary Variable

**Problem:** A temporary variable is re-used for two different purposes inside the same method.
**Solution:** Give each purpose its own variable with a descriptive name.

```java
// BEFORE
double temp = 2 * (height + width);
System.out.println(temp);          // perimeter
temp = height * width;
System.out.println(temp);          // area — same 'temp' reused!

// AFTER
double perimeter = 2 * (height + width);
System.out.println(perimeter);
double area = height * width;
System.out.println(area);
```

---

## 7. Remove Assignments to Parameters

**Problem:** A parameter variable is being reassigned inside the method body, overwriting the passed-in value and confusing readers.
**Solution:** Use a local variable instead.

```java
// BEFORE
int discount(int inputValue, int quantity) {
    if (inputValue > 50) inputValue -= 2; // modifies the parameter — confusing!
    if (quantity > 100)  inputValue -= 1;
    return inputValue;
}

// AFTER
int discount(int inputValue, int quantity) {
    int result = inputValue;
    if (inputValue > 50) result -= 2;
    if (quantity  > 100) result -= 1;
    return result;
}
```

---

## 8. Replace Method with Method Object

**Problem:** A long method uses local variables in a way that prevents Extract Method from working — the variables are interdependent.
**Solution:** Turn the method into its own class. Each local variable becomes a field, and the method's logic becomes a method on the new class.

```java
// BEFORE — complex method where locals are tightly interdependent
class Order {
    double price() {
        double primaryBase   = /* complex calc involving many fields */ 0;
        double secondaryBase = /* depends on primaryBase */ 0;
        double tertiary      = /* depends on both */ 0;
        return primaryBase + secondaryBase + tertiary;
    }
}

// AFTER — each concern is a separate method on the PriceCalculator object
class PriceCalculator {
    private final Order order;
    private double primaryBase, secondaryBase, tertiary;

    PriceCalculator(Order order) { this.order = order; }

    double compute() {
        primaryBase   = calculatePrimaryBase();
        secondaryBase = calculateSecondaryBase();
        tertiary      = calculateTertiary();
        return primaryBase + secondaryBase + tertiary;
    }

    private double calculatePrimaryBase()   { return 0; /* use order fields */ }
    private double calculateSecondaryBase() { return primaryBase * 0.5; }
    private double calculateTertiary()      { return (primaryBase + secondaryBase) * 0.1; }
}
```

---

## 9. Substitute Algorithm

**Problem:** You want to replace an existing algorithm with a clearer or more efficient one.
**Solution:** Rewrite the method body with the new algorithm. Keep the signature identical; tests will verify behavioural equivalence.

```java
// BEFORE — manual iteration to find a name in a list
String findPerson(String[] people) {
    for (String person : people) {
        if (person.equals("Don"))  return "Don";
        if (person.equals("John")) return "John";
        if (person.equals("Kent")) return "Kent";
    }
    return "";
}

// AFTER — cleaner algorithm using a Set lookup
String findPerson(String[] people) {
    Set<String> candidates = Set.of("Don", "John", "Kent");
    return Arrays.stream(people)
            .filter(candidates::contains)
            .findFirst()
            .orElse("");
}
```

---

## See Also
- [ComposingMethods.java](./ComposingMethods.java) — compilable before/after Java examples for all nine techniques
