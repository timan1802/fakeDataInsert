package com.github.timan1802.fakedatainsert

import com.intellij.database.psi.DbTable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware

class GenerateFakeDataAction : AnAction(), DumbAware {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        val isTable = psiElement is DbTable

        e.presentation.isVisible = isTable
        e.presentation.isEnabled = isTable
    }

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        if (psiElement is DbTable) {
            val dialog = DataFakerDialog()
            dialog.show()
        }
    }
}
