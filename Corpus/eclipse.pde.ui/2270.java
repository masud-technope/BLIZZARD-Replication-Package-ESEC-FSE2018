package invokedynamic;

import java.util.Arrays;

/**
 * Tests an invoke dynamic reference to an instance method of an arbitrary object
 */
public class test3 {

    void m1() {
        String[] array = { "one" };
        Arrays.sort(array, String::<>compareToIgnoreCase);
    }
}
