package com.github.timan1802.fakedatainsert;

import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DataFakerDialogJava extends DialogWrapper {

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable            table      = new JTable(tableModel);

    private final DbTable dbTable;

    public DataFakerDialogJava(DbTable dbTable) {
        super(true);
        this.dbTable = dbTable;

        init();
        setTitle("Fake Data Insert for " + dbTable.getName());
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel             topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc      = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel     countLabel = new JLabel("생성할 데이터 개수");
        JTextField countField = new JTextField("100", 10);

        JLabel            countryLabel    = new JLabel("국가");
        JComboBox<String> countryComboBox = new JComboBox<>(new String[]{"KO", "US", "JP", "CN"});

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(countLabel, gbc);
        gbc.gridx = 1;
        topPanel.add(countField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(countryLabel, gbc);
        gbc.gridx = 1;
        topPanel.add(countryComboBox, gbc);

        // ✅ DbTable에서 컬럼명 추출
        List<String> columnNames = dbTable.getDasChildren(ObjectKind.COLUMN)
                .toList()
                                          .stream()
                                          .map(it -> it.getName())
                                          .collect(Collectors.toList())
                ;

        // 사용 가능한 데이터 타입 정의
        String[] availableDataTypes = {"이름", "성", "이메일", "전화번호", "주소", "도시", "회사명", "직업", "생년월일", "나이", "신용카드", "숫자",
                                       "문자열", "날짜", "불리언"};

        // TableModel을 수정하여 JComboBox를 제대로 처리하도록 함
        DefaultTableModel customTableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0 && getValueAt(0, columnIndex) instanceof JComboBox) {
                    return JComboBox.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return row == 0;  // 첫 번째 행만 편집 가능하도록 설정
            }
        };

        // TableModel 설정
        table.setModel(customTableModel);

        // 컬럼명 설정
        for (String columnName : columnNames) {
            customTableModel.addColumn(columnName);
        }

        // 콤보박스로 첫 번째 행 추가
        Object[] dataTypeRow = columnNames.stream().map(column -> {
            JComboBox<String> comboBox = new JComboBox<>(availableDataTypes);
            comboBox.setSelectedIndex(0);
            return comboBox;
        }).toArray()
                ;
        customTableModel.addRow(dataTypeRow);

        // 콤보박스 렌더러와 에디터 설정
        table.getColumnModel().getColumns().asIterator().forEachRemaining(column -> {
            // 렌더러 설정
            column.setCellRenderer((table, value, isSelected, hasFocus, row, col) -> {
                if (value instanceof JComboBox) {
                    JComboBox<?> comboBox = (JComboBox<?>) value;
                    if (isSelected) {
                        comboBox.setBackground(table.getSelectionBackground());
                        comboBox.setForeground(table.getSelectionForeground());
                    } else {
                        comboBox.setBackground(table.getBackground());
                        comboBox.setForeground(table.getForeground());
                    }
                    return comboBox;
                }
                return new JLabel(value != null ? value.toString() : "");
            });

            // 에디터 설정
            column.setCellEditor(new DefaultCellEditor(new JComboBox<>(availableDataTypes)) {
                @Override
                public Component getTableCellEditorComponent(JTable table,
                                                             Object value,
                                                             boolean isSelected,
                                                             int row,
                                                             int column) {
                    JComboBox<?> comboBox = (JComboBox<?>) super.getTableCellEditorComponent(table,
                                                                                             value,
                                                                                             isSelected,
                                                                                             row,
                                                                                             column);
                    if (value instanceof JComboBox) {
                        comboBox.setSelectedItem(((JComboBox<?>) value).getSelectedItem());
                    }
                    return comboBox;
                }
            });
        })
        ;

        // 첫 번째 행의 높이를 조절
        table.setRowHeight(0, 25);

        // 예시 데이터 추가 (원하면 제거 가능)
        if (columnNames.size() >= 4) {
            customTableModel.addRow(new Object[]{"Alfreds Futterkiste", "Maria Anders", "Berlin", "030-0074321"});
            customTableModel.addRow(new Object[]{"Antonio Moreno Taquería", "Antonio Moreno", "México D.F.",
                                                 "(5) 555-3932"});
        }

        table.setPreferredScrollableViewportSize(new Dimension(120 * Math.min(columnNames.size(), 5),
                                                               // 컬럼 수 따라 넓이 증감(가변 처리)
                                                               25 * 4));
        JScrollPane tableScrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JButton("Insert"));
        buttonPanel.add(new JButton("Cancel"));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}