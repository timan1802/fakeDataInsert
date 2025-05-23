package com.github.timan1802.fakedatainsert;

public enum MatchType {
    STARTS_WITH(MessagesBundle.message("match.type.starts.with")),
    ENDS_WITH(MessagesBundle.message("match.type.ends.with")),
    CONTAINS(MessagesBundle.message("match.type.contains")),
    EQUALS(MessagesBundle.message("match.type.equals"));

    private final String displayName;

    MatchType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
