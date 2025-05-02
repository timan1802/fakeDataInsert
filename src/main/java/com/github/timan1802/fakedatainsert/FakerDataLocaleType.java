package com.github.timan1802.fakedatainsert;

public enum FakerDataLocaleType {
    AR("ar", "Armenian"),
    BE_BY("be_BY", "Belarusian"),
    BG_BG("bg_BG", "Bulgarian"),
    CA_CA("ca_CA", "Canada"),
    CA_CAT("ca-CAT", ""),
    CS_CZ("cs_CZ", "Czech republic"),
    DA_DK("da-DK", "language: Danish, country: Denmark"),
    DE("de", "German"),
    DE_AT("de-AT", "language: German, country: Austria"),
    DE_CH("de_CH", "language: German, country: Switzerland"),
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
    ES_ES("es_ES", "language: Spanish, country: Spain"),
    ES_MX("es-MX", "language: Spanish, country: Mexico"),
    ES_PY("es-PY", "language: Spanish, country: Paraguay"),
    ET_EE("et_EE", "Estonian"),
    FA("fa", "Persian"),
    FI_FI("fi-FI", "language: Finnish, country: Finland"),
    FR_FR("fr_FR", "language: French, country: France"),
    FR_CH("fr_CH", "language: French, country: Switzerland"),
    GE_GE("ge_GE", "Georgia"),
    HE_IL("he_IL", "language: Hebrew, country: Israel"),
    HR_HR("hr_HR", "Croatian"),
    HU_HU("hu_HU", "Hungarian"),
    HY_AM("hy_AM", "Armenian"),
    ID_ID("id_ID", "Indonesia"),
    IT("it", "Italian"),
    JA_JP("ja_JP", "language: Japanese, country: Japan"),
    KA_GE("ka_GE", "language: Georgian, country: Georgia"),
    KO_KR("ko_KR", "language: Korean, country: South Korea"),
    LV_LV("lv_LV", "language: Latvian, country: Latvia"),
    MK_MK("mk_MK", "North Macedonia"),
    NB_NO("nb_NO", "language: Norwegian, country: Norway"),
    NL_NL("nl_NL", "language: Dutch, country: Netherlands"),
    NL_BE("nl_BE", "language: Dutch, country: Belgium"),
    PL("pl", "language: Polish, country: Poland"),
    PT("pt", "language: Portuguese"),
    PT_BR("pt_BR", "language: Portuguese, country: Brazil"),
    RO_MD("ro_MD", "language: Romanian a.k.a. Moldavian, country: Moldova"),
    RU_RU("ru_RU", "Russian"),
    SK_SK("sk_SK", "Slovak"),
    SQ_AL("sq_AL", "Albanian"),
    SV("sv", "Swedish"),
    SV_SE("sv-SE", "language: Swedish, country: Sweden"),
    TA("ta", "Tamil"),
    TR("tr", "Turkish"),
    TH_TH("th_TH", "language: Thai, country: Thailand"),
    UK_UA("uk_UA", "language: Ukrainian, country: Ukraine"),
    UZ("uz", "Uzbek"),
    VI_VN("vi_VN", "language: Vietnamese, country: Vietnam"),
    ZH_CN("zh_CN", "language: Chinese, country: China"),
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