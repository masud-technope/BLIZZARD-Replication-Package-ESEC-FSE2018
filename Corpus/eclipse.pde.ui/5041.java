package invokedynamic;

import java.util.Arrays;

/**
 * Tests an invoke dynamic reference in a lambda method call
 */
public class test6 {

    void m1() {
        String[] array = { "one" };
        Arrays.sort(array, ( s1,  s2) -> s1.compareToIgnoreCase(s2));
    }
}
