//7, 30, 7, 63
package p;

import java.util.function.Supplier;

public class A {

    {
        Supplier<String> supplier = () -> (new Integer(0)).toString();
        Supplier<String> a2 = supplier;
    }
}
