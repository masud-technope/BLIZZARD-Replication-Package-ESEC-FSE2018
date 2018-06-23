package x.y.z;

import c.NoRefClass;

public class testC14 {

    class Inner {

        void method2() {
            NoRefClass clazz = new NoRefClass();
            String field = clazz.fNoRefClassField;
            clazz.noRefClassMethod();
        }
    }

    void method1() {
        NoRefClass clazz = new NoRefClass();
        String field = clazz.fNoRefClassField;
        clazz.noRefClassMethod();
    }
}
