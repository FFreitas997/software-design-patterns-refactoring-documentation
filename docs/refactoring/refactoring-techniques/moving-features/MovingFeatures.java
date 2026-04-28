import java.util.Calendar;
import java.util.Date;

/**
 * MovingFeatures.java
 *
 * Before/after Java examples for all eight Moving Features Between Objects
 * refactoring techniques:
 *
 *   1. Move Method
 *   2. Move Field
 *   3. Extract Class
 *   4. Inline Class
 *   5. Hide Delegate
 *   6. Remove Middle Man
 *   7. Introduce Foreign Method
 *   8. Introduce Local Extension
 */
public class MovingFeatures {

    // =========================================================================
    // 1. MOVE METHOD
    // =========================================================================

    // BEFORE: overdraftCharge() lives in AccountBefore but uses AccountType data.
    static class AccountTypeMM {
        private final boolean premium;
        AccountTypeMM(boolean premium) { this.premium = premium; }
        boolean isPremium() { return premium; }
    }

    static class AccountBefore {
        AccountTypeMM type;
        int daysOverdrawn;

        AccountBefore(AccountTypeMM type, int daysOverdrawn) {
            this.type = type; this.daysOverdrawn = daysOverdrawn;
        }

        // Feature envy — uses type.isPremium() constantly
        double overdraftCharge() {
            if (type.isPremium()) return Math.max(10, daysOverdrawn * 2.5);
            return daysOverdrawn * 1.75;
        }
    }

    // AFTER: calculation moved to AccountType where the relevant data lives.
    static class AccountTypeAfterMM {
        private final boolean premium;
        AccountTypeAfterMM(boolean premium) { this.premium = premium; }
        boolean isPremium() { return premium; }

        double overdraftCharge(int daysOverdrawn) {
            return isPremium() ? Math.max(10, daysOverdrawn * 2.5) : daysOverdrawn * 1.75;
        }
    }

    static class AccountAfter {
        AccountTypeAfterMM type;
        int daysOverdrawn;

        AccountAfter(AccountTypeAfterMM type, int daysOverdrawn) {
            this.type = type; this.daysOverdrawn = daysOverdrawn;
        }

        double overdraftCharge() { return type.overdraftCharge(daysOverdrawn); }
    }

    // =========================================================================
    // 2. MOVE FIELD
    // =========================================================================

    // BEFORE: interestRate field lives in AccountMFBefore but belongs to the type.
    static class AccountMFBefore {
        String  accountType;
        double  interestRate;   // ← field to move
        AccountMFBefore(String accountType, double interestRate) {
            this.accountType  = accountType;
            this.interestRate = interestRate;
        }
    }

    // AFTER: interestRate lives in AccountTypeWithRate.
    static class AccountTypeWithRate {
        private final String  name;
        private final double  interestRate;
        AccountTypeWithRate(String name, double rate) { this.name = name; this.interestRate = rate; }
        double getInterestRate() { return interestRate; }
    }

    static class AccountMFAfter {
        AccountTypeWithRate type;
        AccountMFAfter(AccountTypeWithRate type) { this.type = type; }
        double getInterestRate() { return type.getInterestRate(); }
    }

    // =========================================================================
    // 3. EXTRACT CLASS
    // =========================================================================

    // BEFORE: Person holds personal details AND telephone details.
    static class PersonBefore {
        String name;
        String officeAreaCode, officeNumber;

        String telephoneNumber() { return "(" + officeAreaCode + ") " + officeNumber; }
    }

    // AFTER: telephone concern extracted into TelephoneNumber.
    static class TelephoneNumber {
        private final String areaCode, number;
        TelephoneNumber(String areaCode, String number) { this.areaCode = areaCode; this.number = number; }
        @Override public String toString() { return "(" + areaCode + ") " + number; }
    }

    static class PersonAfter {
        String          name;
        TelephoneNumber officeTelephone;

        PersonAfter(String name, TelephoneNumber phone) { this.name = name; this.officeTelephone = phone; }
        String telephoneNumber() { return officeTelephone.toString(); }
    }

    // =========================================================================
    // 4. INLINE CLASS (inverse of Extract Class)
    // =========================================================================

    // BEFORE: TelephoneNumberThin is so thin it adds no value.
    static class TelephoneNumberThin { String number; }
    static class PersonWithPhone     { TelephoneNumberThin phone; }

    // AFTER: absorbed into Person — no separate class needed.
    static class PersonInlined {
        String name;
        String phoneNumber; // formerly TelephoneNumberThin.number
        PersonInlined(String name, String phoneNumber) { this.name = name; this.phoneNumber = phoneNumber; }
    }

