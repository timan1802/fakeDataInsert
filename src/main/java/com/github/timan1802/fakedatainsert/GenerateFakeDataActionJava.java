package com.github.timan1802.fakedatainsert;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAware;

public class GenerateFakeDataActionJava extends AnAction implements DumbAware {

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(AnActionEvent e) {
        Object  psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        boolean isTable    = psiElement instanceof DbTable;

        e.getPresentation().setVisible(isTable);
        e.getPresentation().setEnabled(isTable);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Object psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement instanceof DbTable) {
            DataFakerDialogJava dialog = new DataFakerDialogJava((DbTable) psiElement);  // ✅ DbTable 인자 전달
            dialog.show();
        }
    }
}