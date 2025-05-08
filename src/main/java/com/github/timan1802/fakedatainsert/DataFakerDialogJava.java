package com.github.timan1802.fakedatainsert;

import com.github.timan1802.fakedatainsert.utils.FakerUtils;
import com.intellij.database.Dbms;
import com.intellij.database.model.DasNamed;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import net.datafaker.Faker;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.Schema;
import net.datafaker.transformations.sql.SqlDialect;
import net.datafaker.transformations.sql.SqlTransformer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static net.datafaker.transformations.Field.field;

/**
 * 가짜 데이터 삽입을 위한 대화상자 클래스입니다.
 * 데이터베이스 테이블에 대한 테스트 데이터를 생성하고 SQL 문을 만들어내는 기능을 제공합니다.
 */
public class DataFakerDialogJava extends DialogWrapper {
    // UI 컴포넌트 크기 상수
    private static final int DEFAULT_ROW_COUNT = 100;  // 기본 생성 행 수
    private static final int HEADER_HEIGHT = 30;       // 헤더 높이
    private static final int FIRST_ROW_HEIGHT = 60;    // 첫 번째 행 높이 (데이터 타입 선택 패널용)
    private static final int DATA_ROW_HEIGHT = 30;     // 데이터 행 높이
    private static final int DEFAULT_COLUMN_WIDTH = 200; // 기본 컬럼 너비
    private static final int DIALOG_WIDTH = 800;       // 대화상자 너비
    private static final int DIALOG_HEIGHT = 600;      // 대화상자 높이
    private static final int TABLE_HEIGHT = 300;       // 테이블 높이
    private static final int DIVIDER_LOCATION = 200;   // 분할창 구분자 위치

    // UI 컴포넌트
    private final DefaultTableModel tableModel;        // 테이블 데이터 모델
    private final JBTable table;                       // 데이터 미리보기 테이블
    private final DbTable dbTable;                     // 대상 데이터베이스 테이블
    private final JTextArea sqlTextArea;               // SQL 출력 영역
    private Faker faker;                               // 가짜 데이터 생성기
    private JComboBox<FakerDataLocaleType> countryComboBox; // 국가/언어 선택 콤보박스
    private JTextField countField;                     // 생성할 데이터 개수 입력 필드

    // 클래스 필드에 추가
    private CheckBoxHeaderRenderer[] checkBoxHeaders;

    /**
     * 대화상자 생성자
     * @param dbTable 대상 데이터베이스 테이블
     */
    public DataFakerDialogJava(DbTable dbTable) {
        super(true); // 모달 대화상자로 생성
        this.dbTable = dbTable;
        this.tableModel = new DefaultTableModel();
        this.table = new JBTable(tableModel);
        this.sqlTextArea = createSqlTextArea();

        init();
        setTitle("Fake Data Insert for " + dbTable.getName());
    }

