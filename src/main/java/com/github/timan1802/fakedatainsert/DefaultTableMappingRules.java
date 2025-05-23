package com.github.timan1802.fakedatainsert;


import java.util.Arrays;
import java.util.List;

/**
 * 플러그인 최초 설치 시 기본값이 자동으로 설정
 */
public class DefaultTableMappingRules {
    public static List<TableMappingRule> getDefaultRules() {
        return Arrays.asList(
                createRule(true, "created_at", TableMappingRule.MatchType.EQUALS, "date", "yyyy-MM-dd HH:mm:ss"),
                createRule(true, "updated_at", TableMappingRule.MatchType.EQUALS, "date", "yyyy-MM-dd HH:mm:ss"),
                createRule(true, "url", TableMappingRule.MatchType.CONTAINS, "internet", "url"),
                createRule(true, "user_agent", TableMappingRule.MatchType.CONTAINS, "internet", "userAgent"),
                createRule(true, "_dt", TableMappingRule.MatchType.ENDS_WITH, "date", "yyyy-MM-dd HH:mm:ss")
        );
    }

    private static TableMappingRule createRule(boolean enabled, String text,
                                               TableMappingRule.MatchType matchType, String provider, String method) {
        TableMappingRule rule = new TableMappingRule();
        rule.setEnabled(enabled);
        rule.setText(text);
        rule.setMatchType(matchType);
        rule.setProvider(provider);
        rule.setMethod(method);
        return rule;
    }
}