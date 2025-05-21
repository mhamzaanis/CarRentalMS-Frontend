package com.Buildex.main;

import com.Buildex.api.ApiClient;
import com.Buildex.auth.LoginSignUpForm;
import com.Buildex.component.AdminDashboard;
import com.Buildex.component.Header;
import com.Buildex.event.EventMenuSelected;
import com.Buildex.form.Form_Bookings;
import com.Buildex.form.Form_Cars;
import com.Buildex.form.Form_Home;
import com.Buildex.form.Form_Users;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private final ApiClient apiClient;
    private final Form_Home home;
    private final Form_Users formUsers;
    private final Form_Cars formCars;
    private final Form_Bookings formBookings;

    public Main(ApiClient apiClient) {
        this.apiClient = apiClient;
        System.out.println("Main using ApiClient instance: " + apiClient.getInstanceId());
        setTitle("Car Rental Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);


        initComponents();
        setBackground(new Color(0, 0, 0, 0));
        home = new Form_Home(apiClient);
        formUsers = new Form_Users(apiClient);
        formCars = new Form_Cars(apiClient);
        formBookings = new Form_Bookings(apiClient);
        adminDashboard.initMoving(this);
        adminDashboard.addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(int index) {
                System.out.println("Menu item selected: " + index);
                if (index == 0) {
                    setForm(home);
                } else if (index == 1) {
                    setForm(formUsers);
                } else if (index == 2) {
                    setForm(formCars);
                } else if (index == 3) {
                    setForm(formBookings);
                } else if (index == 4) {
                    int confirm = JOptionPane.showConfirmDialog(Main.this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            apiClient.clearCredentials();
                            dispose();
                            new LoginSignUpForm(apiClient).setVisible(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(Main.this, "Error during logout: " + e.getMessage());
                        }
                    }
                }
            }
        });
        setForm(home);
    }

    private void setForm(JComponent com) {
        mainPanel.removeAll();
        mainPanel.add(com);
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        panelBorder1 = new com.Buildex.swing.PanelBorder();
        adminDashboard = new AdminDashboard(apiClient);
        header2 = new Header();
        mainPanel = new JPanel();



        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelBorder1.setBackground(new Color(21, 19, 52)); // Updated to match LoginForm
        mainPanel.setBackground(new Color(21, 19, 52)); // Updated to match LoginForm
        mainPanel.setOpaque(true); // Ensure background is applied
        mainPanel.setLayout(new BorderLayout());

        GroupLayout panelBorder1Layout = new GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
                panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addComponent(adminDashboard, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGroup(panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(header2, GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())))
        );
        panelBorder1Layout.setVerticalGroup(
                panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(adminDashboard, GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addComponent(header2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginSignUpForm(new ApiClient()).setVisible(true));

    }

    private Header header2;
    private JPanel mainPanel;
    private AdminDashboard adminDashboard;
    private com.Buildex.swing.PanelBorder panelBorder1;
}