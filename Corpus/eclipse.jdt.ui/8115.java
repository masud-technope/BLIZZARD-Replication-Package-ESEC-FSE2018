package trycatch_in;

import java.io.File;

public enum TestEnum2 implements  {

    A() {

        public void foo() {
            File file = null;
            /*[*/
            file.toURL();
        }
    }
    ;
}
