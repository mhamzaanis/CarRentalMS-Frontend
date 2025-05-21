package com.Buildex.form;

import com.Buildex.api.ApiClient;
import com.Buildex.component.Card;
import com.Buildex.model.Booking;
import com.Buildex.model.Car;
import com.Buildex.model.StatusType;
import com.Buildex.swing.ScrollBar;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Form_Home extends JPanel {
    private ApiClient apiClient;
    private Card card4;
    private Card card1;
    private Card card2;
    private Card card3;
    private JLabel jLabel1;
    private JLayeredPane panel;
    private com.Buildex.swing.PanelBorder panelBorder1;
    private JScrollPane spTable;
    private com.Buildex.swing.Table table;

    public Form_Home(ApiClient apiClient) {
        this.apiClient = apiClient;
        System.out.println("Form_Home using ApiClient instance: " + apiClient.getInstanceId());
        initComponents();
        initData();
    }

    private void initData() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog loadingDialog;
        if (parentWindow != null) {
            loadingDialog = new JDialog((Frame) parentWindow, "Loading", true);
        } else {
            loadingDialog = new JDialog((Window) null, "Loading");
            loadingDialog.setModal(false);
        }
        loadingDialog.setSize(200, 100);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.add(new JLabel("Loading data...", SwingConstants.CENTER));

        new Thread(() -> {
            try {
                List<Car> allCars = apiClient.getAllCars();
                List<Car> availableCars = apiClient.getAvailableCars();
                List<Booking> bookings = apiClient.getAllBookings();

                SwingUtilities.invokeLater(() -> {
                    card1.setData(new com.Buildex.model.Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/stock.png")),
                            "Total Cars",
                            String.valueOf(allCars.size()),
                            "All vehicles in inventory"
                    ));
                    card2.setData(new com.Buildex.model.Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/profit.png")),
                            "Available Cars",
                            String.valueOf(availableCars.size()),
                            "Cars ready for booking"
                    ));
                    card3.setData(new com.Buildex.model.Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/flag.png")),
                            "Total Bookings",
                            String.valueOf(bookings.size()),
                            "All rental bookings"
                    ));

                    double totalRevenue = bookings.stream()
                            .filter(Booking::isPaid)
                            .mapToDouble(Booking::getTotalAmount)
                            .sum();
                    card4.setData(new com.Buildex.model.Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/profit.png")),
                            "Total Revenue",
                            String.format("$%.2f", totalRevenue),
                            "Revenue from paid bookings"
                    ));

                    Map<Car, Long> carBookingCounts = bookings.stream()
                            .collect(Collectors.groupingBy(Booking::getCar, Collectors.counting()));
                    Car mostBookedCar = carBookingCounts.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse(null);

                    for (Booking booking : bookings) {
                        String userName = (booking.getUser() != null) ? booking.getUser().getName() : "Unknown";
                        String carPlate = (booking.getCar() != null) ? booking.getCar().getNoPlate() : "Unknown";
                        String startDate = (booking.getStartDate() != null) ? booking.getStartDate().toString() : "N/A";
                        String endDate = (booking.getEndDate() != null) ? booking.getEndDate().toString() : "N/A";
                        boolean isMostBooked = mostBookedCar != null && booking.getCar() != null && booking.getCar().getId().equals(mostBookedCar.getId());

                        table.addRow(new Object[]{
                                userName,
                                carPlate,
                                startDate,
                                endDate,
                                booking.isPaid() ? StatusType.PAID : StatusType.PENDING,
                                isMostBooked ? "★ Most Booked" : ""
                        });
                    }
                    loadingDialog.dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage());
                    loadingDialog.dispose();
                });
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        panel = new JLayeredPane();
        card1 = new Card();
        card2 = new Card();
        card3 = new Card();
        card4 = new Card();
        panelBorder1 = new com.Buildex.swing.PanelBorder();
        jLabel1 = new JLabel();
        spTable = new JScrollPane();
        table = new com.Buildex.swing.Table();

        setBackground(new Color(21, 19, 52)); // Updated to match LoginForm
        setOpaque(true);

        panel.setLayout(new GridLayout(1, 4, 10, 0));
        panel.setBackground(new Color(21, 19, 52)); // Updated to match LoginForm
        panel.setOpaque(true);

        card1.setColor1(new Color(21, 19, 52)); // Updated to match LoginForm
        card1.setColor2(new Color(81, 79, 112)); // Updated to match LoginForm
        panel.add(card1);

        card2.setColor1(new Color(21, 19, 52));
        card2.setColor2(new Color(81, 79, 112));
        panel.add(card2);

        card3.setColor1(new Color(21, 19, 52));
        card3.setColor2(new Color(81, 79, 112));
        panel.add(card3);

        card4.setColor1(new Color(21, 19, 52));
        card4.setColor2(new Color(81, 79, 112));
        panel.add(card4);

        panelBorder1.setBackground(new Color(81, 79, 112)); // Updated to match LoginForm

        jLabel1.setFont(new Font("sansserif", Font.BOLD, 18));
        jLabel1.setForeground(Color.WHITE); // Updated to match LoginForm
        jLabel1.setText("Recent Bookings");

        spTable.setBorder(null);
        table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"User", "Car Plate", "Start Date", "End Date", "Status", "Highlight"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        spTable.setViewportView(table);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(new Color(81, 79, 112)); // Updated to match LoginForm
        spTable.getViewport().setBackground(new Color(81, 79, 112)); // Updated to match LoginForm
        JPanel p = new JPanel();
        p.setBackground(new Color(81, 79, 112)); // Updated to match LoginForm
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        GroupLayout panelBorder1Layout = new GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
                panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(spTable))
                                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
                panelBorder1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorder1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spTable, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(panelBorder1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE))
                                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(panelBorder1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );
    }
}