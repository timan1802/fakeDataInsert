package com.github.timan1802.fakedatainsert;

import junit.framework.TestCase;
import net.datafaker.Faker;
import net.datafaker.providers.base.Cat;
import net.datafaker.providers.base.Options;

import java.lang.reflect.Method;
import java.util.*;

public class DataFakerTest extends TestCase {

    public void testFaker() {
        Faker faker = new Faker(new Locale("ko"));
        System.out.println(faker.name().fullName());

        Cat catFaker = faker.getProvider("Cat");
        System.out.printf("cat :" + catFaker.name());


        Faker faker2 = new Faker(new Locale("en"));
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
