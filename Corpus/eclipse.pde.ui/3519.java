package invokedynamic;

import java.util.Arrays;

/**
 * Tests an invoke dynamic reference in a lambda object
 */
public class test5 {

    void m1() {
        String[] array = { "one" };
        Arrays.sort(array, (String s1, String s2) -> {
            return s1.compareToIgnoreCase(s2);
        });
    }
}
