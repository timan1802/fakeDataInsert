package com.github.timan1802.fakedatainsert;

import com.github.timan1802.fakedatainsert.utils.FakerUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.Messages;
import net.datafaker.Faker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class TableMappingSettingsPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable            table;
    private final JButton addButton;
    private final Faker   faker;

    // 기존 필드들...
    
    public TableMappingSettingsPanel() {
        faker = new Faker();
        setLayout(new BorderLayout());

        // 컬럼 이름을 다국어화된 메시지로 설정
        model = new DefaultTableModel(
                new String[]{
                        MessagesBundle.message("table.mapping.enabled"),
                        MessagesBundle.message("table.mapping.text"),
                        MessagesBundle.message("table.mapping.match.type"),
                        MessagesBundle.message("table.mapping.provider"),
                        MessagesBundle.message("table.mapping.method"),
                        MessagesBundle.message("table.mapping.delete")
                },
                0
        ){
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return String.class;
            }
        };


        table = new JTable(model);
        setupTable();

        // Add Rule 버튼
        addButton = new JButton(MessagesBundle.message("table.mapping.add.rule"));
        addButton.addActionListener(e -> addNewRow());

        // 내보내기/불러오기 버튼 추가
        JButton exportButton = new JButton(MessagesBundle.message("table.mapping.export"));
        JButton importButton = new JButton(MessagesBundle.message("table.mapping.import"));
        
        exportButton.addActionListener(e -> exportSettings());
        importButton.addActionListener(e -> importSettings());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);

        JButton resetButton = new JButton(MessagesBundle.message("table.mapping.reset"));
        resetButton.addActionListener(e -> {
            if (Messages.showYesNoDialog(
                    MessagesBundle.message("table.mapping.reset.confirm"),
                    MessagesBundle.message("table.mapping.reset.title"),
                    Messages.getQuestionIcon()) == Messages.YES) {
                // 기본값으로 초기화
                PropertiesComponent.getInstance().unsetValue("TABLE_MAPPING_RULES");
                loadSettings();
            }
        });

        buttonPanel.add(resetButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadSettings(); // 초기 설정 로드
    }

    private void setupTable() {
        // 체크박스 컬럼
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));

        // 문자열 비교 콤보박스
        JComboBox<TableMappingRule.MatchType> matchTypeCombo =
            new JComboBox<>(TableMappingRule.MatchType.values());
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(matchTypeCombo));

        // Provider 콤보박스
        JComboBox<String> providerCombo = new JComboBox<>(
            FakerUtils.getAllProviderNames(faker).toArray(new String[0])
        );
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(providerCombo));

        // Method 콤보박스 - Provider에 따라 동적 업데이트
        table.getColumnModel().getColumn(4).setCellEditor(new MethodComboBoxEditor(faker));

        // 삭제 버튼
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer(MessagesBundle.message("table.mapping.delete")));
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(
            new JButton(MessagesBundle.message("table.mapping.delete")),
            (row) -> model.removeRow(row)
        ));

        // 컬럼 너비 설정
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
    }

    private void addNewRow() {
        model.addRow(new Object[]{
                true,
                "",
                TableMappingRule.MatchType.CONTAINS,
                FakerUtils.getAllProviderNames(faker).getFirst(),
                "",
                "Delete"
        });
    }

    public boolean validateAndSave() {
        // 중복 검사를 위한 Set
        Set<String> ruleSet = new HashSet<>();

        // 각 행의 규칙을 검사
        for (int i = 0; i < model.getRowCount(); i++) {
            String ruleKey = String.format("%s_%s_%s_%s",
                                           model.getValueAt(i, 1),
                                           model.getValueAt(i, 2),
                                           model.getValueAt(i, 3),
                                           model.getValueAt(i, 4)
            );

            if (!ruleSet.add(ruleKey)) {
                Messages.showErrorDialog(
                        MessagesBundle.message("validation.duplicate.rules"),
                        MessagesBundle.message("validation.duplicate.rules.title")
                );
                return false;
            }
        }

        // 설정 저장
        List<TableMappingRule> rules = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            TableMappingRule rule = new TableMappingRule();
            rule.setEnabled((Boolean) model.getValueAt(i, 0));
            rule.setText((String) model.getValueAt(i, 1));
            rule.setMatchType((TableMappingRule.MatchType) model.getValueAt(i, 2));
            rule.setProvider((String) model.getValueAt(i, 3));
            rule.setMethod((String) model.getValueAt(i, 4));
            rules.add(rule);
        }

        // JSON으로 변환하여 저장
        String json = new Gson().toJson(rules);
        PropertiesComponent.getInstance().setValue("TABLE_MAPPING_RULES", json);

        return true;
    }

    public void loadSettings() {
        // 기존 데이터 삭제
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        // 저장된 설정 불러오기
        String savedRulesJson = PropertiesComponent.getInstance()
                .getValue("TABLE_MAPPING_RULES");
        
        List<TableMappingRule> rules;
        
        if (savedRulesJson == null || savedRulesJson.isEmpty()) {
            // 저장된 설정이 없는 경우 기본값 사용
            rules = DefaultTableMappingRules.getDefaultRules();
            
            // 기본값을 저장
            String defaultJson = new Gson().toJson(rules);
            PropertiesComponent.getInstance().setValue("TABLE_MAPPING_RULES", defaultJson);
        } else {
            // 저장된 설정이 있는 경우 해당 설정 사용
            rules = new Gson().fromJson(
                savedRulesJson,
                new TypeToken<List<TableMappingRule>>(){}.getType()
        );
    }

    // 테이블에 데이터 추가
    for (TableMappingRule rule : rules) {
        model.addRow(new Object[]{
                rule.isEnabled(),
                rule.getText(),
                rule.getMatchType(),
                rule.getProvider(),
                rule.getMethod(),
                MessagesBundle.message("table.mapping.delete")
        });
    }
}

    // 설정 내보내기
    private void exportSettings() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(MessagesBundle.message("table.mapping.export.title"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON Files (*.json)";
            }
        });

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".json")) {
                file = new File(file.getParentFile(), file.getName() + ".json");
            }

            try {
                List<TableMappingRule> rules = getCurrentRules();
                String json = new GsonBuilder().setPrettyPrinting().create().toJson(rules);
                
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(json);
                }

                Messages.showInfoMessage(
                    MessagesBundle.message("table.mapping.export.success"),
                    MessagesBundle.message("table.mapping.export.success.title")
                );
            } catch (IOException ex) {
                Messages.showErrorDialog(
                    MessagesBundle.message("table.mapping.export.error", ex.getMessage()),
                    MessagesBundle.message("table.mapping.export.error.title")
                );
            }
        }
    }

    // 설정 불러오기
    private void importSettings() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(MessagesBundle.message("table.mapping.import.title"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON Files (*.json)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String json = Files.readString(fileChooser.getSelectedFile().toPath());
                List<TableMappingRule> rules = new Gson().fromJson(
                    json,
                    new TypeToken<List<TableMappingRule>>(){}.getType()
                );

                // 기존 데이터 삭제
                while (model.getRowCount() > 0) {
                    model.removeRow(0);
                }

                // 새로운 데이터 추가
                for (TableMappingRule rule : rules) {
                    model.addRow(new Object[]{
                        rule.isEnabled(),
                        rule.getText(),
                        rule.getMatchType(),
                        rule.getProvider(),
                        rule.getMethod(),
                        MessagesBundle.message("table.mapping.delete")
                    });
                }

                Messages.showInfoMessage(
                    MessagesBundle.message("table.mapping.import.success"),
                    MessagesBundle.message("table.mapping.import.success.title")
                );
            } catch (Exception ex) {
                Messages.showErrorDialog(
                    MessagesBundle.message("table.mapping.import.error", ex.getMessage()),
                    MessagesBundle.message("table.mapping.import.error.title")
                );
            }
        }
    }

    // 현재 규칙 목록 가져오기
    private List<TableMappingRule> getCurrentRules() {
        List<TableMappingRule> rules = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            TableMappingRule rule = new TableMappingRule();
            rule.setEnabled((Boolean) model.getValueAt(i, 0));
            rule.setText((String) model.getValueAt(i, 1));
            rule.setMatchType((TableMappingRule.MatchType) model.getValueAt(i, 2));
            rule.setProvider((String) model.getValueAt(i, 3));
            rule.setMethod((String) model.getValueAt(i, 4));
            rules.add(rule);
        }
        return rules;
    }

    // ButtonRenderer 클래스 추가
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            return this;
        }
    }

    // ButtonEditor 클래스 추가
    private static class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private final Consumer<Integer> onDelete;
        private int currentRow;

        public ButtonEditor(JButton button, Consumer<Integer> onDelete) {
            super(new JCheckBox());
            this.button = button;
            this.onDelete = onDelete;

            button.addActionListener(e -> {
                fireEditingStopped();
                onDelete.accept(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                   boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}