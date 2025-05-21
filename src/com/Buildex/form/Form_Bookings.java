package com.Buildex.form;

import com.Buildex.api.ApiClient;
import com.Buildex.model.Booking;
import com.Buildex.model.BookingModel;
import com.Buildex.swing.PanelBorder;
import com.Buildex.swing.ScrollBar;
import com.Buildex.swing.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class Form_Bookings extends JPanel {
    private ApiClient apiClient;

    public Form_Bookings(ApiClient apiClient) {
        this.apiClient = apiClient;
        initComponents();
        initData();
    }

    private void initData() {
        refreshBookingsTable();
        try {
            List<Booking> bookings = apiClient.getAllBookings();
            for (Booking booking : bookings) {
                table.addRow(new Object[]{
                        booking.getId(),
                        booking.getUser().getName(),
                        booking.getCar().getNoPlate(),
                        booking.getStartDate().toString(),
                        booking.getEndDate().toString(),
                        booking.isPaid() ? "Paid" : "Pending",
                        booking.getTotalAmount()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching bookings: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        panelBorder1 = new PanelBorder();
        jLabel1 = new JLabel();
        spTable = new JScrollPane();
        table = new Table();
        btnAdd = new JButton();
        btnDelete = new JButton();

        setBackground(new Color(242, 242, 242));

        panelBorder1.setBackground(Color.WHITE);

        jLabel1.setFont(new Font("sansserif", Font.BOLD, 18));
        jLabel1.setForeground(new Color(127, 127, 127));
        jLabel1.setText("Booking Management");

        spTable.setBorder(null);
        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "User", "Car Plate", "Start Date", "End Date", "Status", "Total Amount"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        spTable.setViewportView(table);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        btnAdd.setText("Add Booking");
        btnAdd.addActionListener(e -> addBooking());

        btnDelete.setText("Delete Selected");
        btnDelete.addActionListener(e -> deleteBooking());

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

    private void addBooking() {
        JTextField userIdField = new JTextField();
        JTextField carIdField = new JTextField();
        JTextField startDateField = new JTextField("YYYY-MM-DD");
        JTextField endDateField = new JTextField("YYYY-MM-DD");
        JCheckBox paidCheck = new JCheckBox("Paid");
        JTextField totalAmountField = new JTextField();

        Object[] fields = {
                "User ID:", userIdField,
                "Car ID:", carIdField,
                "Start Date (YYYY-MM-DD):", startDateField,
                "End Date (YYYY-MM-DD):", endDateField,
                "Paid:", paidCheck,
                "Total Amount:", totalAmountField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Booking", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                BookingModel booking = new BookingModel();
                booking.setUserId(Long.parseLong(userIdField.getText()));
                booking.setCarId(Long.parseLong(carIdField.getText()));
                booking.setStartDate(LocalDate.parse(startDateField.getText()));
                booking.setEndDate(LocalDate.parse(endDateField.getText()));
                booking.setPaid(paidCheck.isSelected());
                booking.setTotalAmount(Double.parseDouble(totalAmountField.getText()));

                apiClient.createBooking(booking);
                initData(); // Refresh table
                JOptionPane.showMessageDialog(this, "Booking added successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding booking: " + e.getMessage());
            }
        }
    }
    private void refreshBookingsTable() {
        new Thread(() -> {
            try {
                List<Booking> bookings = apiClient.getAllBookings();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                SwingUtilities.invokeLater(() -> {
                    model.setRowCount(0); // Clear existing rows
                    for (Booking booking : bookings) {
                        System.out.println("Adding booking to table: ID=" + booking.getId() + ", User=" + booking.getUser().getName() + ", Car Plate=" + booking.getCar().getNoPlate());
                        model.addRow(new Object[]{
                                booking.getId(),
                                booking.getUser().getName(),
                                booking.getCar().getNoPlate(),
                                booking.getStartDate().toString(),
                                booking.getEndDate().toString(),
                                booking.isPaid() ? "Paid" : "Pending",
                                booking.getTotalAmount()
                        });
                    }
                    table.repaint();
                    table.revalidate();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error refreshing bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void deleteBooking() {
        int selectedRow = table.getSelectedRow(); // Use the existing table field
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long bookingId = (Long) table.getValueAt(selectedRow, 0); // Cast ID to Long
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete booking ID " + bookingId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        new Thread(() -> {
            try {
                apiClient.deleteBooking(bookingId);
                // Update car availability if the booking is active
                Booking booking = apiClient.getBookingById(bookingId);
                if (booking != null && booking.getEndDate().isAfter(LocalDate.now())) {
                    apiClient.updateCarAvailability(booking.getCar().getId(), true);
                }
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Booking deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshBookingsTable();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting booking: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private JButton btnAdd;
    private JButton btnDelete;
    private JLabel jLabel1;
    private PanelBorder panelBorder1;
    private JScrollPane spTable;
    private Table table;
}