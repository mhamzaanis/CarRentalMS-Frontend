package com.Buildex.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Header extends JPanel {
    private JButton minimizeButton;
    private JButton maximizeButton;
    private JButton closeButton;

    public Header() {
        initComponents();
        setOpaque(true); // Ensure background is applied
        setBackground(new Color(21, 19, 52)); // Updated to match LoginForm
    }

    public void setButtonSize(int size) {
        Dimension buttonSize = new Dimension(size, size);
        minimizeButton.setPreferredSize(buttonSize);
        minimizeButton.setMinimumSize(buttonSize);
        minimizeButton.setMaximumSize(buttonSize);
        maximizeButton.setPreferredSize(buttonSize);
        maximizeButton.setMinimumSize(buttonSize);
        maximizeButton.setMaximumSize(buttonSize);
        closeButton.setPreferredSize(buttonSize);
        closeButton.setMinimumSize(buttonSize);
        closeButton.setMaximumSize(buttonSize);
        revalidate();
        repaint();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Initialize window control buttons
        minimizeButton = new JButton("−");
        maximizeButton = new JButton("□");
        closeButton = new JButton("×");

        // Style buttons
        for (JButton button : new JButton[]{minimizeButton, maximizeButton, closeButton}) {
            button.setFont(new Font("SansSerif", Font.BOLD, 16)); // Increased font size for larger buttons
            button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Increased padding
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setForeground(Color.WHITE); // Updated to match LoginForm
        }

        // Add hover effects
        minimizeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                minimizeButton.setForeground(new Color(150, 150, 150)); // Lighter shade for hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                minimizeButton.setForeground(Color.WHITE);
            }
        });

        maximizeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                maximizeButton.setForeground(new Color(150, 150, 150));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                maximizeButton.setForeground(Color.WHITE);
            }
        });

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(new Color(255, 70, 70)); // Red for close button hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.WHITE);
            }
        });

        // Add actions
        minimizeButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.setState(Frame.ICONIFIED);
        });

        maximizeButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                frame.setExtendedState(Frame.NORMAL);
                maximizeButton.setText("□");
            } else {
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                maximizeButton.setText("❐");
            }
        });

        closeButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Push buttons to the right
                                .addComponent(minimizeButton)
                                .addComponent(maximizeButton)
                                .addComponent(closeButton)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(minimizeButton)
                                        .addComponent(maximizeButton)
                                        .addComponent(closeButton))
                                .addContainerGap())
        );
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(21, 19, 52)); // Updated to match LoginForm
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(0, 0, 25, getHeight());
        g2.fillRect(getWidth() - 25, getHeight() - 25, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }
}