package com.github.timan1802.fakedatainsert


import com.intellij.openapi.ui.DialogWrapper
import java.awt.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class DataFakerDialog : DialogWrapper(true) {

    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)

    init {
        init()
        title = "Fake Data Insert"
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

        // 테이블 세팅
        tableModel.addColumn("Company")
        tableModel.addColumn("Contact")
        tableModel.addColumn("City")
        tableModel.addColumn("Phone")

        tableModel.addRow(arrayOf<Any>("DataType", "DataType", "DataType", "Phone"))
        tableModel.addRow(arrayOf("Alfreds Futterkiste", "Maria Anders", "Berlin", "030-0074321"))
        tableModel.addRow(arrayOf("Antonio Moreno Taquería", "Antonio Moreno", "México D.F.", "(5) 555-3932"))
        tableModel.addRow(arrayOf("Around the Horn", "Thomas Hardy", "London", "(171) 555-7788"))
        tableModel.addRow(arrayOf("Berglunds snabbköp", "Christina Berglund", "Luleå", "0921-12 34 65"))

        table.preferredScrollableViewportSize = Dimension(500, 120)
        val tableScrollPane = JScrollPane(table)

        // Insert, Cancel 버튼
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        buttonPanel.add(JButton("Insert"))
        buttonPanel.add(JButton("Cancel"))

        panel.add(topPanel, BorderLayout.NORTH)
        panel.add(tableScrollPane, BorderLayout.CENTER)
        panel.add(buttonPanel, BorderLayout.SOUTH)

        return panel
    }
}
