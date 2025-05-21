package com.Buildex.component;

import com.Buildex.api.ApiClient;
import com.Buildex.event.EventMenuSelected;
import com.Buildex.form.Form_Bookings;
import com.Buildex.model.Model_Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

public class AdminDashboard extends JPanel {
    private EventMenuSelected event;
    private com.Buildex.swing.ListMenu listMenu1;
    private ApiClient apiClient;
    private JPanel panelMoving;
    private JLabel jLabel1;

    public AdminDashboard(ApiClient apiClient) {
        this.apiClient = apiClient;
        initComponents();
        setOpaque(true);
        setBackground(new Color(21, 19, 52));
        listMenu1.setOpaque(true);
        listMenu1.setBackground(new Color(21, 19, 52));
        init();
    }

    public AdminDashboard(List<Model_Menu> menuItems) {
        initComponents();
        setOpaque(true);
        setBackground(new Color(21, 19, 52));
        listMenu1.setOpaque(true);
        listMenu1.setBackground(new Color(21, 19, 52));
        for (Model_Menu item : menuItems) {
            listMenu1.addItem(item);
        }
    }

    public AdminDashboard() {
        initComponents();
    }

    private void init() {
        listMenu1.addItem(new Model_Menu("1", "Dashboard", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("2", "Users", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("3", "Cars", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("4", "Bookings", Model_Menu.MenuType.MENU));
        listMenu1.addItem(new Model_Menu("", " ", Model_Menu.MenuType.EMPTY));
        listMenu1.addItem(new Model_Menu("", "Settings", Model_Menu.MenuType.TITLE));
        listMenu1.addItem(new Model_Menu("", " ", Model_Menu.MenuType.EMPTY));
        listMenu1.addItem(new Model_Menu("8", "Logout", Model_Menu.MenuType.MENU));
    }

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
        listMenu1.addEventMenuSelected(event); // Delegate directly to the event
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        panelMoving = new JPanel();
        jLabel1 = new JLabel();
        listMenu1 = new com.Buildex.swing.ListMenu();

        panelMoving.setOpaque(true);
        panelMoving.setBackground(new Color(21, 19, 52));

        jLabel1.setFont(new Font("sansserif", Font.BOLD, 18));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setIcon(new ImageIcon(getClass().getResource("/com/Buildex/icon/logo.png")));
        jLabel1.setText("Car Rental");

        GroupLayout panelMovingLayout = new GroupLayout(panelMoving);
        panelMoving.setLayout(panelMovingLayout);
        panelMovingLayout.setHorizontalGroup(
                panelMovingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panelMovingLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                                .addContainerGap())
        );
        panelMovingLayout.setVerticalGroup(
                panelMovingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, panelMovingLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(jLabel1)
                                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(panelMoving, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(listMenu1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(panelMoving, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(listMenu1, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
        );
    }

    @Override
    protected void paintChildren(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint g = new GradientPaint(0, 0, new Color(21, 19, 52), 0, getHeight(), new Color(81, 79, 112));
        g2.setPaint(g);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(getWidth() - 20, 0, getWidth(), getHeight());
        super.paintChildren(grphcs);
    }

    private int x;
    private int y;

    public void initMoving(JFrame frame) {
        if (panelMoving != null) {
            panelMoving.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {
                    x = me.getX();
                    y = me.getY();
                }
            });
            panelMoving.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent me) {
                    frame.setLocation(me.getXOnScreen() - x, me.getYOnScreen() - y);
                }
            });
        } else {
            System.out.println("Warning: panelMoving is null in initMoving");
        }
    }
}