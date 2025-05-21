package com.Buildex.form;

import com.Buildex.api.ApiClient;
import com.Buildex.model.User;
import com.Buildex.swing.ScrollBar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Form_Users extends JPanel {
    private final ApiClient apiClient;
    private com.Buildex.swing.Table table;
    private DefaultTableModel tableModel;

    public Form_Users(ApiClient apiClient) {
        this.apiClient = apiClient;
        initComponents();
        initData();
    }

    private void initData() {
        try {
            List<User> users = apiClient.getAllUsers();
            // Clear existing rows
            tableModel.setRowCount(0);
            for (User user : users) {
                tableModel.addRow(new Object[]{
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching users: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        panelBorder1 = new com.Buildex.swing.PanelBorder();
        jLabel1 = new JLabel();
        spTable = new JScrollPane();
        table = new com.Buildex.swing.Table();
        btnAdd = new JButton();
        btnDelete = new JButton();

        setBackground(new Color(242, 242, 242));

        panelBorder1.setBackground(Color.WHITE);

        jLabel1.setFont(new Font("sansserif", Font.BOLD, 18));
        jLabel1.setForeground(new Color(127, 127, 127));
        jLabel1.setText("User Management");

        spTable.setBorder(null);
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Name", "Email", "Role"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        table.setModel(tableModel);
        spTable.setViewportView(table);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        btnAdd.setText("Add User");
        btnAdd.addActionListener(e -> addUser());

        btnDelete.setText("Delete Selected");
        btnDelete.addActionListener(e -> deleteUser());

        GroupLayout panelBorder1Layout = new GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
                panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(spTable)
                                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                                .addComponent(btnAdd)
                                                .addGap(10, 10, 10)
                                                .addComponent(btnDelete)))
                                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
                panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spTable, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAdd)
                                        .addComponent(btnDelete))
                                .addGap(20, 20, 20))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(panelBorder1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(panelBorder1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );
    }

    private void addUser() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField roleField = new JTextField();

        Object[] fields = {
                "Name:", nameField,
                "Email:", emailField,
                "Password:", passwordField,
                "Role:", roleField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                User user = new User();
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setPassword(passwordField.getText());
                user.setRole(roleField.getText());

                apiClient.createUser(user);
                initData(); // Refresh table
                JOptionPane.showMessageDialog(this, "User added successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) table.getValueAt(selectedRow, 0);
            try {
                apiClient.deleteUser(id);
                initData(); // Refresh table
                table.repaint(); // Ensure UI updates
                table.revalidate();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }


    private JButton btnAdd;
    private JButton btnDelete;
    private JLabel jLabel1;
    private com.Buildex.swing.PanelBorder panelBorder1;
    private JScrollPane spTable;

}