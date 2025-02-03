package BankingManagementSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class AccountManager extends JFrame {

    private Connection connection;
    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextField securityPinField;
    private JTextArea resultArea;

    public AccountManager(Connection connection) {
        this.connection = connection;

        initializeComponents();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setVisible(true);
    }

    private void initializeComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        accountNumberField = new JTextField(10);
        amountField = new JTextField(10);
        securityPinField = new JTextField(10);
        resultArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JButton creditButton = new JButton("Credit Money");
        JButton debitButton = new JButton("Debit Money");
        JButton transferButton = new JButton("Transfer Money");
        JButton balanceButton = new JButton("Check Balance");

        panel.add(new JLabel("Account Number:"));
        panel.add(accountNumberField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Security Pin:"));
        panel.add(securityPinField);
        panel.add(creditButton);
        panel.add(debitButton);
        panel.add(transferButton);
        panel.add(balanceButton);
        panel.add(scrollPane);

        add(panel);

        creditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                creditMoney();
            }
        });

        debitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                debitMoney();
            }
        });

        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferMoney();
            }
        });

        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBalance();
            }
        });
    }

    private void creditMoney() {
        try {
            long accountNumber = Long.parseLong(accountNumberField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String securityPin = securityPinField.getText();

            AccountManager accountManager = new AccountManager(connection);
            accountManager.credit_money(accountNumber, amount, securityPin);
            resultArea.setText("Rs." + amount + " credited Successfully");
        } catch (NumberFormatException | SQLException ex) {
            resultArea.setText("Invalid input or Transaction Failed!");
        }
    }

    private void credit_money(long accountNumber, double amount, String securityPin) {
    try {
        connection.setAutoCommit(false);

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
        preparedStatement.setLong(1, accountNumber);
        preparedStatement.setString(2, securityPin);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
            preparedStatement1.setDouble(1, amount);
            preparedStatement1.setLong(2, accountNumber);

            int rowsAffected = preparedStatement1.executeUpdate();

            if (rowsAffected > 0) {
                resultArea.setText("Rs." + amount + " credited Successfully");
                connection.commit();
            } else {
                resultArea.setText("Transaction Failed!");
                connection.rollback();
            }
        } else {
            resultArea.setText("Invalid Security Pin!");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


    private void debitMoney() {
        try {
            long accountNumber = Long.parseLong(accountNumberField.getText());
            double amount = Double.parseDouble(amountField.getText());
            String securityPin = securityPinField.getText();

            AccountManager accountManager = new AccountManager(connection);
            accountManager.debit_money(accountNumber, amount, securityPin);
            resultArea.setText("Rs." + amount + " debited Successfully");
        } catch (NumberFormatException | SQLException ex) {
            resultArea.setText("Invalid input or Transaction Failed!");
        }
    }

    private void debit_money(long accountNumber, double amount, String securityPin) {
        try {
            connection.setAutoCommit(false);
    
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
    
                if (amount <= current_balance) {
                    String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, accountNumber);
    
                    int rowsAffected = preparedStatement1.executeUpdate();
    
                    if (rowsAffected > 0) {
                        resultArea.setText("Rs." + amount + " debited Successfully");
                        connection.commit();
                    } else {
                        resultArea.setText("Transaction Failed!");
                        connection.rollback();
                    }
                } else {
                    resultArea.setText("Insufficient Balance!");
                }
            } else {
                resultArea.setText("Invalid Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    

    private void transferMoney() throws SQLException {
        try {
            long senderAccountNumber = Long.parseLong(accountNumberField.getText());
            long receiverAccountNumber = Long.parseLong(JOptionPane.showInputDialog("Enter Receiver's Account Number:"));
            double amount = Double.parseDouble(amountField.getText());
            String securityPin = securityPinField.getText();

            AccountManager accountManager = new AccountManager(connection);
            accountManager.transfer_money(senderAccountNumber, receiverAccountNumber, amount, securityPin);
            resultArea.setText("Rs." + amount + " Transferred Successfully");
        } catch (NumberFormatException ex) {
            resultArea.setText("Invalid input or Transaction Failed!");
        }
    }

    private void transfer_money(long senderAccountNumber, long receiverAccountNumber, double amount, String securityPin) {
        try {
            connection.setAutoCommit(false);
    
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, senderAccountNumber);
            preparedStatement.setString(2, securityPin);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                double current_balance = resultSet.getDouble("balance");
    
                if (amount <= current_balance) {
                    String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                    String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
    
                    PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                    PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
    
                    creditPreparedStatement.setDouble(1, amount);
                    creditPreparedStatement.setLong(2, receiverAccountNumber);
                    debitPreparedStatement.setDouble(1, amount);
                    debitPreparedStatement.setLong(2, senderAccountNumber);
    
                    int rowsAffected1 = debitPreparedStatement.executeUpdate();
                    int rowsAffected2 = creditPreparedStatement.executeUpdate();
    
                    if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                        resultArea.setText("Transaction Successful!\nRs." + amount + " Transferred Successfully");
                        connection.commit();
                    } else {
                        resultArea.setText("Transaction Failed");
                        connection.rollback();
                    }
                } else {
                    resultArea.setText("Insufficient Balance!");
                }
            } else {
                resultArea.setText("Invalid Security Pin!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkBalance() {
        try {
            long accountNumber = Long.parseLong(accountNumberField.getText());
            String securityPin = securityPinField.getText();

            AccountManager accountManager = new AccountManager(connection);
            double balance = accountManager.getBalance(accountNumber, securityPin);
            resultArea.setText("Balance: Rs." + balance);
        } catch (NumberFormatException | SQLException ex) {
            resultArea.setText("Invalid input or Transaction Failed!");
        }
    }

    private double getBalance(long accountNumber, String securityPin) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?");
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);
    
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                resultArea.setText("Invalid Pin!");
                return 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_system", "root", "techsavvy123");
            SwingUtilities.invokeLater(() -> new AccountManager(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
