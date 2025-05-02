package com.github.timan1802.fakedatainsert;

import javax.swing.*;
import java.awt.*;

// 두 개의 콤보박스가 포함된 패널
class ComboPanel extends JPanel {
    final JComboBox<String> typeBox;
    final JComboBox<String> subTypeBox;
    public ComboPanel(String[] types, String[] subTypes) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        typeBox = new JComboBox<>(types);
        subTypeBox = new JComboBox<>(subTypes);
        add(typeBox);
        add(subTypeBox);
        setOpaque(true);
    }
    public String getSelectedType() {
        return (String)typeBox.getSelectedItem();
    }
    public String getSelectedSubType() {
        return (String)subTypeBox.getSelectedItem();
    }
}