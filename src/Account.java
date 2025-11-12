import javafx.beans.property.*;

public class Account {
    private final StringProperty accountNumber;
    private final StringProperty name;
    private final DoubleProperty balance;

    public Account(String accountNumber, String name, double balance) {
        this.accountNumber = new SimpleStringProperty(accountNumber);
        this.name = new SimpleStringProperty(name);
        this.balance = new SimpleDoubleProperty(balance);
    }

    // Convenience constructor (used when account number is auto-generated)
    public Account(String name, double balance) {
        this("ACC" + System.currentTimeMillis(), name, balance);
    }

    // Deposit money
    public void deposit(double amount) {
        setBalance(getBalance() + amount);
    }

    // Withdraw money (returns true if successful)
    public boolean withdraw(double amount) {
        if (amount <= getBalance()) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }

    // Getters and property accessors
    public String getAccountNumber() { return accountNumber.get(); }
    public StringProperty accountNumberProperty() { return accountNumber; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public double getBalance() { return balance.get(); }
    public DoubleProperty balanceProperty() { return balance; }

    public void setBalance(double balance) { this.balance.set(balance); }
}
