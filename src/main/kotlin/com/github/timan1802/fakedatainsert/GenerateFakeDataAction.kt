package com.github.timan1802.fakedatainsert

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GenerateFakeDataAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = DataFakerDialog()
        dialog.show()
    }
}