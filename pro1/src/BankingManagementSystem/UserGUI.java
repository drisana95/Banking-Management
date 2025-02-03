package BankingManagementSystem;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UserGUI extends JPanel {
    private Connection connection;
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public UserGUI() {
        this.connection = connection;
        setLayout(new GridLayout(5, 2));
        fullNameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton registerButton = new JButton("Register");
        messageLabel = new JLabel("");

        add(new JLabel("Full Name: "));
        add(fullNameField);
        add(new JLabel("Email: "));
        add(emailField);
        add(new JLabel("Password: "));
        add(passwordField);
        add(new JLabel(""));  // Empty label for spacing
        add(registerButton);
        add(new JLabel(""));  // Empty label for spacing
        add(messageLabel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    public UserGUI() {
    }

    public UserGUI(Object object) {
    }

    private void registerUser() {
        String full_name = fullNameField.getText();
        String email = emailField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (userExist(email)) {
            messageLabel.setText("User Already Exists for this Email Address!!");
            messageLabel.setForeground(Color.RED);
        } else {
            String registerQuery = "INSERT INTO User(full_name, email, password) VALUES(?, ?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
                preparedStatement.setString(1, full_name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    messageLabel.setText("Registration Successful!");
                    messageLabel.setForeground(Color.GREEN);
                } else {
                    messageLabel.setText("Registration Failed!");
                    messageLabel.setForeground(Color.RED);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean userExist(String email) {
        String query = "SELECT * FROM User WHERE email = ?";
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("User Registration");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 200);
                frame.add(new UserGUI(null));
                frame.setVisible(true);
            }
        });
    }

    public boolean login() {
        return false;
    }

    public boolean user_exist(String email) {
        return false;
    }

    public void register(String fullName, String email, String password) {
    }

    public boolean login() {
        return false;
    }

    public boolean login(String email, String string) {
        return false;
    }
}