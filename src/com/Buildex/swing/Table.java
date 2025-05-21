package com.Buildex.swing;

import com.Buildex.model.StatusType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Table extends JTable {
    public Table() {
        setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Column1", "Column2", "Column3", "Column4", "Status"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            int lastRow = -1;
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != lastRow) {
                    lastRow = row;
                    repaint();
                }
            }
        });

        setShowHorizontalLines(false);
        setGridColor(new Color(51, 49, 82));
        setRowHeight(40);
        setBackground(new Color(255, 255, 255, 255)); // Updated to match LoginForm
        setForeground(Color.BLACK); // Updated to match LoginForm
        // Set selection colors to match the theme
        setSelectionBackground(new Color(255, 255, 255)); // Matches hover effect color
        setSelectionForeground(Color.BLACK); // Ensure text remains readable when selected
        // Clear any default selection
        clearSelection();
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setBackground(new Color(255, 255, 255, 255)); // Updated to match LoginForm
        getTableHeader().setForeground(Color.BLACK); // Updated to match LoginForm
        setCustomRenderer();
    }

    private void setCustomRenderer() {
        if (getColumnCount() > 4 && "Status".equals(getColumnName(4))) {
            getColumnModel().getColumn(4).setCellRenderer(new CellStatus());
        }
    }

    public void addRow(Object[] row) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.addRow(row);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getMousePosition() != null) {
            int row = rowAtPoint(getMousePosition());
            if (row >= 0) {
                g.setColor(new Color(100, 98, 131, 100)); // Lighter shade for highlight
                g.fillRect(0, row * getRowHeight(), getWidth(), getRowHeight());
            }
        }
    }
}