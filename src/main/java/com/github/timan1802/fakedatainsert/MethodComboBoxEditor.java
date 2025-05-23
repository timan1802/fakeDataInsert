package com.github.timan1802.fakedatainsert;

import com.github.timan1802.fakedatainsert.utils.FakerUtils;
import net.datafaker.Faker;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MethodComboBoxEditor extends DefaultCellEditor {
    private final Faker faker;

    public MethodComboBoxEditor(Faker faker) {
        super(new JComboBox<>());
        this.faker = faker;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        JComboBox<String> combo = (JComboBox<String>) super.getTableCellEditorComponent(
                table, value, isSelected, row, column
        );

        String       provider = (String) table.getValueAt(row, 3);
        List<String> methods  = FakerUtils.getProviderMethodNames(faker, provider);

        combo.removeAllItems();
        methods.forEach(combo::addItem);

        if (value != null) {
            combo.setSelectedItem(value);
        }

        return combo;
    }
}