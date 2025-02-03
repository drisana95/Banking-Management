package BankingManagementSystem;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class BankingAppGUI {
    private Connection connection;
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private UserGUI user;
    private AccountsGUI accounts;

    public BankingAppGUI(Connection connection) {
        this.connection = connection;

        frame = new JFrame("Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 1));

        initializeComponents();

        frame.setVisible(true);
    }

    private void initializeComponents() {
        user = new UserGUI();
        accounts = new AccountsGUI(connection);

        emailField = new JTextField();
        passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        frame.add(new JLabel("Email:"));
        frame.add(emailField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);
        frame.add(registerButton);
        frame.add(loginButton);
        frame.add(exitButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterDialog();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void openRegisterDialog() {
        // Implement a dialog for user registration
        JDialog registerDialog = new JDialog(frame, "Register User", true);
        registerDialog.setSize(300, 200);
        registerDialog.setLayout(new GridLayout(5, 2));

        JTextField fullNameField = new JTextField();
        JTextField registerEmailField = new JTextField();
        JPasswordField registerPasswordField = new JPasswordField();
        JButton registerUserButton = new JButton("Register");

        registerDialog.add(new JLabel("Full Name:"));
        registerDialog.add(fullNameField);
        registerDialog.add(new JLabel("Email:"));
        registerDialog.add(registerEmailField);
        registerDialog.add(new JLabel("Password:"));
        registerDialog.add(registerPasswordField);
        registerDialog.add(new JLabel(""));  // Empty label for spacing
        registerDialog.add(registerUserButton);

        registerUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Call the registration logic in UserGUI
                user.register(fullNameField.getText(), registerEmailField.getText(), new String(registerPasswordField.getPassword()));
                registerDialog.dispose();
            }
        });

        registerDialog.setVisible(true);
    }

    private void loginUser() {
        String email = emailField.getText();
        char[] passwordChars = passwordField.getPassword();
        if (user.login(email, new String(passwordChars))) {
            accounts.getAccountNumber(email);

            // Open the main banking screen
            openBankingScreen();
        } else {
            JOptionPane.showMessageDialog(frame, "Login failed. Please check your credentials.");
        }
    }

    private void openBankingScreen() {
        // Implement the banking operations UI
        JFrame bankingFrame = new JFrame("Banking Operations");
        bankingFrame.setSize(600, 400);
        bankingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add buttons and components for deposit, withdrawal, balance check, etc.
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton checkBalanceButton = new JButton("Check Balance");

        bankingFrame.setLayout(new GridLayout(3, 1));
        bankingFrame.add(depositButton);
        bankingFrame.add(withdrawButton);
        bankingFrame.add(checkBalanceButton);

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement deposit logic
                try {
                    // You might want to open a dialog for deposit amount
                    double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter the deposit amount:"));
        
                    // Call the deposit method in AccountsGUI
                    boolean depositSuccess = accounts.deposit(amount);
        
                    // Display a confirmation message
                    if (depositSuccess) {
                        JOptionPane.showMessageDialog(frame, "Deposit of Rs." + amount + " successful.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Deposit failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement withdrawal logic
                try {
                    // You might want to open a dialog for withdrawal amount
                    double amount = Double.parseDouble(JOptionPane.showInputDialog("Enter the withdrawal amount:"));
        
                    // Call the withdraw method in AccountsGUI
                    boolean withdrawalSuccess = accounts.withdraw(amount);
        
                    // Display a confirmation message or error message
                    if (withdrawalSuccess) {
                        JOptionPane.showMessageDialog(frame, "Withdrawal of Rs." + amount + " successful.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Insufficient balance for withdrawal.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid amount.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        checkBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement balance check logic
                double balance = accounts.checkBalance();
        
                // Display the balance in a message dialog
                JOptionPane.showMessageDialog(frame, "Your account balance is: Rs." + balance);
            }
        });
        

        bankingFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_system", "root", "techsavvy@123");
            SwingUtilities.invokeLater(() -> new BankingAppGUI(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
