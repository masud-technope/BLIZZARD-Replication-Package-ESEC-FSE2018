package trycatch_out;

import java.io.File;
import java.net.MalformedURLException;

public enum TestEnum2 implements  {

    A() {

        public void foo() {
            File file = null;
            try {
                /*[*/
                /*]*/
                file.toURL();
            } catch (MalformedURLException e) {
            }
        }
    }
    ;
}
