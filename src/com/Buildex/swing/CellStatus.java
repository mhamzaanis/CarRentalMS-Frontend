package com.Buildex.swing;

import com.Buildex.model.StatusType;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CellStatus extends TableStatus implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TableStatus status = new TableStatus();
        status.setStatus((StatusType) value); // Changed setType to setStatus
        if (isSelected) {
            status.setBackground(table.getSelectionBackground());
            status.setForeground(table.getSelectionForeground());
        } else {
            // Colors are already set by setStatus, but we can adjust if needed
            status.setBackground(table.getBackground());
        }
        return status;
    }
}