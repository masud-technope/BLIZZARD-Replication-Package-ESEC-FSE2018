package a3;

import a3.b.B;

public class References {

    public void foo() {
        // single type references
        X x1 = new Z();
        // qualified type reference
        a3.b.A a = null;
        // qualified type references with inner type
        a3.b.A.B.C inner = null;
        // binary reference + qualified name reference
        Object o = a3.Y.field;
        // single name reference
        X x2 = (B) x1;
    }
}
