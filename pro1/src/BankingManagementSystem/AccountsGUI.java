package BankingManagementSystem;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AccountsGUI {
    private Connection connection;
    private JFrame frame;
    private JTextField emailField;
    private JTextField fullNameField;
    private JTextField initialAmountField;
    private JTextField securityPinField;

    public AccountsGUI(Connection connection) {
        this.connection = connection;

        frame = new JFrame("Account Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 1));

        createUI();

        frame.setVisible(true);
    }

    private void createUI() {
        emailField = new JTextField();
        fullNameField = new JTextField();
        initialAmountField = new JTextField();
        securityPinField = new JTextField();

        JButton openAccountButton = new JButton("Open Account");

        frame.add(new JLabel("Email:"));
        frame.add(emailField);
        frame.add(new JLabel("Full Name:"));
        frame.add(fullNameField);
        frame.add(new JLabel("Initial Amount:"));
        frame.add(initialAmountField);
        frame.add(new JLabel("Security Pin:"));
        frame.add(securityPinField);
        frame.add(openAccountButton);

        openAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAccount();
            }
        });
    }

    private void openAccount() {
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        double initialAmount = Double.parseDouble(initialAmountField.getText());
        String securityPin = securityPinField.getText();

        if (!accountExists(email)) {
            String openAccountQuery = "INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VALUES(?, ?, ?, ?, ?)";
            try {
                long accountNumber = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(openAccountQuery);
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, fullName);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, initialAmount);
                preparedStatement.setString(5, securityPin);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Account Created Successfully\nAccount Number: " + accountNumber);
                } else {
                    JOptionPane.showMessageDialog(frame, "Account Creation Failed");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Account Already Exists");
        }
    }

    private long generateAccountNumber() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(account_number) FROM Accounts");
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long lastAccountNumber = resultSet.getLong(1);
                return lastAccountNumber + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 10000100;
    }

    private boolean accountExists(String email) {
        String query = "SELECT account_number FROM Accounts WHERE email = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_system", "root", "techsavvy123");
                new AccountsGUI(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public long getAccountNumber(String email) {
        return 0;
    }

    public long open_account(String email) {
        return 0;
    }

    public void debit_money(long accountNumber, double amount, String securityPin) {
    }

    public void credit_money(long accountNumber, double amount, String securityPin) {
    }

    public void transfer_money(long accountNumber, long receiverAccountNumber, double amount, String securityPin) {
    }

    public void getBalance(long accountNumber, String securityPin) {
    }

    public boolean deposit(double amount) {
        return false;
    }

    public boolean withdraw(double amount) {
        return false;
    }

    public double checkBalance() {
        return 0;
    }
}

