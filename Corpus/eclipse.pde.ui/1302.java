package invokedynamic;

import java.util.Arrays;

/**
 * Tests an invoke dynamic reference for an instance method ref 
 */
public class test2 {

    class MR {

        public int mrCompare(String str1, String str2) {
            return 0;
        }
    }

    ;

    void m1() {
        MR mr = new MR();
        String[] array = { "one" };
        Arrays.sort(array, mr::<>mrCompare);
    }
}