    // =========================================================================
    // 5. HIDE DELEGATE
    // =========================================================================

    static class Department {
        private final String managerName;
        Department(String managerName) { this.managerName = managerName; }
        String getManager() { return managerName; }
    }

    // BEFORE: client navigates person.getDepartment().getManager().
    static class PersonHDBefore {
        private final Department department;
        PersonHDBefore(Department d) { this.department = d; }
        Department getDepartment() { return department; } // exposes delegate
    }

    // AFTER: Person hides the chain behind getManager().
    static class PersonHDAfter {
        private final Department department;
        PersonHDAfter(Department d) { this.department = d; }
        String getManager() { return department.getManager(); } // delegate hidden
    }

    // =========================================================================
    // 6. REMOVE MIDDLE MAN
    // =========================================================================

    // BEFORE: PersonMMBefore does nothing but forward to Department.
    static class PersonMMBefore {
        private final Department department;
        PersonMMBefore(Department d) { this.department = d; }
        Department getDepartment() { return department; }
        String getManager() { return department.getManager(); } // pure forwarding
    }

    // AFTER: client calls getDepartment().getManager() directly.
    // PersonMMBefore.getManager() is deleted; clients updated to:
    //   person.getDepartment().getManager()
    // (no new class — the middle man method is just removed)

    // =========================================================================
    // 7. INTRODUCE FOREIGN METHOD
    // =========================================================================

    // BEFORE: date arithmetic inline at the call site (repeated in many places).
    @SuppressWarnings("deprecation")
    static Date nextDayBefore(Date date) {
        // Magic calculation scattered wherever a "next day" date is needed.
        return new Date(date.getYear(), date.getMonth(), date.getDate() + 1);
    }

    // AFTER: single static utility method — the "foreign method".
    @SuppressWarnings("deprecation")
    static Date nextDayForeignMethod(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate() + 1);
    }

    // =========================================================================
    // 8. INTRODUCE LOCAL EXTENSION
    // =========================================================================

    // A subclass that adds useful methods missing from java.util.Date.
    @SuppressWarnings("deprecation")
    static class MfDate extends Date {
        private static final long MILLIS_PER_DAY = 86_400_000L;

        MfDate() { super(); }
        MfDate(long time) { super(time); }

        MfDate nextDay() { return new MfDate(this.getTime() + MILLIS_PER_DAY); }

        boolean isWeekend() {
            Calendar c = Calendar.getInstance();
            c.setTime(this);
            int day = c.get(Calendar.DAY_OF_WEEK);
            return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
        }

        @Override public String toString() {
            return super.toString() + (isWeekend() ? " [weekend]" : " [weekday]");
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== 1. Move Method ===");
        AccountAfter account = new AccountAfter(new AccountTypeAfterMM(true), 8);
        System.out.printf("Overdraft charge: %.2f%n", account.overdraftCharge());

        System.out.println("\n=== 2. Move Field ===");
        AccountTypeWithRate at = new AccountTypeWithRate("SAVINGS", 0.03);
        System.out.printf("Interest rate: %.2f%%%n", new AccountMFAfter(at).getInterestRate() * 100);

        System.out.println("\n=== 3. Extract Class ===");
        PersonAfter person = new PersonAfter("Bob", new TelephoneNumber("415", "555-1234"));
        System.out.println(person.name + ": " + person.telephoneNumber());

        System.out.println("\n=== 4. Inline Class ===");
        PersonInlined pi = new PersonInlined("Carol", "555-9876");
        System.out.println(pi.name + ": " + pi.phoneNumber);

        System.out.println("\n=== 5. Hide Delegate ===");
        PersonHDAfter hdPerson = new PersonHDAfter(new Department("Alice"));
        System.out.println("Manager: " + hdPerson.getManager());

        System.out.println("\n=== 6. Remove Middle Man ===");
        PersonMMBefore mmPerson = new PersonMMBefore(new Department("Dave"));
        System.out.println("Manager (via dept): " + mmPerson.getDepartment().getManager());

        System.out.println("\n=== 7. Introduce Foreign Method ===");
        Date today = new Date();
        System.out.println("Today    : " + today);
        System.out.println("Next day : " + nextDayForeignMethod(today));

        System.out.println("\n=== 8. Introduce Local Extension ===");
        MfDate mfDate = new MfDate();
        System.out.println("Today        : " + mfDate);
        System.out.println("Tomorrow     : " + mfDate.nextDay());
    }
}
