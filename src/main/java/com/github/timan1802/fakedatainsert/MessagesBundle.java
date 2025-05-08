package com.github.timan1802.fakedatainsert;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class MessagesBundle extends DynamicBundle {
    private static final String BUNDLE = "messages.Messages";
    private static final MessagesBundle INSTANCE = new MessagesBundle();

    private MessagesBundle() {
        super(BUNDLE);
    }

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }

    @Nls
    @NotNull
    public static String getString(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}