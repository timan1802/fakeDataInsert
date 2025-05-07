package com.github.timan1802.fakedatainsert;

import net.datafaker.Faker;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// 두 콤보박스를 포함할 패널 클래스
public class DataTypePanel extends JPanel {
    private final DataFakerDialogJava dialog;
    private final JComboBox<String> typeBox;
    private final JComboBox<String> subTypeBox;
    private       Faker             faker;
    private       JTable            table;  // 테이블 참조 추가
    private int column;    // 현재 패널의 열 위치

    public DataTypePanel(String[] availableTypes, Faker faker, JTable table, int column, DataFakerDialogJava dialog) {
        this.dialog = dialog;
        this.faker = faker;
        this.table = table;
        this.column = column;
        
        // BoxLayout으로 변경하여 컴포넌트들이 세로로 쌓이도록 함
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        typeBox = new JComboBox<>(availableTypes);
        subTypeBox = new JComboBox<>();
        
        // 기본 크기 설정
        typeBox.setPreferredSize(new Dimension(150, 25));
        typeBox.setMaximumSize(new Dimension(150, 25));  // BoxLayout에서 중요
        
        subTypeBox.setPreferredSize(new Dimension(150, 25));
        subTypeBox.setMaximumSize(new Dimension(150, 25));  // BoxLayout에서 중요
        
        // 이벤트 리스너 추가
        typeBox.addActionListener(e -> updateSubTypeBox());
        subTypeBox.addActionListener(e -> updateTableValues());
        
        // 약간의 여백 추가
        add(Box.createVerticalStrut(2));  // 상단 여백
        add(typeBox);
        add(Box.createVerticalStrut(2));  // 콤보박스 사이 여백
        add(subTypeBox);
        add(Box.createVerticalStrut(2));  // 하단 여백
        
        setOpaque(true);

        if (typeBox.getSelectedItem() != null) {
            updateSubTypeBox();
        }

        // Provider 선택 시 SubType 업데이트
        typeBox.addActionListener(e -> updateSubTypes());

        // SubType 선택 시 SQL 업데이트
        subTypeBox.addActionListener(e -> triggerSqlUpdate());

    }

    private void updateTableValues() {
        String providerName = getSelectedType();
        String methodName = getSelectedSubType();

        if (providerName != null && methodName != null && table != null) {
            // 샘플 값 5개 생성하여 테이블에 설정
            for (int i = 1; i <= 2; i++) {  // 2행과 3행에 대해
                Object value = FakerUtils.invokeProviderMethod(faker, providerName, methodName);
                table.setValueAt(value, i, column);  // i행, column열에 값 설정
            }
            table.repaint();  // 테이블 갱신
        }
    }

    private void updateSubTypeBox() {
        String selectedProvider = (String) typeBox.getSelectedItem();
        if (selectedProvider != null) {
            // 현재 선택된 값 저장
            Object currentSelection = subTypeBox.getSelectedItem();

            // 콤보박스 아이템 업데이트
            subTypeBox.removeAllItems();

            // 선택된 Provider의 메서드 목록 가져오기
            List<String> methodNames = FakerUtils.getProviderMethodNames(faker, selectedProvider);

            // 정렬하여 콤보박스에 추가
            methodNames.stream()
                    .sorted()
                    .forEach(subTypeBox::addItem);

            // 이전 선택값이 있고 새 목록에도 있다면 다시 선택
            if (currentSelection != null && methodNames.contains(currentSelection)) {
                subTypeBox.setSelectedItem(currentSelection);
            } else if (subTypeBox.getItemCount() > 0) {
                subTypeBox.setSelectedIndex(0);  // 첫 번째 항목 선택
            }
        }
    }

    public String getSelectedType() {
        return (String) typeBox.getSelectedItem();
    }

    public void setSelectedType(String type) {
        typeBox.setSelectedItem(type);
    }

    public String getSelectedSubType() {
        return (String) subTypeBox.getSelectedItem();
    }

    public void setSelectedSubType(String type) {
        subTypeBox.setSelectedItem(type);
    }

    public void updateFaker(Faker newFaker) {
        this.faker = newFaker;
        // Provider 목록 업데이트가 필요한 경우 여기에 추가

        // 현재 선택된 값들 저장
        String currentType = getSelectedType();
        String currentSubType = getSelectedSubType();

        // 필요한 경우 콤보박스 업데이트
        if (currentType != null) {
            updateSubTypeBox();

            // 이전 선택값 복원
            setSelectedType(currentType);
            if (currentSubType != null) {
                setSelectedSubType(currentSubType);
            }

            // 테이블 값 업데이트
            updateTableValues();
        }
    }

    private void updateSubTypes() {
        String selectedProvider = (String) typeBox.getSelectedItem();
        if (selectedProvider != null) {
            List<String> methodNames = FakerUtils.getProviderMethodNames(faker, selectedProvider);
            subTypeBox.setModel(new DefaultComboBoxModel<>(methodNames.toArray(new String[0])));
        }
    }


    
    private void updateSql() {
//        updateDialogSql();
    }

    // updateSql을 호출하는 메서드
    private void triggerSqlUpdate() {
        if (dialog != null) {
            dialog.updateSql();
        }
    }





}