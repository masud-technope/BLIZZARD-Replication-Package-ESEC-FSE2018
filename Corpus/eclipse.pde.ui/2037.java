package invokedynamic;

import java.util.HashSet;
import java.util.function.Supplier;

/**
 * Tests an invoke dynamic reference to a constructor method ref
 */
public class test4 {

    class MR {

        public <T> void mr(Supplier<T> supplier) {
        }
    }

    ;

    void m1() {
        MR mr = new MR();
        mr.mr(HashSet<String>::<>new);
    }
}
