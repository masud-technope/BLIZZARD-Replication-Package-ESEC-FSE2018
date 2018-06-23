package trycatch_in;

import java.io.File;

public enum TestEnum1 implements  {

    A() {
    }
    ;

    public void foo() {
        File file = null;
        /*[*/
        file.toURL();
    }
}
