package com.Buildex.auth;

import com.Buildex.api.ApiClient;
import com.Buildex.main.Main;
import com.Buildex.model.User;
import com.Buildex.user.UserDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginSignUpForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton signupButton;
    private final ApiClient apiClient;

    public LoginSignUpForm(ApiClient apiClient) {
        this.apiClient = apiClient;
        setTitle("Car Rental - Login/Signup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        // Main panel (equivalent to jPanel1 in friend's code)
        JPanel jPanel1 = new JPanel();
        jPanel1.setBackground(new Color(51, 51, 51));
        jPanel1.setLayout(new BorderLayout());

        // Inner panel for form (equivalent to jPanel2 in friend's code)
        JPanel jPanel2 = new JPanel();
        jPanel2.setBackground(new Color(102, 102, 102));
        jPanel2.setBorder(BorderFactory.createCompoundBorder());
        jPanel2.setLayout(new GridLayout(5, 2, 10, 10));
        jPanel2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanel2.setPreferredSize(new Dimension(400, 250)); // Increased width to 400px

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        emailLabel.setForeground(new Color(255, 255, 255));
        emailField = new JTextField();
        emailField.setBackground(new Color(102, 102, 102));
        emailField.setForeground(new Color(255, 255, 255));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(255, 255, 255));
        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(102, 102, 102));
        passwordField.setForeground(new Color(255, 255, 255));

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(102, 102, 102));
        loginButton.setForeground(new Color(255, 255, 255));
        loginButton.setFocusPainted(false); // Remove focus border
        loginButton.setBorderPainted(false); // Remove button border
        loginButton.addActionListener(this::login);

        signupButton = new JButton("Signup");
        signupButton.setBackground(new Color(102, 102, 102));
        signupButton.setForeground(new Color(255, 255, 255));
        signupButton.setFocusPainted(false); // Remove focus border
        signupButton.setBorderPainted(false); // Remove button border
        signupButton.addActionListener(this::signup);

        jPanel2.add(emailLabel);
        jPanel2.add(emailField);
        jPanel2.add(passwordLabel);
        jPanel2.add(passwordField);
        jPanel2.add(new JLabel()); // Empty space
        jPanel2.add(loginButton);
        jPanel2.add(new JLabel()); // Empty space
        jPanel2.add(signupButton);

        // Center the inner panel in the main panel
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setBackground(new Color(51, 51, 51));
        wrapperPanel.setLayout(new GridBagLayout());
        wrapperPanel.add(jPanel2);

        jPanel1.add(wrapperPanel, BorderLayout.CENTER);

        add(jPanel1);

        // Set Nimbus Look and Feel like friend's code
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void login(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email and password are required.");
            return;
        }

        try {
            User user = apiClient.login(email, password);
            if (user != null) {
                System.out.println("Calling setCredentials with email: " + email);
                apiClient.setCredentials(email, password);
                dispose();
                if ("ADMIN".equals(user.getRole())) {
                    new Main(apiClient).setVisible(true);
                } else {
                    new UserDashboard(user, apiClient).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Could not connect to the server. Please ensure the backend is running.";
            JOptionPane.showMessageDialog(this, "Error during login: " + errorMessage);
        }
    }

    private void signup(ActionEvent e) {
        JDialog signupDialog = new JDialog(this, "Signup", true);
        signupDialog.setSize(500, 400);
        signupDialog.setLocationRelativeTo(this);
        signupDialog.setLayout(new BorderLayout());

        // Main panel for the dialog
        JPanel dialogPanel = new JPanel();
        dialogPanel.setBackground(new Color(80, 80, 80));
        dialogPanel.setLayout(new GridLayout(4, 2, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField nameField = new JTextField();
        JTextField signupEmailField = new JTextField();
        JPasswordField signupPasswordField = new JPasswordField();

        // Apply theme to signup dialog fields
        nameField.setBackground(new Color(102, 102, 102));
        nameField.setForeground(new Color(255, 255, 255));
        signupEmailField.setBackground(new Color(102, 102, 102));
        signupEmailField.setForeground(new Color(255, 255, 255));
        signupPasswordField.setBackground(new Color(102, 102, 102));
        signupPasswordField.setForeground(new Color(255, 255, 255));

        Object[] fields = {
                "Name:", nameField,
                "Email:", signupEmailField,
                "Password:", signupPasswordField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Signup", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                User user = new User();
                user.setName(nameField.getText().trim());
                user.setEmail(signupEmailField.getText().trim());
                user.setPassword(new String(signupPasswordField.getPassword()).trim());
                user.setRole("USER");

                User createdUser = apiClient.createUser(user);
                if (createdUser != null) {
                    JOptionPane.showMessageDialog(this, "Signup successful! Please login.");
                } else {
                    JOptionPane.showMessageDialog(this, "Signup failed. Email may already exist.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during signup: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginSignUpForm(new ApiClient()).setVisible(true));
    }
}