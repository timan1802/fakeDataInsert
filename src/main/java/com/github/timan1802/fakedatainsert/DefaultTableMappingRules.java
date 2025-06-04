package com.github.timan1802.fakedatainsert;

import java.util.Arrays;
import java.util.List;

import static com.github.timan1802.fakedatainsert.constants.DataFakerConst.METHOD_CUSTOM_DATE_FOR_DB;
import static com.github.timan1802.fakedatainsert.constants.DataFakerConst.METHOD_CUSTOM_YN;

/**
 * 플러그인 최초 설치 시 기본값이 자동으로 설정
 */
public class DefaultTableMappingRules {
    /**
     * 기본 테이블 매핑 규칙 목록을 반환
     * @return 미리 정의된 매핑 규칙 목록
     */
    public static List<TableMappingRule> getDefaultRules() {
        return Arrays.asList(
                createRule(true, "created_at", TableMappingRule.MatchType.EQUALS, "date", METHOD_CUSTOM_DATE_FOR_DB),
                createRule(true, "updated_at", TableMappingRule.MatchType.EQUALS, "date", METHOD_CUSTOM_DATE_FOR_DB),
                createRule(true, "_date", TableMappingRule.MatchType.ENDS_WITH, "date", METHOD_CUSTOM_DATE_FOR_DB),
                createRule(true, "_dt", TableMappingRule.MatchType.ENDS_WITH, "date", METHOD_CUSTOM_DATE_FOR_DB),

                createRule(true, "url", TableMappingRule.MatchType.CONTAINS, "internet", "url"),

                createRule(true, "user_agent", TableMappingRule.MatchType.CONTAINS, "internet", "userAgent"),

                createRule(true, "_yn", TableMappingRule.MatchType.ENDS_WITH, "text", METHOD_CUSTOM_YN),

                createRule(true, "_name", TableMappingRule.MatchType.ENDS_WITH, "name", "fullName"),
                createRule(true, "_nm", TableMappingRule.MatchType.ENDS_WITH, "name", "fullName"),

                createRule(true, "content", TableMappingRule.MatchType.CONTAINS, "lorem", "sentence")

        );
    }

    /**
     * 새로운 테이블 매핑 규칙 객체를 생성
     * @param enabled 규칙 활성화 여부
     * @param text 매칭될 텍스트
     * @param matchType 매칭 타입 (EQUALS, CONTAINS, ENDS_WITH 등)
     * @param provider 데이터 제공자 (date, internet 등)
     * @param method 사용할 메서드 이름
     * @return 생성된 TableMappingRule 객체
     */
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