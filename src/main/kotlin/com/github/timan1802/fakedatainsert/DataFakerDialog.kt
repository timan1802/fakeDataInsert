package com.github.timan1802.fakedatainsert

import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbTable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.table.JBTable
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

class DataFakerDialog(private val dbTable: DbTable) : DialogWrapper(true) {

    private val tableModel = DefaultTableModel()
    private val table = JBTable(tableModel)

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
        val countryComboBox = ComboBox(arrayOf("KO", "US", "JP", "CN"))

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

        val columnNames = dbTable.getDasChildren(ObjectKind.COLUMN)
            .map { it.name }
            .toList()

        val availableDataTypes = arrayOf(
            "이름", "성", "이메일", "전화번호", "주소", "도시",
            "회사명", "직업", "생년월일", "나이", "신용카드",
            "숫자", "문자열", "날짜", "불리언"
        )

        val customTableModel = object : DefaultTableModel() {
            override fun getColumnClass(columnIndex: Int): Class<*> {
                return if (getRowCount() > 0 && getValueAt(0, columnIndex) is ComboBox<*>) {
                    ComboBox::class.java
                } else {
                    super.getColumnClass(columnIndex)
                }
            }

            override fun isCellEditable(row: Int, column: Int): Boolean {
                return row == 0
            }
        }

        table.model = customTableModel
        columnNames.forEach { customTableModel.addColumn(it) }

        val dataTypeRow = columnNames.map {
            ComboBox(availableDataTypes).apply {
                selectedIndex = 0
            }
        }.toTypedArray()
        customTableModel.addRow(dataTypeRow)

        table.columnModel.columns.toList().forEach { column ->
            column.cellRenderer = object : TableCellRenderer {
                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    return (value as? ComboBox<*>)?.apply {
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
            column.cellEditor = object : DefaultCellEditor(ComboBox(availableDataTypes)) {
                override fun getTableCellEditorComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    row: Int,
                    column: Int
                ): Component {
                    val combo = super.getTableCellEditorComponent(table, value, isSelected, row, column) as ComboBox<*>
                    if (value is ComboBox<*>) {
                        combo.selectedItem = value.selectedItem
                    }
                    return combo
                }
            }
        }

        table.setRowHeight(0, 25)

        // 각 컬럼 크기를 컬럼명 전체가 보이도록 자동 조절
        val header = table.tableHeader
        val renderer = header.defaultRenderer as TableCellRenderer

        for (columnIndex in columnNames.indices) {
            val col = table.columnModel.getColumn(columnIndex)
            val headerValue = col.headerValue?.toString() ?: ""
            val comp = renderer.getTableCellRendererComponent(
                table, headerValue, false, false, -1, columnIndex
            )
            val headerWidth = comp.preferredSize.width + 20 // 조금 여유를 줌
            col.preferredWidth = headerWidth
        }

        if (columnNames.size >= 4) {
            tableModel.addRow(arrayOf("Alfreds Futterkiste", "Maria Anders", "Berlin", "030-0074321"))
            tableModel.addRow(arrayOf("Antonio Moreno Taquería", "Antonio Moreno", "México D.F.", "(5) 555-3932"))
        }

        table.preferredScrollableViewportSize = Dimension(
            (0 until table.columnCount).sumOf { table.columnModel.getColumn(it).preferredWidth }.coerceAtMost(900),
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