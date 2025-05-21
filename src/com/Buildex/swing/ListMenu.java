package com.Buildex.swing;

import com.Buildex.event.EventMenuSelected;
import com.Buildex.model.Model_Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class ListMenu extends JList<Model_Menu> {
    private final DefaultListModel<Model_Menu> model;
    private int selectedIndex = -1;
    private int overIndex = -1;
    private EventMenuSelected event;

    public void addEventMenuSelected(EventMenuSelected event) {
        this.event = event;
    }

    public ListMenu() {
        model = new DefaultListModel<>();
        setModel(model);
        setBackground(new Color(21, 19, 52)); // Updated to match LoginForm
        setForeground(Color.WHITE); // Updated to match LoginForm
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isLeftMouseButton(me)) {
                    int index = locationToIndex(me.getPoint());
                    Model_Menu menu = model.getElementAt(index);
                    if (menu.getType() == Model_Menu.MenuType.MENU) {
                        selectedIndex = index;
                        if (event != null) {
                            int menuIndex = 0;
                            for (int i = 0; i <= index; i++) {
                                Model_Menu item = model.getElementAt(i);
                                if (item.getType() == Model_Menu.MenuType.MENU) {
                                    menuIndex++;
                                }
                            }
                            event.selected(menuIndex - 1);
                        } else {
                            System.out.println("Invalid selection index: " + index + ", model size: " + model.getSize());
                        }
                    }
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent me) {
                overIndex = -1;
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                int index = locationToIndex(me.getPoint());
                if (index != overIndex) {
                    Model_Menu menu = model.getElementAt(index);
                    if (menu.getType() == Model_Menu.MenuType.MENU) {
                        overIndex = index;
                    } else {
                        overIndex = -1;
                    }
                    repaint();
                }
            }
        });
    }

    @Override
    public ListCellRenderer<? super Model_Menu> getCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object o, int index, boolean selected, boolean focus) {
                Model_Menu data = (Model_Menu) o;
                MenuItem item = new MenuItem(data);
                item.setSelected(selectedIndex == index);
                item.setOver(overIndex == index);
                return item;
            }
        };
    }

    public void addItem(Model_Menu data) {
        model.addElement(data);
    }
}