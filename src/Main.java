import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.*;

public class Main extends Application {
    private final TableView<Account> table = new TableView<>();
    private final ObservableList<Account> accounts = FXCollections.observableArrayList();

    private static final String FILE_PATH = "data/accounts.csv";

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage stage) {
        stage.setTitle("🏦 Banking System");

        // --- Load existing data ---
        loadAccountsFromFile();

        // --- Table columns ---
        TableColumn<Account, String> colNumber = new TableColumn<>("Account No");
        colNumber.setCellValueFactory(data -> data.getValue().accountNumberProperty());

        TableColumn<Account, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            Account acc = event.getRowValue();
            acc.nameProperty().set(event.getNewValue());
        });

        TableColumn<Account, Double> colBalance = new TableColumn<>("Balance");
        colBalance.setCellValueFactory(data -> data.getValue().balanceProperty().asObject());
        colBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colBalance.setOnEditCommit(event -> {
            Account acc = event.getRowValue();
            acc.setBalance(event.getNewValue());
        });

        table.getColumns().addAll(colNumber, colName, colBalance);
        table.setItems(accounts);
        table.setEditable(true);

        // --- Buttons ---
        Button addBtn = new Button("➕ Add Account");
        Button depBtn = new Button("💸 Deposit");
        Button withBtn = new Button("💰 Withdraw");
        Button saveBtn = new Button("💾 Save");

        addBtn.setOnAction(e -> addAccount());
        depBtn.setOnAction(e -> deposit());
        withBtn.setOnAction(e -> withdraw());
        saveBtn.setOnAction(e -> saveAccountsToFile());

        HBox controls = new HBox(10, addBtn, depBtn, withBtn, saveBtn);
        controls.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(controls);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    // --- Add new account ---
    private void addAccount() {
        String accNo = "100" + (accounts.size() + 1);
        accounts.add(new Account(accNo, "New User", 0.0));
    }

    // --- Deposit ---
    private void deposit() {
        Account selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Please select an account first!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit");
        dialog.setHeaderText("Enter the amount to deposit:");
        dialog.setContentText("Amount:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) throw new NumberFormatException();
                selected.setBalance(selected.getBalance() + amount);
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Please enter a valid positive number!");
            }
        });
    }

    // --- Withdraw ---
    private void withdraw() {
        Account selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Please select an account first!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdraw");
        dialog.setHeaderText("Enter the amount to withdraw:");
        dialog.setContentText("Amount:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double amount = Double.parseDouble(input);
                if (amount <= 0) throw new NumberFormatException();
                if (selected.getBalance() >= amount) {
                    selected.setBalance(selected.getBalance() - amount);
                } else {
                    showAlert(AlertType.ERROR, "Insufficient funds!");
                }
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Please enter a valid positive number!");
            }
        });
    }

    // --- Save accounts to CSV file ---
    private void saveAccountsToFile() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // Ensure data folder exists

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("AccountNumber,Name,Balance\n");
            for (Account acc : accounts) {
                bw.write(acc.getAccountNumber() + "," + acc.getName() + "," + acc.getBalance() + "\n");
            }
            showAlert(AlertType.INFORMATION, "Accounts saved successfully!");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error saving accounts: " + e.getMessage());
        }
    }

    // --- Load accounts from CSV file ---
    private void loadAccountsFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // skip header
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
            showAlert(AlertType.ERROR, "Error loading accounts: " + e.getMessage());
        }
    }

    // --- Utility alert method ---
    private void showAlert(AlertType type, String message) {
        new Alert(type, message).showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
