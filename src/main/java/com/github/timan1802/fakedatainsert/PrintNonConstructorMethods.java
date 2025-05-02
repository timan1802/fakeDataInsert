package com.github.timan1802.fakedatainsert;

import net.datafaker.Faker;

import java.lang.reflect.Method;

public class PrintNonConstructorMethods {
    public static void main(String[] args) {
        Faker faker = new Faker();

        for (Method providerMethod : faker.getClass().getMethods()) {
            if (providerMethod.getParameterCount() == 0 &&
                providerMethod.getReturnType().getPackageName().startsWith("net.datafaker.providers")) {
                try {
                    Object provider = providerMethod.invoke(faker);
                    System.out.println("== Provider: " + providerMethod.getName() + " ==");

                    for (Method m : provider.getClass().getDeclaredMethods()) {
                        // 생성자 또는 synthetic 제외
                        if (!m.isSynthetic() && !m.getName().equals("<init>")) {
                            System.out.println("- " + m.getName());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error with provider: " + providerMethod.getName());
                }
            }
        }
    }
}
