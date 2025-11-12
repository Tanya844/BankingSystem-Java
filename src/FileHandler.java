import java.io.*;
import java.util.*;
import javafx.collections.ObservableList;

public class FileHandler {

    private static final String FILE_PATH = "data/accounts.csv";

    // Load accounts from CSV
    public static void loadAccounts(ObservableList<Account> accounts) {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String accNo = parts[0];
                    String name = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    accounts.add(new Account(accNo, name, balance));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    // Save accounts to CSV
    public static void saveAccounts(ObservableList<Account> accounts) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            bw.write("AccountNumber,Name,Balance\n");
            for (Account acc : accounts) {
                bw.write(acc.getAccountNumber() + "," + acc.getName() + "," + acc.getBalance() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }
}
