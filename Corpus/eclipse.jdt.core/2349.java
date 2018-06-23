package test0557;

import java.util.Vector;

public class A {

    AA aa;

    void foo() {
        (aa.bar()).get(0);
        // comment
        if (true) {
            //$NON-NLS-1$
            System.out.println("Hello: " + toString());
        }
    }
}

class AA {

    Vector bar() {
        return new Vector(1);
    }
}
