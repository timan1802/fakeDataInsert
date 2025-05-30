package com.github.timan1802.fakedatainsert.utils;

import net.datafaker.Faker;
import net.datafaker.providers.base.Text;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.timan1802.fakedatainsert.constants.DataFakerConst.METHOD_CUSTOM_DATE_FOR_DB;
import static com.github.timan1802.fakedatainsert.constants.DataFakerConst.METHOD_CUSTOM_YN;

public class FakerUtils {

    /**
     * 1. Faker가 제공하는 모든 Provider 이름 반환
     */
    public static List<String> getAllProviderNames(Faker faker) {
        List<String> providerNames = new ArrayList<>();

        for (Method method : faker.getClass().getMethods()) {
            if (method.getParameterCount() == 0 &&
                method.getReturnType().getPackageName().startsWith("net.datafaker.providers.base")) {
                //제외
                if("drivingLicense".equals(method.getName())){
                    continue;
                }else if("duration".equals(method.getName())){
                    continue;

                }else if("options".equals(method.getName())){
                    continue;

                }else if("time".equals(method.getName())){
                    continue;

                }else if("unique".equals(method.getName())){
                    continue;

                }else if("getFaker".equals(method.getName())){
                    continue;
                }
                providerNames.add(method.getName());
            }
        }

        return providerNames;
    }

    /**
     * 2. Provider 이름으로 해당 Provider의 모든 메서드 반환 (생성자 제외)
     */
public static List<String> getProviderMethodNames(Faker faker, String providerName) {
    try {
        if("domain".equals(providerName)) {
            return List.of("fullDomain");
        }

        Method providerMethod = faker.getClass().getMethod(providerName);
        Object provider = providerMethod.invoke(faker);

        // 메소드 필터링 및 이름 추출
        List<String> methodList = Arrays.stream(provider.getClass().getMethods())
                     .filter(method ->
                        // Object 클래스의 메소드 제외
                        method.getDeclaringClass() != Object.class &&
                        // void 반환 메소드 제외
                        method.getReturnType() != void.class &&
                        // 파라미터가 없는 메소드만 포함
                        method.getParameterCount() == 0 &&
                        // getter 메소드 제외
                        !method.getName().startsWith("get") &&
                        // setter 메소드 제외
                        !method.getName().startsWith("set") &&
                        // 기타 제외할 메소드들
                        !Arrays.asList("equals", "hashCode", "toString", "stream").contains(method.getName())
                    )
                     .map(Method::getName)
                     .sorted()
                     .collect(Collectors.toList());

        if("date".equals(providerName)) {
            methodList.add(METHOD_CUSTOM_DATE_FOR_DB);
        }

        if("text".equals(providerName)) {
            methodList.add(METHOD_CUSTOM_YN);
        }

        return methodList;

    } catch (Exception e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}

    /**
     * 3. Provider 이름과 메서드 이름을 받아 실행 결과값 반환
     */
    public static Object invokeProviderMethod(Faker faker, String providerName, String methodName) {
        try {
            if("fullDomain".equals(methodName)) {
                return faker.domain().fullDomain("my-domain");
            }

            if(METHOD_CUSTOM_DATE_FOR_DB.equals(methodName)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(METHOD_CUSTOM_DATE_FOR_DB);
                return LocalDateTime.ofInstant(
                        faker.timeAndDate().past(),
                        ZoneId.systemDefault()
                ).format(formatter);

            }

            // Y or N
            if(METHOD_CUSTOM_YN.equals(methodName)) {
                return faker.text().text(Text.TextSymbolsBuilder.builder()
                                                 .len(1)
                                                 .with("YN", 1)
                                                 .build());

            }

            Method providerMethod = faker.getClass().getMethod(providerName);
            Object provider = providerMethod.invoke(faker);

            Method targetMethod = provider.getClass().getMethod(methodName);
            return targetMethod.invoke(provider);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}