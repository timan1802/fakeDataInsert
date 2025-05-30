package com.github.timan1802.fakedatainsert;

import junit.framework.TestCase;
import net.datafaker.Faker;
import net.datafaker.providers.base.Cat;
import net.datafaker.providers.base.Options;
import net.datafaker.providers.base.Text;

import java.lang.reflect.Method;
import java.util.*;

public class DataFakerTest extends TestCase {

    public void test_custom_yn(){
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            String YN = faker.text().text(Text.TextSymbolsBuilder.builder()
                                                  .len(1)
                                                  .with("YN", 1)
                                                  .build());
            System.out.println("YN : "+ YN);
            assertTrue(YN.equals("Y") || YN.equals("N"));
        }
    }

    public void testFaker_locale() {
        for (FakerDataLocaleType localeType : FakerDataLocaleType.values()) {
            Faker faker = new Faker(Locale.forLanguageTag(localeType.getCode()));
            String fullName = faker.name().fullName();

            System.out.println(localeType.getCode() + ": " + fullName);
            assertNotNull(fullName);

        }
    }

    public void testFaker() {
        Faker faker = new Faker(Locale.forLanguageTag("ko"));
        System.out.println(faker.name().fullName());

        Cat catFaker = faker.getProvider("Cat");
        System.out.printf("cat :" + catFaker.name());


        Faker faker2 = new Faker(Locale.forLanguageTag("en"));
        List<Faker> fakers = Arrays.asList(faker, faker2);

        for (int i = 0; i < 10; i++) {
            Faker randomFaker = new Faker().options().nextElement(fakers);
            System.out.println(randomFaker.address().fullAddress());
        }

        enum Day {
            MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
        }

        final Options opt = faker.options();
        opt.option(Day.class);

        Set<String> providerMethods = new TreeSet<>();

        for (Method method : faker.getClass().getMethods()) {
            if (method.getParameterCount() == 0) {
                Class<?> returnType = method.getReturnType();
                if (returnType.getPackageName().startsWith("net.datafaker.providers")) {
                    providerMethods.add(method.getName());
                }
            }
        }

        System.out.println("Available DataFaker Providers:");
        providerMethods.forEach(System.out::println);




    }


}