    /**
     * 대화상자의 중앙 패널을 생성합니다.
     * @return 생성된 중앙 패널
     */
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));

        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createSplitPane(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(new JButton("Copy SQL to Clipboard"));
        buttonPanel.add(new JButton("DB Insert Execute"));
        return buttonPanel;
    }

    /**
     * 상단 패널을 생성합니다. 데이터 생성 개수와 국가 선택 컴포넌트를 포함합니다.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createGridBagConstraints();

        addCountComponents(topPanel, gbc);
        addCountryComponents(topPanel, gbc);

        return topPanel;
    }

    /**
     * GridBagConstraints 객체를 생성하고 기본 설정을 적용합니다.
     */
    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    /**
     * 데이터 생성 개수 관련 컴포넌트를 추가합니다.
     */
    private void addCountComponents(JPanel panel, GridBagConstraints gbc) {
        JLabel countLabel = new JLabel("Number of data to generate"); //생성할 데이터 개수
        countField = new JBTextField(String.valueOf(DEFAULT_ROW_COUNT), 10);
        
        // 실시간 업데이트를 위한 문서 리스너 추가
        countField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateSqlIfValid() {
                try {
                    String text = countField.getText().trim();
                    if (!text.isEmpty()) {
                        int count = Integer.parseInt(text);
                        if (count > 0) {
                            updateSql();
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // 유효하지 않은 입력은 무시
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSqlIfValid();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSqlIfValid();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSqlIfValid();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(countLabel, gbc);
        gbc.gridx = 1;
        panel.add(countField, gbc);
    }

    /**
     * 국가 선택 관련 컴포넌트를 추가합니다.
     */
    private void addCountryComponents(JPanel panel, GridBagConstraints gbc) {
        JLabel countryLabel = new JLabel("Country");
        countryComboBox = new ComboBox<>(FakerDataLocaleType.values());
        setupCountryComboBox();

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(countryLabel, gbc);
        gbc.gridx = 1;
        panel.add(countryComboBox, gbc);
    }

    /**
     * 국가 선택 콤보박스를 초기화하고 설정합니다.
     */
    private void setupCountryComboBox() {
        countryComboBox.setSelectedItem(FakerDataLocaleType.KO_KR);
        countryComboBox.setRenderer(createCountryComboBoxRenderer());
        countryComboBox.addActionListener(e -> updateFakerLocale());
        updateFakerLocale();
    }

    /**
     * 국가 선택 콤보박스를 위한 셀 렌더러를 생성합니다.
     * FakerDataLocaleType enum을 표시하기 위한 커스텀 렌더러입니다.
     *
     * @return ListCellRenderer<FakerDataLocaleType> 커스텀된 콤보박스 셀 렌더러
     */
    private ListCellRenderer<FakerDataLocaleType> createCountryComboBoxRenderer() {
        return new ListCellRenderer<FakerDataLocaleType>() {
            // 기본 렌더링을 위한 DefaultListCellRenderer 인스턴스
            private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            /**
             * 콤보박스의 각 항목을 렌더링하는 메서드
             * 
             * @param list 렌더링 중인 JList 컴포넌트
             * @param value 현재 렌더링할 FakerDataLocaleType 값
             * @param index 현재 항목의 인덱스
             * @param isSelected 항목이 선택되었는지 여부
             * @param cellHasFocus 셀에 포커스가 있는지 여부
             * @return Component 렌더링된 리스트 항목 컴포넌트
             */
            @Override
            public Component getListCellRendererComponent(JList<? extends FakerDataLocaleType> list,
                                                        FakerDataLocaleType value,
                                                        int index,
                                                        boolean isSelected,
                                                        boolean cellHasFocus) {
                // 기본 렌더러를 사용하여 기본 스타일링 적용
                JLabel label = (JLabel) defaultRenderer.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                
                // value가 null이 아닌 경우에만 텍스트 설정
                if (value != null) {
                    String desc = value.getDescription();
                    // 국가 코드와 설명을 함께 표시 (설명이 있는 경우에만)
                    label.setText(value.getCode() + (desc != null && !desc.isEmpty() ? " - " + desc : ""));
                }
                
                return label;
            }
        };
    }



    private JSplitPane createSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(createTablePanel());
        splitPane.setBottomComponent(new JBScrollPane(sqlTextArea));
        splitPane.setDividerLocation(DIVIDER_LOCATION);
        splitPane.setEnabled(true);
        return splitPane;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        JBScrollPane tableScrollPane = new JBScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(DIALOG_WIDTH, TABLE_HEIGHT));
        setupTable();
        tablePanel.add(tableScrollPane);
        return tablePanel;
    }

    /**
     * SQL 미리보기를 위한 텍스트 영역을 생성합니다.
     */
    private JTextArea createSqlTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setRows(5);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return textArea;
    }

    /**
     * 선택된 로케일에 따라 Faker 인스턴스를 업데이트합니다.
     */
    private void updateFakerLocale() {
        FakerDataLocaleType selectedLocale = (FakerDataLocaleType) countryComboBox.getSelectedItem();
        if (selectedLocale != null) {
            // 새로운 Faker 인스턴스 생성
            faker = new Faker(Locale.forLanguageTag(selectedLocale.getCode()));

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

    /**
     * 현재 설정된 데이터 타입에 따라 SQL 문을 생성하고 업데이트합니다.
     */
    public void updateSql() {
        try {
            List<Field<String, String>> fields = new ArrayList<>();

            // 각 컬럼에 대해
            for (int col = 0; col < table.getColumnCount(); col++) {
                // 체크되지 않은 컬럼은 건너뛰기
                if (checkBoxHeaders[col].isNotColumnChecked()) {
                    continue;
                }

                Object value = table.getValueAt(0, col);
                if (value instanceof DataTypePanel panel) {
                    String columnName = table.getColumnModel().getColumn(col).getHeaderValue().toString();
                    String provider = panel.getSelectedType();
                    String subType = panel.getSelectedSubType();

                    if (provider != null && subType != null) {
                        fields.add(field(columnName, () -> {
                            try {
                                return String.valueOf(FakerUtils.invokeProviderMethod(faker, provider, subType));
                            } catch (Exception e) {
                                return "";
                            }
                        }));
                    }
                }
            }

            // 스키마 생성
            Schema<String, String> schema = Schema.of(fields.toArray(new Field[0]));

            // DB 종류 확인 및 SqlDialect 설정
            Dbms dbms = dbTable.getDataSource().getDbms();
            SqlDialect sqlDialect = DbmsDialectMapper.getSqlDialectOrNull(dbms);

            if (sqlDialect == null) {
                sqlDialect = SqlDialect.MYSQL; // 기본값
                Messages.showWarningDialog(
                        MessagesBundle.message("unsupported.database.type", dbms.getDisplayName()),
                        "Database Type Warning"
                );
            }

            SqlTransformer<String> transformer =
                    new SqlTransformer.SqlTransformerBuilder<String>()
                            .batch(5)
                            .tableName(dbTable.getName())
                            .dialect(sqlDialect)
                            .build();

            // countField의 값을 정수로 변환하여 사용
            int count;
            try {
                count = Integer.parseInt(countField.getText().trim());
            } catch (NumberFormatException e) {
                //생성할 데이터 개수는 숫자여야 합니다.
                Messages.showErrorDialog(MessagesBundle.message("must.be.number"), "INPUT ERROR");
                return;
            }

            // SQL 생성
            String output = transformer.generate(schema, count);

            // SQL 텍스트 영역 업데이트
            sqlTextArea.setText(output);

        } catch (Exception e) {
            //SQL 생성 중 오류 발생
            sqlTextArea.setText(MessagesBundle.message("error.occurs.during.sql.creation", e.getMessage()));
        }
    }

    public DasObject getDbTable() {
        return dbTable;
    }


    /**
     * 테이블 설정을 초기화하고 구성합니다.
     * - 컬럼 설정
     * - 데이터 타입 선택 패널 추가
     * - 렌더러와 에디터 설정
     */
    private void setupTable() {
        // ✅ DbTable에서 컬럼명 추출
        List<String> columnNames = dbTable.getDasChildren(ObjectKind.COLUMN).map(DasNamed::getName).toList();

        // Faker의 Provider 목록을 가져와서 availableDataTypes로 사용
        String[] availableDataTypes = FakerUtils.getAllProviderNames(faker).stream().sorted()
                .toArray(String[]::new);

        // TableModel을 수정하여 JComboBox를 제대로 처리하도록 함
        DefaultTableModel tableModel = new DefaultTableModel();

        // 컬럼명 설정
        for (String columnName : columnNames) {
            tableModel.addColumn(columnName);
        }

        // 먼저 테이블 모델 설정
        table.setModel(tableModel);

        // ✅ checkBoxHeaders 배열 초기화 - 여기로 이동
        checkBoxHeaders = new CheckBoxHeaderRenderer[table.getColumnCount()];
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);
            checkBoxHeaders[col] = new CheckBoxHeaderRenderer(col);
            column.setHeaderRenderer(checkBoxHeaders[col]);
        }

        // 3개의 빈 행 추가 (DataTypePanel용 1행 + 샘플 데이터용 2행)
        tableModel.addRow(new Object[columnNames.size()]);  // 첫 번째 행
        tableModel.addRow(new Object[columnNames.size()]);  // 두 번째 행
        tableModel.addRow(new Object[columnNames.size()]);  // 세 번째 행

        // 테이블 행 높이 설정
        table.setRowHeight(0, 60);  // 첫 번째 행 높이를 더 크게 설정
        table.setRowHeight(1, 30);  // 두 번째 행
        table.setRowHeight(2, 30);  // 세 번째 행

        // 테이블 헤더 높이 설정
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 30));

        // 첫 번째 행에 DataTypePanel 추가
        for (int col = 0; col < columnNames.size(); col++) {
            DataTypePanel panel = new DataTypePanel(availableDataTypes, faker, table, col, this);
            table.setValueAt(panel, 0, col);
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
                    if (editor instanceof final DataTypePanel panel) {
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

        // 테이블 헤더 클릭 이벤트 처리
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col >= 0 && col < checkBoxHeaders.length) {
                    Rectangle headerRect = table.getTableHeader().getHeaderRect(col);
                    // 체크박스 영역 클릭 확인 (왼쪽 20픽셀 정도)
                    if (e.getX() - headerRect.x < 20) {
                        checkBoxHeaders[col].setChecked(checkBoxHeaders[col].isNotColumnChecked());
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
        setDefaultLocaleByUserLanguage();
    }

    
    private class CheckBoxHeaderRenderer extends JCheckBox implements TableCellRenderer {
        private final int column;
        private boolean isChecked;

        public CheckBoxHeaderRenderer(int column) {
            this.column = column;
            this.isChecked = true; // 기본값 true로 설정
            setHorizontalAlignment(JLabel.LEFT);
            setBorderPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int col) {
            if (value != null) {
                setText(value.toString());
            }
            setSelected(isChecked);
            setBackground(table.getTableHeader().getBackground());
            setForeground(table.getTableHeader().getForeground());
            setFont(table.getTableHeader().getFont());
            
            return this;
        }

        public boolean isColumnChecked() {
            return isChecked;
        }

        public boolean isNotColumnChecked() {
            return !isChecked;
        }

        public void setChecked(boolean checked) {
            if (this.isChecked != checked) {
                this.isChecked = checked;
                updateColumnState();
                updateSql();
            }
        }

        private void updateColumnState() {
            // 열의 모든 셀 비활성화/활성화
            if (table != null) {
                for (int row = 0; row < table.getRowCount(); row++) {
                    Object value = table.getValueAt(row, column);
                    if (value instanceof DataTypePanel panel) {
                        panel.setEnabled(isChecked);
                    }
                }
                table.getTableHeader().repaint();
                table.repaint();
            }
        }
    }
    @Override
    protected JComponent createSouthPanel() {
        JPanel southPanel = (JPanel) super.createSouthPanel();
        
        // Copy SQL 버튼 생성
        JButton copyButton = new JButton("Copy SQL to Clipboard");
        copyButton.addActionListener(e -> {
            try {
                // 클립보드에 SQL 텍스트 복사
                StringSelection selection = new StringSelection(sqlTextArea.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                
                // 복사 성공 메시지 표시
                Messages.showInfoMessage(MessagesBundle.message("sql.copied.to.clipboard")
                        , MessagesBundle.message("sql.copied.completion"));
            } catch (Exception ex) {
                Messages.showErrorDialog(MessagesBundle.message("sql.copied.error.message", ex.getMessage()), MessagesBundle.message("sql.copied.error"));
            }
        });
        
        // 버튼을 OK 버튼 앞에 추가
        if (southPanel != null) {
            southPanel.add(copyButton, BorderLayout.WEST);
        }
        
        return southPanel;
    }
    
    private void setDefaultLocaleByUserLanguage() {
        // IntelliJ IDEA의 현재 UI 언어 설정 가져오기
        String currentLanguage = com.intellij.DynamicBundle.getLocale().getLanguage();
        
        // 한국어인 경우 ('ko')
        if ("ko".equals(currentLanguage)) {
            countryComboBox.setSelectedItem(FakerDataLocaleType.KO_KR);
        } else {
            // 그 외의 경우 영어로 설정
            countryComboBox.setSelectedItem(FakerDataLocaleType.EN_US);
        }
    }
}