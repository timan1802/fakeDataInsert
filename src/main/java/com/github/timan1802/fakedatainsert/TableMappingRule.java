package com.github.timan1802.fakedatainsert;

public class TableMappingRule {
    private boolean enabled;
    private String text;
    private MatchType matchType;
    private String provider;
    private String method;

    public boolean isEnabled() {
        return enabled;
    }

    // 생성자, getter, setter 구현

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(final MatchType matchType) {
        this.matchType = matchType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(final String provider) {
        this.provider = provider;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public enum MatchType {
        STARTS_WITH("startsWith"),
        ENDS_WITH("endsWith"),
        CONTAINS("contains"),
        EQUALS("equals");

        private final String displayName;

        MatchType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}