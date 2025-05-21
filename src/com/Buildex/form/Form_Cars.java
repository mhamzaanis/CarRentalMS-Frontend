package com.Buildex.form;

import com.Buildex.api.ApiClient;
import com.Buildex.model.Car;
import com.Buildex.swing.ScrollBar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class Form_Cars extends JPanel {
    private ApiClient apiClient;
    private JScrollPane spTable;
    private com.Buildex.swing.Table table;
    private JButton addCarButton;
    private JButton editCarButton;
    private JButton deleteCarButton;
    private JTextField searchField;

    private List<Car> allCars; // Store the full list for filtering

    public Form_Cars(ApiClient apiClient) {
        this.apiClient = apiClient;
        initComponents();
        initData();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(0, 0, new Color(220, 240, 255), 0, getHeight(), Color.WHITE);
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
    }

    private void initData() {
        // Get the parent window dynamically
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog loadingDialog;
        if (parentWindow != null) {
            loadingDialog = new JDialog((Frame) parentWindow, "Loading", true);
        } else {
            loadingDialog = new JDialog((Window) null, "Loading");
            loadingDialog.setModal(false); // Non-modal if no parent is found
        }
        loadingDialog.setSize(200, 100);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.add(new JLabel("Loading data...", SwingConstants.CENTER));
        new Thread(() -> {
            try {
                allCars = apiClient.getAllCars();
                SwingUtilities.invokeLater(() -> {
                    refreshTable(allCars);
                    loadingDialog.dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error fetching cars: " + e.getMessage());
                    loadingDialog.dispose();
                });
            }
        }).start();
        loadingDialog.setVisible(true);
    }

    private void refreshTable(List<Car> cars) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Car car : cars) {
            table.addRow(new Object[]{
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getNoPlate(),
                    car.getPricePerDay(),
                    car.isAvailable() ? "Yes" : "No"
            });
        }
    }


    private void addCar() {
        JTextField noPlateField = new JTextField(10);
        JTextField brandField = new JTextField(10);
        JTextField modelField = new JTextField(10);
        JTextField pricePerDayField = new JTextField(10);
        JCheckBox availableCheckBox = new JCheckBox("Available", true);

        JPanel dialogPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        dialogPanel.add(new JLabel("Plate Number:"));
        dialogPanel.add(noPlateField);
        dialogPanel.add(new JLabel("Brand:"));
        dialogPanel.add(brandField);
        dialogPanel.add(new JLabel("Model:"));
        dialogPanel.add(modelField);
        dialogPanel.add(new JLabel("Price Per Day:"));
        dialogPanel.add(pricePerDayField);
        dialogPanel.add(new JLabel("Available:"));
        dialogPanel.add(availableCheckBox);

        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Add New Car", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String noPlate = noPlateField.getText().trim();
                String brand = brandField.getText().trim();
                String model = modelField.getText().trim();
                double pricePerDay = Double.parseDouble(pricePerDayField.getText().trim());
                boolean available = availableCheckBox.isSelected();

                if (noPlate.isEmpty() || brand.isEmpty() || model.isEmpty()) {
                    throw new IllegalArgumentException("Plate Number, Brand, and Model are required.");
                }

                Car newCar = new Car();
                newCar.setNoPlate(noPlate);
                newCar.setBrand(brand);
                newCar.setModel(model);
                newCar.setPricePerDay(pricePerDay);
                newCar.setAvailable(available);

                apiClient.addCar(newCar);
                initData();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Price Per Day must be a valid number.");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding car: " + e.getMessage());
            }
        }
    }

    private void editCar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) table.getValueAt(selectedRow, 0);
            try {
                Car car = allCars.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
                if (car == null) {
                    JOptionPane.showMessageDialog(this, "Car not found.");
                    return;
                }

                JTextField noPlateField = new JTextField(car.getNoPlate(), 10);
                JTextField brandField = new JTextField(car.getBrand(), 10);
                JTextField modelField = new JTextField(car.getModel(), 10);
                JTextField pricePerDayField = new JTextField(String.valueOf(car.getPricePerDay()), 10);
                JCheckBox availableCheckBox = new JCheckBox("Available", car.isAvailable());

                JPanel dialogPanel = new JPanel(new GridLayout(5, 2, 5, 5));
                dialogPanel.add(new JLabel("Plate Number:"));
                dialogPanel.add(noPlateField);
                dialogPanel.add(new JLabel("Brand:"));
                dialogPanel.add(brandField);
                dialogPanel.add(new JLabel("Model:"));
                dialogPanel.add(modelField);
                dialogPanel.add(new JLabel("Price Per Day:"));
                dialogPanel.add(pricePerDayField);
                dialogPanel.add(new JLabel("Available:"));
                dialogPanel.add(availableCheckBox);

                int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Edit Car", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String noPlate = noPlateField.getText().trim();
                    String brand = brandField.getText().trim();
                    String model = modelField.getText().trim();
                    double pricePerDay = Double.parseDouble(pricePerDayField.getText().trim());
                    boolean available = availableCheckBox.isSelected();

                    if (noPlate.isEmpty() || brand.isEmpty() || model.isEmpty()) {
                        throw new IllegalArgumentException("Plate Number, Brand, and Model are required.");
                    }

                    car.setNoPlate(noPlate);
                    car.setBrand(brand);
                    car.setModel(model);
                    car.setPricePerDay(pricePerDay);
                    car.setAvailable(available);

                    updateCar(car);
                    initData();
                    JOptionPane.showMessageDialog(this, "Car updated successfully!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Price Per Day must be a valid number.");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating car: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a car to edit.");
        }
    }

    private void deleteCar() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) table.getValueAt(selectedRow, 0);
            String carDetails = table.getValueAt(selectedRow, 1) + " " + table.getValueAt(selectedRow, 2) + " (" + table.getValueAt(selectedRow, 3) + ")";
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car: " + carDetails + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    apiClient.deleteCar(id);
                    initData();
                    JOptionPane.showMessageDialog(this, "Car deleted successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting car: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a car to delete.");
        }
    }

    private void searchCars() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.equals("search by brand, model, or plate...") || query.isEmpty()) {
            refreshTable(allCars);
        } else {
            List<Car> filteredCars = allCars.stream()
                    .filter(car -> car.getBrand().toLowerCase().contains(query) ||
                            car.getModel().toLowerCase().contains(query) ||
                            car.getNoPlate().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            refreshTable(filteredCars);
        }
    }

    private void updateCar(Car car) throws Exception {
        apiClient.updateCar(car);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        JPanel panelBorder = new com.Buildex.swing.PanelBorder();
        JLabel jLabel1 = new JLabel();
        spTable = new JScrollPane();
        table = new com.Buildex.swing.Table();
        addCarButton = new JButton("Add Car");
        editCarButton = new JButton("Edit Selected");
        deleteCarButton = new JButton("Delete Selected");
        searchField = new JTextField("Search by brand, model, or plate...");

        addCarButton.setBackground(new Color(50, 150, 50));
        addCarButton.setForeground(Color.WHITE);
        editCarButton.setBackground(new Color(50, 50, 150));
        editCarButton.setForeground(Color.WHITE);
        deleteCarButton.setBackground(new Color(200, 50, 50));
        deleteCarButton.setForeground(Color.WHITE);

        addCarButton.setToolTipText("Add a new car to the inventory");
        editCarButton.setToolTipText("Edit the selected car");
        deleteCarButton.setToolTipText("Delete the selected car from the inventory");

        addCarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addCarButton.setBackground(new Color(70, 180, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addCarButton.setBackground(new Color(50, 150, 50));
            }
        });
        editCarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editCarButton.setBackground(new Color(70, 70, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editCarButton.setBackground(new Color(50, 50, 150));
            }
        });
        deleteCarButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteCarButton.setBackground(new Color(255, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteCarButton.setBackground(new Color(200, 50, 50));
            }
        });

        addCarButton.addActionListener(e -> addCar());
        editCarButton.addActionListener(e -> editCar());
        deleteCarButton.addActionListener(e -> deleteCar());

        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search by brand, model, or plate...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search by brand, model, or plate...");
                }
            }
        });
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchCars(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchCars(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchCars(); }
        });

        setBackground(new Color(242, 242, 242));

        panelBorder.setBackground(Color.WHITE);

        jLabel1.setFont(new Font("sansserif", Font.BOLD, 18));
        jLabel1.setForeground(new Color(127, 127, 127));
        jLabel1.setText("All Cars");

        spTable.setBorder(null);
        table.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Brand", "Model", "Plate", "Price/Day", "Available"}
        ) {
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false};

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

        GroupLayout panelBorderLayout = new GroupLayout(panelBorder);
        panelBorder.setLayout(panelBorderLayout);
        panelBorderLayout.setHorizontalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(spTable)
                                        .addGroup(panelBorderLayout.createSequentialGroup()
                                                .addComponent(addCarButton)
                                                .addGap(10, 10, 10)
                                                .addComponent(editCarButton)
                                                .addGap(10, 10, 10)
                                                .addComponent(deleteCarButton)))
                                .addContainerGap())
        );
        panelBorderLayout.setVerticalGroup(
                panelBorderLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelBorderLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(jLabel1)
                                .addGap(10, 10, 10)
                                .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(spTable, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                                .addGap(10, 10, 10)
                                .addGroup(panelBorderLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(addCarButton)
                                        .addComponent(editCarButton)
                                        .addComponent(deleteCarButton))
                                .addGap(20, 20, 20))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(panelBorder, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(panelBorder, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );
    }
}