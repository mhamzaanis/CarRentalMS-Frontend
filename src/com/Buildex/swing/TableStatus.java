package com.Buildex.swing;

import com.Buildex.model.StatusType;

import javax.swing.*;
import java.awt.*;

public class TableStatus extends JLabel {

    public TableStatus() { // No-argument constructor
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public void setStatus(StatusType status) {
        if (status == StatusType.PAID) {
            setForeground(new Color(0, 128, 0)); // Green for Paid
            setBackground(new Color(204, 255, 204));
        } else if (status == StatusType.PENDING) {
            setForeground(new Color(255, 165, 0)); // Orange for Pending
            setBackground(new Color(255, 245, 204));
        } else {
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);
        }
        setText(status.toString());
    }
}