package com.github.timan1802.fakedatainsert;

import com.github.timan1802.fakedatainsert.constants.DataFakerConst;
import com.github.timan1802.fakedatainsert.utils.FakerUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.ide.util.PropertiesComponent;
import net.datafaker.Faker;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Faker 데이터 타입과 서브타입을 선택할 수 있는 패널
 * 테이블의 각 컬럼에 대한 데이터 생성 설정을 담당
 */
public class DataTypePanel extends JPanel {
    private static final int COMBO_BOX_WIDTH = 150;
    private static final int COMBO_BOX_HEIGHT = 25;
    private static final int VERTICAL_GAP = 2;
    private final DataFakerDialogJava dialog;    // 부모 다이얼로그 참조
    private final JTable table;                  // 부모 테이블 참조
    private final int columnIndex;               // 현재 패널의 컬럼 인덱스
    private JComboBox<String> typeBox;     // 메인 데이터 타입 선택
    private JComboBox<String> subTypeBox;  // 서브 타입 선택
    private Faker             faker;                         // 데이터 생성기


    /**
     * DataTypePanel 생성자
     *
     * @param availableTypes Faker에서 사용 가능한 데이터 타입 목록
     * @param faker Faker 인스턴스
     * @param table 부모 테이블
     * @param columnIndex 컬럼 인덱스
     * @param dialog 부모 다이얼로그
     */
    public DataTypePanel(String[] availableTypes, Faker faker, JTable table, int columnIndex, DataFakerDialogJava dialog) {
        this.dialog = dialog;
        this.faker = faker;
        this.table = table;
        this.columnIndex = columnIndex;
        
        initializeLayout();
        initializeComboBoxes(availableTypes);
        setupListeners();
        setupInitialState();
        checkAndSetColumnDefaults();
    }

    /**
     * 패널 레이아웃 초기화
     */
    private void initializeLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
    }

    /**
     * 콤보박스 초기화 및 설정
     */
    private void initializeComboBoxes(String[] availableTypes) {
        typeBox = createComboBox(availableTypes);
        subTypeBox = createComboBox(new String[]{});
        
        add(Box.createVerticalStrut(VERTICAL_GAP));
        add(typeBox);
        add(Box.createVerticalStrut(VERTICAL_GAP));
        add(subTypeBox);
        add(Box.createVerticalStrut(VERTICAL_GAP));
    }

    /**
     * 표준화된 콤보박스 생성
     */
    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(COMBO_BOX_WIDTH, COMBO_BOX_HEIGHT));
        comboBox.setMaximumSize(new Dimension(COMBO_BOX_WIDTH, COMBO_BOX_HEIGHT));
        return comboBox;
    }

    /**
     * 이벤트 리스너 설정
     */
    private void setupListeners() {
        typeBox.addActionListener(e -> {
            updateSubTypeBox();
            triggerSqlUpdate();
        });
        
        subTypeBox.addActionListener(e -> {
            updateTableValues();
            triggerSqlUpdate();
        });
    }

    /**
     * 초기 상태 설정
     */
    private void setupInitialState() {
        if (typeBox.getSelectedItem() != null) {
            updateSubTypeBox();
        }
    }

    /**
     * 컬럼 타입에 따른 Data Faker 콤보박스 기본값 설정
     */
    private void checkAndSetColumnDefaults() {
        String columnName = table.getColumnModel()
                                 .getColumn(columnIndex)
                                 .getHeaderValue()
                                 .toString()
                                 .toLowerCase();

        DasColumn column = getColumnFromDbTable(columnName);
        if (column == null) return;

        // 저장된 규칙 불러오기
        String savedRulesJson = PropertiesComponent.getInstance()
                                                   .getValue(DataFakerConst.TABLE_MAPPING_RULES);
        if (savedRulesJson == null) return;

        List<TableMappingRule> rules = new Gson().fromJson(
                savedRulesJson,
                new TypeToken<List<TableMappingRule>>(){}.getType()
        );

        // 규칙 적용
        for (TableMappingRule rule : rules) {
            if (!rule.isEnabled()) continue;

            boolean matches = switch (rule.getMatchType()) {
                case STARTS_WITH -> columnName.startsWith(rule.getText());
                case ENDS_WITH -> columnName.endsWith(rule.getText());
                case CONTAINS -> columnName.contains(rule.getText());
                case EQUALS -> columnName.equals(rule.getText());
            };

            if (matches) {
                setTypeAndSubType(rule.getProvider(), rule.getMethod());
                break;
            }
        }
    }

    /**
     * DbTable에서 컬럼 정보 조회
     */
    private DasColumn getColumnFromDbTable(String columnName) {
        for (DasObject col : dialog.getDbTable().getDasChildren(ObjectKind.COLUMN)) {
            if (col instanceof DasColumn && col.getName().equalsIgnoreCase(columnName)) {
                return (DasColumn) col;
            }
        }
        return null;
    }

    /**
     * 타입과 서브타입 설정
     */
    private void setTypeAndSubType(String type, String subType) {
        typeBox.setSelectedItem(type);
        updateSubTypeBox();
        subTypeBox.setSelectedItem(subType);
    }

    /**
     * 테이블 미리보기 값 업데이트
     */
    private void updateTableValues() {
        String providerName = getSelectedType();
        String methodName = getSelectedSubType();

        if (providerName != null && methodName != null) {
            for (int row = 1; row <= 2; row++) {
                Object value = FakerUtils.invokeProviderMethod(faker, providerName, methodName);
                table.setValueAt(value, row, columnIndex);
            }
            table.repaint();
        }
    }

    /**
     * 서브타입 콤보박스 업데이트
     */
    private void updateSubTypeBox() {
        String selectedProvider = getSelectedType();
        if (selectedProvider == null) return;

        Object currentSelection = subTypeBox.getSelectedItem();
        List<String> methodNames = FakerUtils.getProviderMethodNames(faker, selectedProvider);

        subTypeBox.removeAllItems();
        methodNames.stream()
                .sorted()
                .forEach(subTypeBox::addItem);

        if (currentSelection != null && methodNames.contains(currentSelection)) {
            subTypeBox.setSelectedItem(currentSelection);
        } else if (subTypeBox.getItemCount() > 0) {
            subTypeBox.setSelectedIndex(0);
        }
    }

    // Getter 메서드들
    public String getSelectedType() {
        return (String) typeBox.getSelectedItem();
    }

    public String getSelectedSubType() {
        return (String) subTypeBox.getSelectedItem();
    }

    /**
     * Faker 인스턴스 업데이트
     */
    public void updateFaker(Faker newFaker) {
        this.faker = newFaker;
        String currentType = getSelectedType();
        String currentSubType = getSelectedSubType();

        if (currentType != null) {
            updateSubTypeBox();
            typeBox.setSelectedItem(currentType);
            if (currentSubType != null) {
                subTypeBox.setSelectedItem(currentSubType);
            }
            updateTableValues();
        }
    }

    /**
     * SQL 업데이트 트리거
     */
    private void triggerSqlUpdate() {
        if (dialog != null) {
            dialog.updateSql();
        }
    }
}