import java.sql.*;
import java.util.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // if you have a password, put it here

    // Connect to MySQL
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // ✅ load MySQL driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Save new account
    public static void saveAccount(Account acc) {
        String query = "INSERT INTO accounts (account_number, name, balance) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, acc.getAccountNumber());
            ps.setString(2, acc.getName());
            ps.setDouble(3, acc.getBalance());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load all accounts
    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Account acc = new Account(rs.getString("name"), rs.getDouble("balance"));
                java.lang.reflect.Field field = Account.class.getDeclaredField("accountNumber");
                field.setAccessible(true);
                field.set(acc, rs.getString("account_number"));
                accounts.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    // Update account balance
    public static void updateAccount(Account acc) {
        String query = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDouble(1, acc.getBalance());
            ps.setString(2, acc.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Log transaction
    public static void logTransaction(Transaction txn) {
        String query = "INSERT INTO transactions (account_number, type, amount, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, txn.getAccountNumber());
            ps.setString(2, txn.getType());
            ps.setDouble(3, txn.getAmount());
            ps.setTimestamp(4, Timestamp.valueOf(txn.getTimestamp()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
