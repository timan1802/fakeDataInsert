import com.intellij.testFramework.UsefulTestCase;
import net.datafaker.Faker;
import net.datafaker.providers.base.Cat;
import net.datafaker.providers.base.Options;
import net.datafaker.service.FakerContext;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DataFakerTest extends UsefulTestCase {

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


    }
}
