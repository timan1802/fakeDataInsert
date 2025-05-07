package com.github.timan1802.fakedatainsert;

import com.intellij.database.model.DasNamed;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import net.datafaker.Faker;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.Schema;
import net.datafaker.transformations.sql.SqlDialect;
import net.datafaker.transformations.sql.SqlTransformer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.datafaker.transformations.Field.field;

public class DataFakerDialogJava extends DialogWrapper {
    private final DefaultTableModel              tableModel = new DefaultTableModel();
    private final JBTable                        table      = new JBTable(tableModel);
    private final DbTable                        dbTable;
    private final JTextArea                      sqlTextArea;
    private       Faker                          faker;
    private       JComboBox<FakerDataLocaleType> countryComboBox;


    public DataFakerDialogJava(DbTable dbTable) {
        super(true);
        this.dbTable = dbTable;
        this.sqlTextArea = new JTextArea();


        init();
        setTitle("Fake Data Insert for " + dbTable.getName());
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel             topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc      = new GridBagConstraints();
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel     countLabel = new JLabel("생성할 데이터 개수");
        JTextField countField = new JTextField("100", 10);

        JLabel countryLabel = new JLabel("국가");
        //        ComboBox<String> countryComboBox = new ComboBox<>(new String[]{"KO", "US", "JP", "CN"});
        ComboBox<FakerDataLocaleType> countryComboBox = new ComboBox<>(FakerDataLocaleType.values());

        // 기본값 설정 (한국어)
        countryComboBox.setSelectedItem(FakerDataLocaleType.KO_KR);
        faker = new Faker(new Locale(FakerDataLocaleType.KO_KR.getCode()));

        // 콤보박스 선택 이벤트 처리
        countryComboBox.addActionListener(e -> {
            FakerDataLocaleType selectedLocale = (FakerDataLocaleType) countryComboBox.getSelectedItem();
            if (selectedLocale != null) {
                updateFaker(selectedLocale);
            }
        });


        // 콤보박스에 보이는 값을 좀 더 보기 좋게 하려면 렌더러 추가(선택)
        // (국가 코드+설명 함께 출력)
        countryComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list,
                                                                           value,
                                                                           index,
                                                                           isSelected,
                                                                           cellHasFocus);
                if (value instanceof FakerDataLocaleType fakerLocale) {
                    String desc = fakerLocale.getDescription();
                    label.setText(fakerLocale.getCode() + (desc != null && !desc.isEmpty() ? " - " + desc : ""));
                }
                return label;
            }
        });

        System.out.printf("Faker Test: %s\n", faker.name().fullName());


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
        List<String> columnNames = dbTable.getDasChildren(ObjectKind.COLUMN).map(DasNamed::getName).toList();


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

        //테이블 설정 부분
        setupTable();

        //테이블 가로 크기 고정. // 컬럼 수 따라 넓이 증감(가변 처리)
        table.setPreferredScrollableViewportSize(new Dimension(200 * Math.min(columnNames.size(), 10), 500));

        // 예시 데이터 추가 (원하면 제거 가능)
        if (columnNames.size() >= 4) {
            customTableModel.addRow(new Object[]{"Alfreds Futterkiste", "Maria Anders", "Berlin", "030-0074321"});
            customTableModel.addRow(new Object[]{"Antonio Moreno Taquería", "Antonio Moreno", "México D.F.",
                                                 "(5) 555-3932"});
        }

        JBScrollPane tableScrollPane = new JBScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(-1, 400)); // 테이블 높이 고정

        // SQL 텍스트영역 설정 및 배치
        sqlTextArea.setRows(5);
        sqlTextArea.setEditable(false);
        sqlTextArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        sqlTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 텍스트 여백
        sqlTextArea.append("Sql Query");
        JScrollPane textScrollPane = new JScrollPane(sqlTextArea);


        //        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //        buttonPanel.add(new JButton("Insert"));
        //        buttonPanel.add(new JButton("Cancel"));
        //        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(textScrollPane, BorderLayout.SOUTH);


        // 전체 패널 크기 설정
        mainPanel.setPreferredSize(new Dimension(800, 600));
        return mainPanel;
    }

    // Faker 업데이트 메서드
    private void updateFaker(FakerDataLocaleType locale) {
        try {
            faker = new Faker(new Locale(locale.getCode()));
            // 필요한 경우 테이블 데이터 갱신
            refreshTableData();
            // 테이블 재설정 (새로운 Faker의 Provider 목록으로 갱신)
            setupTable();
        } catch (Exception e) {
            // 에러 처리
            Messages.showErrorDialog("로케일 '" + locale.getCode() + "' 설정 중 오류가 발생했습니다.", "Faker 초기화 오류");
        }
    }

    // 테이블 데이터 갱신 메서드 (필요한 경우 구현)
    private void refreshTableData() {
        // 테이블의 데이터를 새로운 Faker 인스턴스를 사용하여 갱신
        // 예: 미리보기 데이터 등을 갱신
        System.out.printf("Faker Test: %s\n", faker.name().fullName());
    }

    private void setupTable() {
        // ✅ DbTable에서 컬럼명 추출
        List<String> columnNames = dbTable.getDasChildren(ObjectKind.COLUMN).map(DasNamed::getName).toList();

        // Faker의 Provider 목록을 가져와서 availableDataTypes로 사용
        String[] availableDataTypes = FakerUtils.getAllProviderNames(faker).stream().sorted()  // 알파벳 순으로 정렬
                                                .toArray(String[]::new)
                ;

        // TableModel을 수정하여 JComboBox를 제대로 처리하도록 함
        DefaultTableModel tableModel = new DefaultTableModel();

        // 컬럼명 설정
        for (String columnName : columnNames) {
            tableModel.addColumn(columnName);
        }

        // 먼저 테이블 모델 설정
        table.setModel(tableModel);

        // 3개의 빈 행 추가 (DataTypePanel용 1행 + 샘플 데이터용 2행)
        tableModel.addRow(new Object[columnNames.size()]);  // 첫 번째 행
        tableModel.addRow(new Object[columnNames.size()]);  // 두 번째 행
        tableModel.addRow(new Object[columnNames.size()]);  // 세 번째 행

        // 테이블 행 높이 설정
        table.setRowHeight(0, 60);  // 첫 번째 행 높이를 더 크게 설정
        table.setRowHeight(1, 30);  // 두 번째 행
        table.setRowHeight(2, 30);  // 세 번째 행


        // 테이블 헤더 높이 설정 (선택사항)
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 30));

        // 첫 번째 행에 DataTypePanel 추가
        for (int col = 0; col < columnNames.size(); col++) {
            DataTypePanel panel = new DataTypePanel(availableDataTypes, faker, table, col, this);
            table.setValueAt(panel, 0, col);  // 직접 setValueAt 사용
        }

        // 한 번 클릭으로 편집 모드 시작하도록 설정
        table.putClientProperty("JTable.autoStartsEdit", Boolean.TRUE);

        // 클릭 동작 수정
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row == 0) {  // 첫 번째 행인 경우에만
                    table.editCellAt(row, col);
                    Component editor = table.getEditorComponent();
                    if (editor instanceof DataTypePanel) {
                        DataTypePanel panel = (DataTypePanel) editor;
                        // 클릭된 위치에 따라 적절한 콤보박스 표시
                        Point p = e.getPoint();
                        p.y = 0;  // 패널 내 y좌표 조정
                        Component clicked = panel.getComponentAt(p);
                        if (clicked instanceof JComboBox) {
                            ((JComboBox<?>) clicked).showPopup();
                        }
                    }
                }
            }
        });

        // 각 컬럼에 대한 렌더러와 에디터 설정
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);

            // 렌더러 설정
            column.setCellRenderer((table, value, isSelected, hasFocus, row, column1) -> {
                if (row == 0 && value instanceof DataTypePanel panel) {
                    if (isSelected) {
                        panel.setBackground(table.getSelectionBackground());
                        panel.setForeground(table.getSelectionForeground());
                    } else {
                        panel.setBackground(table.getBackground());
                        panel.setForeground(table.getForeground());
                    }
                    return panel;
                }

                // 다른 행들은 기본 렌더링
                JLabel label = new JLabel(value != null ? value.toString() : "");
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                    label.setOpaque(true);
                }
                return label;
            });

            // 에디터 설정
            column.setCellEditor(new DefaultCellEditor(new JTextField()) {
                private DataTypePanel panel;

                @Override
                public Component getTableCellEditorComponent(JTable table,
                                                             Object value,
                                                             boolean isSelected,
                                                             int row,
                                                             int column) {
                    if (row == 0) {
                        if (value instanceof DataTypePanel) {
                            panel = (DataTypePanel) value;
                        } else {
                            // 새 패널 생성이 필요한 경우
                            panel = new DataTypePanel(availableDataTypes, faker, table, column, DataFakerDialogJava.this);
                        }
                        if (isSelected) {
                            panel.setBackground(table.getSelectionBackground());
                            panel.setForeground(table.getSelectionForeground());
                        }
                        return panel;
                    }
                    // 다른 행들
                    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
                }

                @Override
                public Object getCellEditorValue() {
                    return panel;
                }

                @Override
                public boolean stopCellEditing() {
                    fireEditingStopped();
                    return super.stopCellEditing();
                }
            });

            // 컬럼 너비 설정
            column.setPreferredWidth(200);  // 두 콤보박스가 들어갈 수 있는 충분한 너비
        }
    }

    private void createUIComponents() {
        // 국가 콤보박스 초기화
        countryComboBox = new JComboBox<>(FakerDataLocaleType.values());
        countryComboBox.setSelectedItem(FakerDataLocaleType.KO_KR); // 기본값 설정

        // locale 변경 이벤트 처리
        countryComboBox.addActionListener(e -> updateFakerLocale());

        // 초기 Faker 인스턴스 생성
        updateFakerLocale();
    }

    private void updateFakerLocale() {
        FakerDataLocaleType selectedLocale = (FakerDataLocaleType) countryComboBox.getSelectedItem();
        if (selectedLocale != null) {
            // 새로운 Faker 인스턴스 생성
            faker = new Faker(new Locale(selectedLocale.getCode()));

            // 테이블이 있고 모델이 있는 경우에만 업데이트
            if (table.getModel() != null) {
                // 모든 DataTypePanel에 새로운 Faker 인스턴스 전달
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object value = table.getValueAt(0, col);
                    if (value instanceof DataTypePanel) {
                        ((DataTypePanel) value).updateFaker(faker);
                    }
                }
            }
        }
    }

    public void updateSql() {
        try {
            List<Field<String, String>> fields = new ArrayList<>();

            // 각 컬럼에 대해
            for (int col = 0; col < table.getColumnCount(); col++) {
                Object value = table.getValueAt(0, col);
                if (value instanceof DataTypePanel panel) {
                    String columnName = table.getColumnModel().getColumn(col).getHeaderValue().toString();
                    String provider = panel.getSelectedType();
                    String subType = panel.getSelectedSubType();

                    if (provider != null && subType != null) {
                        fields.add(field(columnName, () -> {
                            try {
                                Object providerInstance = faker.getClass().getMethod(provider).invoke(faker);
                                Method method = providerInstance.getClass().getMethod(subType);
                                return String.valueOf(method.invoke(providerInstance));
                            } catch (Exception e) {
                                return "";
                            }
                        }));
                    }
                }
            }

            // 스키마 생성
            Schema<String, String> schema = Schema.of(fields.toArray(new Field[0]));

            // SQL 변환기 생성
            SqlTransformer<String> transformer =
                    new SqlTransformer.SqlTransformerBuilder<String>()
                            .batch(5)
                            .tableName(dbTable.getName())
                            .dialect(SqlDialect.POSTGRES)
                            .build();

            // SQL 생성
            String output = transformer.generate(schema, 10);

            // SQL 텍스트 영역 업데이트
            sqlTextArea.setText(output);

        } catch (Exception e) {
            sqlTextArea.setText("SQL 생성 중 오류 발생: " + e.getMessage());
        }
    }
}