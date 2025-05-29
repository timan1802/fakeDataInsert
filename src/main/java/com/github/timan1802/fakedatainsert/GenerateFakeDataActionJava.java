package com.github.timan1802.fakedatainsert;

import com.github.timan1802.fakedatainsert.utils.PluginExistsUtils;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 데이터베이스 테이블에 대한 가짜 데이터 생성 액션을 처리하는 클래스
 * DumbAware 인터페이스를 구현하여 인덱싱 중에도 액션 실행 가능
 */
public class GenerateFakeDataActionJava extends AnAction implements DumbAware {
    private static final Logger logger = LoggerFactory.getLogger(GenerateFakeDataActionJava.class);

    /**
     * 액션 업데이트 스레드 타입 지정
     * @return BGT(Background Thread) - 백그라운드에서 업데이트 수행
     */
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // BGT 대신 EDT 사용
    }

    /**
     * 액션의 가시성과 활성화 상태를 업데이트
     * @param e 액션 이벤트 객체
     */
    @Override
    public void update( AnActionEvent e) {
        Object psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        boolean isVisible = psiElement instanceof DbTable;
        boolean existsDbTools = PluginExistsUtils.existsDbTools();
        if (!existsDbTools) {
            logger.warn("Database Tools plugin is not installed");
            isVisible = false;
        }

        // 디버깅을 위한 로깅 추가
        if (psiElement != null) {
            logger.debug("PSI Element type: {}", psiElement.getClass().getName());
        } else {
            logger.warn("PSI Element is null");
        }

        e.getPresentation().setVisible(isVisible);
        e.getPresentation().setEnabled(isVisible);
    }

    /**
     * 실제 액션이 수행될 때 호출되는 메서드
     * DbTable 인스턴스에 대해 DataFakerDialog를 표시
     * @param e 액션 이벤트 객체
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Object psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (psiElement instanceof DbTable) {
            DataFakerDialogJava dialog = new DataFakerDialogJava((DbTable) psiElement);
            dialog.show();
        }
    }
}