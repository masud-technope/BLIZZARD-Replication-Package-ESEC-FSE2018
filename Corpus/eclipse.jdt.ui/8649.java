//6, 73, 6, 83
package p;

import java.util.function.BiFunction;

public class A {

    BiFunction<Integer, Integer, Integer> a1 = (Integer i, Integer j) -> {
        int foo = foo(i + j);
        return foo;
    };

    private int foo(int x) {
        return x;
    }
}
