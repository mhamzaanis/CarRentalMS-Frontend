package com.Buildex.user;

import com.Buildex.api.ApiClient;
import com.Buildex.auth.LoginSignUpForm;
import com.Buildex.component.AdminDashboard;
import com.Buildex.component.Card;
import com.Buildex.component.Header;
import com.Buildex.event.EventMenuSelected;
import com.Buildex.model.*;
import com.Buildex.swing.PanelBorder;
import com.Buildex.swing.ScrollBar;
import com.Buildex.swing.Table;
import com.Buildex.model.BookingModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDashboard extends JFrame {
    private final User currentUser;
    private JTabbedPane tabbedPane;
    private BookingModel bookingModel;
    private final ApiClient apiClient;
    private AdminDashboard adminDashboard;
    private Header header;
    private JPanel mainPanel;
    private PanelBorder panelBorder;
    private JPanel bookCarPanel;
    private JPanel myBookingsPanel;
    private JPanel paymentPanel;
    private JLayeredPane statsPanel;
    private Card totalBookingsCard;
    private Card pendingPaymentsCard;
    private Table myBookingsTable;
    private Table paymentTable;
    private Table availableCarsTable;

    public UserDashboard(User user, ApiClient apiClient) {
        this.currentUser = user;
        this.apiClient = apiClient;

        setTitle("User Dashboard - Welcome " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setUndecorated(true);

        initComponents();
        initStatsPanel();
        setupMenu();
        setForm(bookCarPanel);
    }

    private void initComponents() {
        List<Model_Menu> menuItems = new ArrayList<>();
        menuItems.add(new Model_Menu("1", "Book a Car", Model_Menu.MenuType.MENU));
        menuItems.add(new Model_Menu("2", "My Bookings", Model_Menu.MenuType.MENU));
        menuItems.add(new Model_Menu("3", "Make Payment", Model_Menu.MenuType.MENU));
        menuItems.add(new Model_Menu("", " ", Model_Menu.MenuType.EMPTY));
        menuItems.add(new Model_Menu("4", "Logout", Model_Menu.MenuType.MENU));

        panelBorder = new PanelBorder() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(21, 19, 52), 0, getHeight(), new Color(81, 79, 112));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        adminDashboard = new AdminDashboard(menuItems);
        header = new Header();
        mainPanel = new JPanel();
        statsPanel = new JLayeredPane();

        panelBorder.setBackground(new Color(21, 19, 52));
        mainPanel.setBackground(new Color(21, 19, 52));
        mainPanel.setOpaque(true);
        mainPanel.setLayout(new BorderLayout());

        bookCarPanel = createBookCarPanel();
        myBookingsPanel = createMyBookingsPanel();
        paymentPanel = createPaymentPanel();

        GroupLayout panelBorderLayout = new GroupLayout(panelBorder);
        panelBorder.setLayout(panelBorderLayout);
        panelBorderLayout.setHorizontalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addComponent(adminDashboard, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                                .addGroup(panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(header, GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
                                        .addGroup(panelBorderLayout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())))
        );
        panelBorderLayout.setVerticalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(adminDashboard, GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addComponent(header, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(panelBorder, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
        adminDashboard.initMoving(this);
    }

    private void initStatsPanel() {
        statsPanel.setLayout(new GridLayout(1, 2, 10, 0));
        statsPanel.setBackground(new Color(21, 19, 52));
        statsPanel.setOpaque(true);

        totalBookingsCard = new Card();
        totalBookingsCard.setColor1(new Color(21, 19, 52));
        totalBookingsCard.setColor2(new Color(81, 79, 112));
        pendingPaymentsCard = new Card();
        pendingPaymentsCard.setColor1(new Color(21, 19, 52));
        pendingPaymentsCard.setColor2(new Color(81, 79, 112));

        statsPanel.add(totalBookingsCard);
        statsPanel.add(pendingPaymentsCard);

        refreshStats();
    }

    private void refreshStats() {
        JDialog loadingDialog = createLoadingDialog();
        new Thread(() -> {
            try {
                List<Booking> bookings = apiClient.getBookingsByUserId(currentUser.getId());
                long pendingPaymentsCount = bookings.stream().filter(b -> !b.isPaid()).count();
                SwingUtilities.invokeLater(() -> {
                    totalBookingsCard.setData(new Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/flag.png")),
                            "Total Bookings",
                            String.valueOf(bookings.size()),
                            "Your total bookings"
                    ));
                    pendingPaymentsCard.setData(new Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/profit.png")),
                            "Pending Payments",
                            String.valueOf(pendingPaymentsCount),
                            "Bookings awaiting payment"
                    ));
                    loadingDialog.dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error fetching stats: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    loadingDialog.dispose();
                });
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    private void setupMenu() {
        adminDashboard.addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(int index) {
                System.out.println("Menu item selected: " + index);
                if (index == 0) {
                    bookCarPanel = createBookCarPanel();
                    setForm(bookCarPanel);
                } else if (index == 1) {
                    setForm(myBookingsPanel);
                } else if (index == 2) {
                    setForm(paymentPanel);
                } else if (index == 3) {
                    int confirm = JOptionPane.showConfirmDialog(UserDashboard.this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            System.out.println("Attempting to clear credentials...");
                            apiClient.clearCredentials();
                            System.out.println("Credentials cleared successfully.");
                            dispose();
                            System.out.println("UserDashboard disposed.");
                            SwingUtilities.invokeLater(() -> {
                                try {
                                    System.out.println("Creating new LoginForm...");
                                    new LoginSignUpForm(apiClient).setVisible(true);
                                    System.out.println("LoginForm displayed.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Error initializing LoginForm: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(UserDashboard.this, "Error during logout: " + e.getMessage(), "Logout Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void setForm(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel);
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    private void styleTable(Table table) {
        // Set table properties
        table.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Larger font for readability
        table.setRowHeight(30); // Taller rows for better visibility
        table.setForeground(Color.WHITE); // Bright white text for contrast
        table.setBackground(new Color(60, 58, 91)); // Slightly lighter background
        table.setGridColor(new Color(100, 98, 131)); // Light purple grid lines
        table.setShowGrid(true); // Show grid lines
        table.setSelectionBackground(new Color(100, 98, 131)); // Highlight selected row
        table.setSelectionForeground(Color.WHITE); // Keep text white when selected

        // Customize table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(new Color(81, 79, 112));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // Enable alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(60, 58, 91) : new Color(70, 68, 101)); // Alternating colors
                }
                return c;
            }
        });
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(21, 19, 52));

        PanelBorder panelBorder = new PanelBorder();
        panelBorder.setBackground(new Color(81, 79, 112));

        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JScrollPane spTable = new JScrollPane();
        myBookingsTable = new Table();
        myBookingsTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Car Plate", "Start Date", "End Date", "Total Amount", "Status"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        // Apply table styling
        styleTable(myBookingsTable);

        spTable.setViewportView(myBookingsTable);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(new Color(81, 79, 112));
        spTable.getViewport().setBackground(new Color(81, 79, 112));
        JPanel p = new JPanel();
        p.setBackground(new Color(81, 79, 112));
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        refreshMyBookingsTable();

        GroupLayout panelBorderLayout = new GroupLayout(panelBorder);
        panelBorder.setLayout(panelBorderLayout);
        panelBorderLayout.setHorizontalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(titleLabel)
                                        .addComponent(spTable))
                                .addGap(20, 20, 20))
        );
        panelBorderLayout.setVerticalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(titleLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spTable, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(panelBorder, BorderLayout.CENTER);
        return panel;
    }

    private void refreshMyBookingsTable() {
        JDialog loadingDialog = createLoadingDialog();
        new Thread(() -> {
            try {
                List<Booking> bookings = apiClient.getBookingsByUserId(currentUser.getId());
                System.out.println("Refreshing My Bookings table with " + bookings.size() + " bookings.");
                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel model = (DefaultTableModel) myBookingsTable.getModel();
                    model.setRowCount(0);
                    for (Booking booking : bookings) {
                        String carPlate = (booking.getCar() != null) ? booking.getCar().getNoPlate() : "Unknown";
                        String startDate = (booking.getStartDate() != null) ? booking.getStartDate().toString() : "N/A";
                        String endDate = (booking.getEndDate() != null) ? booking.getEndDate().toString() : "N/A";
                        myBookingsTable.addRow(new Object[]{
                                carPlate,
                                startDate,
                                endDate,
                                String.format("$%.2f", booking.getTotalAmount()),
                                booking.isPaid() ? StatusType.PAID : StatusType.PENDING
                        });
                    }
                    loadingDialog.dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error fetching bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    loadingDialog.dispose();
                });
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(21, 19, 52));

        PanelBorder panelBorder = new PanelBorder();
        panelBorder.setBackground(new Color(81, 79, 112));

        JLabel titleLabel = new JLabel("Make Payment");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JScrollPane spTable = new JScrollPane();
        paymentTable = new Table();
        paymentTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Booking ID", "Car Plate", "Total Amount", "Status"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Apply table styling
        styleTable(paymentTable);

        spTable.setViewportView(paymentTable);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(new Color(81, 79, 112));
        spTable.getViewport().setBackground(new Color(81, 79, 112));
        JPanel p = new JPanel();
        p.setBackground(new Color(81, 79, 112));
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        JButton payButton = new JButton("Pay Selected Booking");
        payButton.setBackground(new Color(81, 79, 112, 255));
        payButton.setForeground(Color.WHITE);
        payButton.setToolTipText("Select a booking and click to pay");
        payButton.setEnabled(false);
        payButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (payButton.isEnabled()) {
                    payButton.setBackground(new Color(100, 98, 131));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                payButton.setBackground(new Color(81, 79, 112, 255));
            }
        });
        paymentTable.getSelectionModel().addListSelectionListener(e -> {
            payButton.setEnabled(paymentTable.getSelectedRow() >= 0);
        });
        payButton.addActionListener(e -> {
            int selectedRow = paymentTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long bookingId = Long.parseLong(paymentTable.getValueAt(selectedRow, 0).toString());
                System.out.println("Paying booking ID: " + bookingId);
                JDialog loadingDialog = createLoadingDialog();
                new Thread(() -> {
                    try {
                        Booking booking = apiClient.getBookingById(bookingId);
                        if (booking == null) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "Booking ID " + bookingId + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                                loadingDialog.dispose();
                            });
                            return;
                        }
                        if (booking.isPaid()) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "Booking ID " + bookingId + " is already paid.", "Info", JOptionPane.INFORMATION_MESSAGE);
                                loadingDialog.dispose();
                            });
                        } else {
                            apiClient.payBooking(bookingId);
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "Payment successful!");
                                refreshAll();
                                loadingDialog.dispose();
                            });
                        }
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Error processing payment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            loadingDialog.dispose();
                        });
                    }
                }).start();
                loadingDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking to pay.");
            }
        });

        refreshPaymentTable();

        GroupLayout panelBorderLayout = new GroupLayout(panelBorder);
        panelBorder.setLayout(panelBorderLayout);
        panelBorderLayout.setHorizontalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(titleLabel)
                                        .addComponent(spTable)
                                        .addComponent(payButton, GroupLayout.Alignment.TRAILING))
                                .addGap(20, 20, 20))
        );
        panelBorderLayout.setVerticalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(titleLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spTable, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                .addGap(10, 10, 10)
                                .addComponent(payButton)
                                .addGap(20, 20, 20))
        );

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(panelBorder, BorderLayout.CENTER);
        return panel;
    }

    private void refreshPaymentTable() {
        JDialog loadingDialog = createLoadingDialog();
        new Thread(() -> {
            try {
                List<Booking> bookings = apiClient.getBookingsByUserId(currentUser.getId());
                System.out.println("Refreshing Payment table with " + bookings.size() + " bookings.");
                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();
                    model.setRowCount(0);
                    for (Booking booking : bookings) {
                        if (!booking.isPaid()) {
                            String carPlate = (booking.getCar() != null) ? booking.getCar().getNoPlate() : "Unknown";
                            paymentTable.addRow(new Object[]{
                                    booking.getId(),
                                    carPlate,
                                    String.format("$%.2f", booking.getTotalAmount()),
                                    StatusType.PENDING
                            });
                        }
                    }
                    loadingDialog.dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error fetching unpaid bookings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    loadingDialog.dispose();
                });
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    private JDialog createLoadingDialog() {
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
        return loadingDialog;
    }

    private void bookCar(Table table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Long carId = Long.parseLong(table.getValueAt(selectedRow, 0).toString());
            System.out.println("Selected row: " + selectedRow + ", Car ID: " + carId);
            JDialog bookingDialog = new JDialog(this, "Book Car", true);
            bookingDialog.setSize(400, 300);
            bookingDialog.setLocationRelativeTo(this);
            bookingDialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel startDateLabel = new JLabel("Start Date:");
            startDateLabel.setForeground(Color.WHITE);
            com.toedter.calendar.JDateChooser startDatePicker = new com.toedter.calendar.JDateChooser();
            startDatePicker.setDateFormatString("yyyy-MM-dd");
            startDatePicker.setBackground(new Color(81, 79, 112, 255));
            startDatePicker.getComponent(0).setForeground(Color.WHITE);
            startDatePicker.getComponent(1).setForeground(Color.WHITE);

            JLabel endDateLabel = new JLabel("End Date:");
            endDateLabel.setForeground(Color.WHITE);
            com.toedter.calendar.JDateChooser endDatePicker = new com.toedter.calendar.JDateChooser();
            endDatePicker.setDateFormatString("yyyy-MM-dd");
            endDatePicker.setBackground(new Color(81, 79, 112, 255));
            endDatePicker.getComponent(0).setForeground(Color.WHITE);
            endDatePicker.getComponent(1).setForeground(Color.WHITE);

            JButton bookButton = new JButton("Confirm Booking");
            bookButton.setBackground(new Color(81, 79, 112, 255));
            bookButton.setForeground(Color.WHITE);
            bookButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    bookButton.setBackground(new Color(100, 98, 131));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    bookButton.setBackground(new Color(81, 79, 112, 255));
                }
            });

            bookButton.addActionListener(e -> {
                try {
                    java.util.Date startUtilDate = startDatePicker.getDate();
                    java.util.Date endUtilDate = endDatePicker.getDate();
                    if (startUtilDate == null || endUtilDate == null) {
                        JOptionPane.showMessageDialog(bookingDialog, "Please select both start and end dates.");
                        return;
                    }
                    LocalDate startDate = startUtilDate.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();
                    LocalDate endDate = endUtilDate.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();

                    if (startDate.isAfter(endDate)) {
                        JOptionPane.showMessageDialog(bookingDialog, "End date must be after start date.");
                        return;
                    }
                    BookingModel bookingModel = new BookingModel();
                    bookingModel.setUserId(currentUser.getId());
                    bookingModel.setCarId(carId);
                    bookingModel.setStartDate(startDate);
                    bookingModel.setEndDate(endDate);
                    double pricePerDay = Double.parseDouble(table.getValueAt(selectedRow, 4).toString());
                    long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
                    bookingModel.setTotalAmount(pricePerDay * days);
                    bookingModel.setPaid(false);

                    JDialog loadingDialog = createLoadingDialog();
                    new Thread(() -> {
                        try {
                            Booking booking = apiClient.createBooking(bookingModel);
                            boolean availabilityUpdated = false;
                            try {
                                apiClient.updateCarAvailability(carId, false);
                                System.out.println("Car ID " + carId + " marked unavailable.");
                                availabilityUpdated = true;
                            } catch (Exception ex) {
                                System.out.println("Failed to update car availability: " + ex.getMessage() +
                                        ". Relying on booking filter. Response: " + ex.getCause());
                                JOptionPane.showMessageDialog(UserDashboard.this,
                                        "Booking created, but failed to update car availability. The car should be excluded " +
                                                "based on the booking. Contact support if it persists.",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
                            }
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(UserDashboard.this, "Booking created successfully!");
                                bookingDialog.dispose();
                                setForm(createBookCarPanel());
                                refreshMyBookingsTable();
                                refreshPaymentTable();
                                refreshStats();
                                loadingDialog.dispose();
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(UserDashboard.this,
                                        "Error creating booking: " + ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
                                loadingDialog.dispose();
                            });
                        }
                    }).start();
                    loadingDialog.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(bookingDialog, "Error selecting dates: " + ex.getMessage(), "Date Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            bookingDialog.getContentPane().setBackground(new Color(21, 19, 52));
            gbc.gridx = 0;
            gbc.gridy = 0;
            bookingDialog.add(startDateLabel, gbc);
            gbc.gridx = 1;
            bookingDialog.add(startDatePicker, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            bookingDialog.add(endDateLabel, gbc);
            gbc.gridx = 1;
            bookingDialog.add(endDatePicker, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            bookingDialog.add(bookButton, gbc);

            bookingDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a car to book.");
        }
    }

    private JPanel createBookCarPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(21, 19, 52));

        PanelBorder panelBorder = new PanelBorder();
        panelBorder.setBackground(new Color(81, 79, 112));

        JLabel titleLabel = new JLabel("Available Cars");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JScrollPane spTable = new JScrollPane();
        availableCarsTable = new Table();
        availableCarsTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Brand", "Model", "Plate", "Price/Day"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false};

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        availableCarsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableCarsTable.setToolTipText("Select a car to book");

        // Apply table styling
        styleTable(availableCarsTable);

        availableCarsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("AvailableCarsTable clicked at: " + e.getPoint() + ", row: " + availableCarsTable.getSelectedRow());
            }
        });
        spTable.setViewportView(availableCarsTable);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(new Color(81, 79, 112));
        spTable.getViewport().setBackground(new Color(81, 79, 112));
        JPanel p = new JPanel();
        p.setBackground(new Color(81, 79, 112));
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        JButton bookButton = new JButton("Book Selected Car");
        bookButton.setBackground(new Color(81, 79, 112, 255));
        bookButton.setForeground(Color.WHITE);
        bookButton.setToolTipText("Select a car and click to book it");
        bookButton.setEnabled(false);
        bookButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (bookButton.isEnabled()) {
                    bookButton.setBackground(new Color(100, 98, 131));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                bookButton.setBackground(new Color(81, 79, 112, 255));
            }
        });
        availableCarsTable.getSelectionModel().addListSelectionListener(e -> {
            bookButton.setEnabled(availableCarsTable.getSelectedRow() >= 0);
            System.out.println("Selection changed, selected row: " + availableCarsTable.getSelectedRow());
        });
        bookButton.addActionListener(e -> bookCar(availableCarsTable));

        refreshAvailableCarsTable();

        GroupLayout panelBorderLayout = new GroupLayout(panelBorder);
        panelBorder.setLayout(panelBorderLayout);
        panelBorderLayout.setHorizontalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(titleLabel)
                                        .addComponent(spTable)
                                        .addComponent(bookButton, GroupLayout.Alignment.TRAILING))
                                .addGap(20, 20, 20))
        );
        panelBorderLayout.setVerticalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(titleLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spTable, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                .addGap(10, 10, 10)
                                .addComponent(bookButton)
                                .addGap(20, 20, 20))
        );

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(panelBorder, BorderLayout.CENTER);
        return panel;
    }

    private void refreshAvailableCarsTable() {
        JDialog loadingDialog = createLoadingDialog();
        new Thread(() -> {
            try {
                List<Car> cars = apiClient.getAvailableCars();
                List<Booking> allBookings = apiClient.getAllBookings();
                List<Long> bookedCarIds = allBookings.stream()
                        .filter(b -> b.getCar() != null && b.getEndDate() != null &&
                                (b.getEndDate().isAfter(LocalDate.now()) ||
                                        b.getEndDate().isEqual(LocalDate.now())))
                        .map(b -> {
                            System.out.println("Processing booking ID: " + b.getId() + ", Car: " + (b.getCar() != null ? b.getCar().getId() : "null") +
                                    ", End Date: " + b.getEndDate());
                            return b.getCar().getId();
                        })
                        .collect(Collectors.toList());
                System.out.println("Refreshing Available Cars table with " + cars.size() + " cars, excluding booked car IDs: " + bookedCarIds);

                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel model = (DefaultTableModel) availableCarsTable.getModel();
                    model.setRowCount(0);
                    for (Car car : cars) {
                        if (!bookedCarIds.contains(car.getId()) && car.isAvailable()) {
                            availableCarsTable.addRow(new Object[]{
                                    car.getId(),
                                    car.getBrand(),
                                    car.getModel(),
                                    car.getNoPlate(),
                                    car.getPricePerDay()
                            });
                            System.out.println("Added car ID " + car.getId() + " to table.");
                        } else {
                            System.out.println("Excluded car ID " + car.getId() + ": booked=" + bookedCarIds.contains(car.getId()) + ", available=" + car.isAvailable());
                        }
                    }
                    availableCarsTable.repaint();
                    availableCarsTable.revalidate();
                    availableCarsTable.getParent().repaint();
                    availableCarsTable.getParent().revalidate();
                    loadingDialog.dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error fetching cars: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    loadingDialog.dispose();
                });
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    private void refreshAll() {
        JDialog loadingDialog = createLoadingDialog();
        new Thread(() -> {
            try {
                List<Booking> bookings = apiClient.getBookingsByUserId(currentUser.getId());
                List<Car> cars = apiClient.getAvailableCars();
                List<Booking> allBookings = apiClient.getAllBookings();
                List<Long> bookedCarIds = allBookings.stream()
                        .filter(b -> b.getCar() != null && b.getEndDate() != null &&
                                (b.getEndDate().isAfter(LocalDate.now()) ||
                                        b.getEndDate().isEqual(LocalDate.now())))
                        .map(b -> {
                            System.out.println("Processing booking ID: " + b.getId() + ", Car: " + (b.getCar() != null ? b.getCar().getId() : "null") +
                                    ", End Date: " + b.getEndDate());
                            return b.getCar().getId();
                        })
                        .collect(Collectors.toList());
                long pendingPaymentsCount = bookings.stream().filter(b -> !b.isPaid()).count();
                System.out.println("Refreshing all: " + cars.size() + " cars, " + bookings.size() + " bookings, excluding booked car IDs: " + bookedCarIds);

                SwingUtilities.invokeLater(() -> {
                    refreshAvailableCarsTable();
                    DefaultTableModel carsModel = (DefaultTableModel) availableCarsTable.getModel();
                    carsModel.setRowCount(0);
                    for (Car car : cars) {
                        if (!bookedCarIds.contains(car.getId())) {
                            availableCarsTable.addRow(new Object[]{
                                    car.getId(),
                                    car.getBrand(),
                                    car.getModel(),
                                    car.getNoPlate(),
                                    car.getPricePerDay()
                            });
                        } else {
                            System.out.println("Car ID " + car.getId() + " excluded due to active booking.");
                        }
                    }

                    DefaultTableModel bookingsModel = (DefaultTableModel) myBookingsTable.getModel();
                    bookingsModel.setRowCount(0);
                    for (Booking booking : bookings) {
                        String carPlate = (booking.getCar() != null) ? booking.getCar().getNoPlate() : "Unknown";
                        String startDate = (booking.getStartDate() != null) ? booking.getStartDate().toString() : "N/A";
                        String endDate = (booking.getEndDate() != null) ? booking.getEndDate().toString() : "N/A";
                        myBookingsTable.addRow(new Object[]{
                                carPlate,
                                startDate,
                                endDate,
                                String.format("$%.2f", booking.getTotalAmount()),
                                booking.isPaid() ? StatusType.PAID : StatusType.PENDING
                        });
                    }

                    DefaultTableModel paymentModel = (DefaultTableModel) paymentTable.getModel();
                    paymentModel.setRowCount(0);
                    for (Booking booking : bookings) {
                        if (!booking.isPaid()) {
                            String carPlate = (booking.getCar() != null) ? booking.getCar().getNoPlate() : "Unknown";
                            paymentTable.addRow(new Object[]{
                                    booking.getId(),
                                    carPlate,
                                    String.format("$%.2f", booking.getTotalAmount()),
                                    StatusType.PENDING
                            });
                        }
                    }

                    totalBookingsCard.setData(new Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/flag.png")),
                            "Total Bookings",
                            String.valueOf(bookings.size()),
                            "Your total bookings"
                    ));
                    pendingPaymentsCard.setData(new Model_Card(
                            new ImageIcon(getClass().getResource("/com/Buildex/icon/profit.png")),
                            "Pending Payments",
                            String.valueOf(pendingPaymentsCount),
                            "Bookings awaiting payment"
                    ));

                    loadingDialog.dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error refreshing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    loadingDialog.dispose();
                });
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApiClient apiClient = new ApiClient();
            User user = new User();
            user.setId(1L);
            user.setName("Test User");
            new UserDashboard(user, apiClient).setVisible(true);
        });
    }
}