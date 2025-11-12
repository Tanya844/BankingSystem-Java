import java.sql.*;
import java.util.*;

public class Bank {
    private List<Account> accounts;

    public Bank() {
        accounts = DatabaseHandler.loadAccounts();
    }

    public void createAccount(String name, double initialDeposit) {
        Account acc = new Account(name, initialDeposit);
        accounts.add(acc);
        DatabaseHandler.saveAccount(acc);
        System.out.println("✅ Account created successfully. Account No: " + acc.getAccountNumber());
    }

    public Account findAccount(String accNo) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber().equals(accNo)) {
                return acc;
            }
        }
        return null;
    }

    public void deposit(String accNo, double amount) {
        Account acc = findAccount(accNo);
        if (acc != null) {
            acc.deposit(amount);
            DatabaseHandler.updateAccount(acc);
            DatabaseHandler.logTransaction(new Transaction(accNo, "Deposit", amount));
            System.out.println("💰 Deposit successful! New Balance: " + acc.getBalance());
        } else {
            System.out.println("❌ Account not found!");
        }
    }

    public void withdraw(String accNo, double amount) {
        Account acc = findAccount(accNo);
        if (acc != null) {
            if (acc.withdraw(amount)) {
                DatabaseHandler.updateAccount(acc);
                DatabaseHandler.logTransaction(new Transaction(accNo, "Withdraw", amount));
                System.out.println("✅ Withdrawal successful! New Balance: " + acc.getBalance());
            } else {
                System.out.println("❌ Insufficient balance!");
            }
        } else {
            System.out.println("❌ Account not found!");
        }
    }

    public void checkBalance(String accNo) {
        Account acc = findAccount(accNo);
        if (acc != null) {
            System.out.println("📊 Current Balance: " + acc.getBalance());
        } else {
            System.out.println("❌ Account not found!");
        }
    }

    public void viewTransactions(String accNo) {
        String query = "SELECT * FROM transactions WHERE account_number = ?";
        try (Connection con = DatabaseHandler.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, accNo);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n📜 Transaction History for Account: " + accNo);
            System.out.println("--------------------------------------------------");
            System.out.printf("%-15s %-10s %-10s %-20s\n", "Account", "Type", "Amount", "Timestamp");
            System.out.println("--------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-15s %-10s %-10.2f %-20s\n",
                        rs.getString("account_number"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("timestamp").toString());
            }
            System.out.println("--------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
