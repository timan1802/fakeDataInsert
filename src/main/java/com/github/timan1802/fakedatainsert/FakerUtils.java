package com.github.timan1802.fakedatainsert;

import net.datafaker.Faker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FakerUtils {


    /**
     * 1. Faker가 제공하는 모든 Provider 이름 반환
     */
    public static List<String> getAllProviderNames(Faker faker) {
        List<String> providerNames = new ArrayList<>();

        for (Method method : faker.getClass().getMethods()) {
            if (method.getParameterCount() == 0 &&
                method.getReturnType().getPackageName().startsWith("net.datafaker.providers")) {
                providerNames.add(method.getName());
            }
        }

        return providerNames;
    }

    /**
     * 2. Provider 이름으로 해당 Provider의 모든 메서드 반환 (생성자 제외)
     */
    public static List<String> getProviderMethodNames(Faker faker, String providerName) {
        List<String> methodNames = new ArrayList<>();
        try {
            Method providerMethod = faker.getClass().getMethod(providerName);
            Object provider = providerMethod.invoke(faker);

            for (Method method : provider.getClass().getDeclaredMethods()) {
                if (!method.isSynthetic() && !method.getName().equals("<init>")) {
                    methodNames.add(method.getName());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return methodNames;
    }

    /**
     * 3. Provider 이름과 메서드 이름을 받아 실행 결과값 반환
     */
    public static Object invokeProviderMethod(Faker faker, String providerName, String methodName) {
        try {
            Method providerMethod = faker.getClass().getMethod(providerName);
            Object provider = providerMethod.invoke(faker);

            Method targetMethod = provider.getClass().getMethod(methodName);
            return targetMethod.invoke(provider);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // 테스트
    public static void main(String[] args) {
        Faker faker = new Faker();
        System.out.println("== Providers ==");
        getAllProviderNames(faker).forEach(System.out::println);

        System.out.println("\n== Methods in 'name' Provider ==");
        getProviderMethodNames(faker, "name").forEach(System.out::println);

        System.out.println("\n== Sample execution of 'name.firstName' ==");
        System.out.println(invokeProviderMethod(faker, "name", "firstName"));
    }
}
