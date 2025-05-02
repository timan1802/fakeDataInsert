package com.github.timan1802.fakedatainsert

import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbTable
import com.intellij.openapi.ui.DialogWrapper
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

class DataFakerDialog(private val dbTable: DbTable) : DialogWrapper(true) {

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)

    init {
        init()
        title = "Fake Data Insert for ${dbTable.name}"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())

        val topPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = Insets(5, 5, 5, 5)
            anchor = GridBagConstraints.WEST
        }

        val countLabel = JLabel("생성할 데이터 개수")
        val countField = JTextField("100", 10)

        val countryLabel = JLabel("국가")
        val countryComboBox = JComboBox(arrayOf("KO", "US", "JP", "CN"))

        gbc.gridx = 0
        gbc.gridy = 0
        topPanel.add(countLabel, gbc)
        gbc.gridx = 1
        topPanel.add(countField, gbc)

        gbc.gridx = 0
        gbc.gridy = 1
        topPanel.add(countryLabel, gbc)
        gbc.gridx = 1
        topPanel.add(countryComboBox, gbc)

        // ✅ DbTable에서 컬럼명 추출
        val columnNames = dbTable.getDasChildren(ObjectKind.COLUMN)
            .map { it.name }
            .toList()

// 사용 가능한 데이터 타입 정의
        val availableDataTypes = arrayOf(
            "이름", "성", "이메일", "전화번호", "주소", "도시",
            "회사명", "직업", "생년월일", "나이", "신용카드",
            "숫자", "문자열", "날짜", "불리언"
        )

// TableModel을 수정하여 JComboBox를 제대로 처리하도록 함
        val customTableModel = object : DefaultTableModel() {
            override fun getColumnClass(columnIndex: Int): Class<*> {
                return if (getRowCount() > 0 && getValueAt(0, columnIndex) is JComboBox<*>) {
                    JComboBox::class.java
                } else {
                    super.getColumnClass(columnIndex)
                }
            }

            override fun isCellEditable(row: Int, column: Int): Boolean {
                return row == 0  // 첫 번째 행만 편집 가능하도록 설정
            }
        }

// TableModel 설정
        table.model = customTableModel

// 컬럼명 설정
        columnNames.forEach { customTableModel.addColumn(it) }

// 콤보박스로 첫 번째 행 추가
        val dataTypeRow = columnNames.map {
            JComboBox(availableDataTypes).apply {
                selectedIndex = 0
            }
        }.toTypedArray()
        customTableModel.addRow(dataTypeRow)

// 콤보박스 렌더러와 에디터 설정
        table.getColumnModel().columns.toList().forEach { column ->
            column.cellRenderer = object : TableCellRenderer {
                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    return (value as? JComboBox<*>)?.apply {
                        if (isSelected) {
                            background = table.selectionBackground
                            foreground = table.selectionForeground
                        } else {
                            background = table.background
                            foreground = table.foreground
                        }
                    } ?: JLabel(value?.toString() ?: "")
                }
            }

            column.cellEditor = object : DefaultCellEditor(JComboBox(availableDataTypes)) {
                override fun getTableCellEditorComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    val combo = super.getTableCellEditorComponent(table, value, isSelected, row, column) as JComboBox<*>
                    if (value is JComboBox<*>) {
                        combo.selectedItem = value.selectedItem
                    }
                    return combo
                }
            }
        }

// 첫 번째 행의 높이를 조절
        table.setRowHeight(0, 25)

        // 예시 데이터 추가 (원하면 제거 가능)
        if (columnNames.size >= 4) {
            tableModel.addRow(arrayOf("Alfreds Futterkiste", "Maria Anders", "Berlin", "030-0074321"))
            tableModel.addRow(arrayOf("Antonio Moreno Taquería", "Antonio Moreno", "México D.F.", "(5) 555-3932"))
        }

        table.preferredScrollableViewportSize = Dimension(
            120 * columnNames.size.coerceAtMost(5), // 컬럼 수 따라 넓이 증감(가변 처리)
            25 * 4
        )
        val tableScrollPane = JScrollPane(table)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        buttonPanel.add(JButton("Insert"))
        buttonPanel.add(JButton("Cancel"))

        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(tableScrollPane, BorderLayout.CENTER)
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }
}