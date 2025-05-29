package com.github.timan1802.fakedatainsert;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 테이블 매핑 설정을 위한 설정 화면을 제공하는 클래스
 * IDE의 Settings/Preferences 대화상자에 통합됨
 */
public class TableMappingConfigurable implements Configurable {
    private TableMappingSettingsPanel settingsPanel;

    /**
     * 설정 화면의 표시 이름을 반환
     */
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return MessagesBundle.message("table.mapping.settings.title");
    }

    /**
     * 설정 화면의 도움말 항목 ID를 반환
     */
    @Override
    public String getHelpTopic() {
        return "table.mapping.settings.description";
    }

    /**
     * 설정 UI 컴포넌트를 생성하고 반환
     */
    @Override
    public @Nullable JComponent createComponent() {
        settingsPanel = new TableMappingSettingsPanel();
        return settingsPanel;
    }

    /**
     * 설정이 수정되었는지 확인
     * @return 항상 true를 반환하여 적용 버튼을 활성화 상태로 유지
     */
    @Override
    public boolean isModified() {
        return true;
    }

    /**
     * 변경된 설정을 적용
     * @throws ConfigurationException 설정 유효성 검사 실패 시 발생
     */
    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            if (!settingsPanel.validateAndSave()) {
                throw new ConfigurationException(MessagesBundle.message("validation.duplicate.rules"));
            }
        }
    }

    /**
     * 설정을 초기 상태로 재설정
     */
    @Override
    public void reset() {
        if (settingsPanel != null) {
            settingsPanel.loadSettings();
        }
    }

    /**
     * UI 리소스 정리
     */
    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }
}