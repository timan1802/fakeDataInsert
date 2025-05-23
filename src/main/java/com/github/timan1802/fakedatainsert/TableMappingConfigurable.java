package com.github.timan1802.fakedatainsert;

//package com.github.timan1802.fakedatainsert.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TableMappingConfigurable implements Configurable {
    private TableMappingSettingsPanel settingsPanel;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return MessagesBundle.message("table.mapping.settings.title");

    }

    // 설명 추가 (선택사항)
    @Override
    public String getHelpTopic() {
        return "table.mapping.settings.description";
    }


    @Override
    public @Nullable JComponent createComponent() {
        settingsPanel = new TableMappingSettingsPanel();
        return settingsPanel;
    }

    @Override
    public boolean isModified() {
        // 설정이 변경되었는지 확인
        return true; // 항상 적용 버튼 활성화
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            if (!settingsPanel.validateAndSave()) {
                throw new ConfigurationException(MessagesBundle.message("validation.duplicate.rules"));
            }
        }
    }

    @Override
    public void reset() {
        // 설정을 초기 상태로 되돌림
        if (settingsPanel != null) {
            settingsPanel.loadSettings();
        }
    }

    @Override
    public void disposeUIResources() {
        settingsPanel = null;
    }
}