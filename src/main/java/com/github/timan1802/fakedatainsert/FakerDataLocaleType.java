package com.github.timan1802.fakedatainsert;

public enum FakerDataLocaleType {
    AR("ar", "Armenian"),
    BE_BY("be-BY", "Belarusian"),
    BG_BG("bg-BG", "Bulgarian"),
    CA_CA("ca-CA", "Canada"),
    CA_CAT("ca-CAT", ""),
    CS_CZ("cs-CZ", "Czech republic"),
    DA_DK("da-DK", "language: Danish, country: Denmark"),
    DE("de", "German"),
    DE_AT("de-AT", "language: German, country: Austria"),
    DE_CH("de-CH", "language: German, country: Switzerland"),
    EL_GR("el-GR", "Greek"),
    EN("en", ""),
    EN_AU("en-AU", "Australia"),
    EN_AU_OCKER("en-au-ocker", ""),
    EN_BORK("en-BORK", ""),
    EN_CA("en-CA", ""),
    EN_GB("en-GB", "Great Britain"),
    EN_IE("en-IE", "Republic Of Ireland"),
    EN_IN("en-IN", "India"),
    EN_MS("en-MS", ""),
    EN_NEP("en-NEP", ""),
    EN_NG("en-NG", ""),
    EN_NZ("en-NZ", ""),
    EN_PAK("en-PAK", ""),
    EN_SG("en-SG", ""),
    EN_UG("en-UG", ""),
    EN_US("en-US", ""),
    EN_ZA("en-ZA", ""),
    EN_PH("en-PH", ""),
    ES_AR("es-AR", "language: Spanish, country: Argentina"),
    ES_ES("es-ES", "language: Spanish, country: Spain"),
    ES_MX("es-MX", "language: Spanish, country: Mexico"),
    ES_PY("es-PY", "language: Spanish, country: Paraguay"),
    ET_EE("et-EE", "Estonian"),
    FA("fa", "Persian"),
    FI_FI("fi-FI", "language: Finnish, country: Finland"),
    FR_FR("fr-FR", "language: French, country: France"),
    FR_CH("fr-CH", "language: French, country: Switzerland"),
    GE_GE("ge-GE", "Georgia"),
    HE_IL("he-IL", "language: Hebrew, country: Israel"),
    HR_HR("hr-HR", "Croatian"),
    HU_HU("hu-HU", "Hungarian"),
    HY_AM("hy-AM", "Armenian"),
    ID_ID("id-ID", "Indonesia"),
    IT("it", "Italian"),
    JA_JP("ja-JP", "language: Japanese, country: Japan"),
    KA_GE("ka-GE", "language: Georgian, country: Georgia"),
    KO_KR("ko-KR", "language: Korean, country: South Korea"),
    LV_LV("lv-LV", "language: Latvian, country: Latvia"),
    MK_MK("mk-MK", "North Macedonia"),
    NB_NO("nb-NO", "language: Norwegian, country: Norway"),
    NL_NL("nl-NL", "language: Dutch, country: Netherlands"),
    NL_BE("nl-BE", "language: Dutch, country: Belgium"),
    PL("pl", "language: Polish, country: Poland"),
    PT("pt", "language: Portuguese"),
    PT_BR("pt-BR", "language: Portuguese, country: Brazil"),
    RO_MD("ro-MD", "language: Romanian a.k.a. Moldavian, country: Moldova"),
    RU_RU("ru-RU", "Russian"),
    SK_SK("sk-SK", "Slovak"),
    SQ_AL("sq-AL", "Albanian"),
    SV("sv", "Swedish"),
    SV_SE("sv-SE", "language: Swedish, country: Sweden"),
    TA("ta", "Tamil"),
    TR("tr", "Turkish"),
    TH_TH("th-TH", "language: Thai, country: Thailand"),
    UK_UA("uk-UA", "language: Ukrainian, country: Ukraine"),
    UZ("uz", "Uzbek"),
    VI_VN("vi-VN", "language: Vietnamese, country: Vietnam"),
    ZH_CN("zh-CN", "language: Chinese, country: China"),
    ZH_TW("zh-TW", "language: Chinese, country: Taiwan");

    private final String code;
    private final String description;

    FakerDataLocaleType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}