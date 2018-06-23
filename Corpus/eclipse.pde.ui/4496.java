package invokedynamic;

import java.util.HashSet;
import java.util.function.Supplier;

/**
 * Tests an invoke dynamic reference in a lambda constructor call
 */
public class test7 {

    class MR {

        public <T> void mr(Supplier<T> supplier) {
        }
    }

    ;

    void m1() {
        MR mr = new MR();
        mr.mr(() -> {
            return new HashSet<String>();
        });
    }
}
