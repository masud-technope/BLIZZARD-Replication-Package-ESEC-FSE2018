package invokedynamic;

import java.util.Arrays;

/**
 * Tests an invoke dynamic reference for a static method ref 
 */
public class test1 {

    public static class MR {

        public static int mrCompare(String str1, String str2) {
            return 0;
        }
    }

    ;

    void m1() {
        String[] array = { "one" };
        Arrays.sort(array, MR::<>mrCompare);
    }
}
