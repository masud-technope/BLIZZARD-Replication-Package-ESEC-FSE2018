//9, 14, 9, 56
package p;

import java.io.FileReader;
import java.io.IOException;

class A {

    void foo() throws IOException {
        try (FileReader reader = new FileReader("file")) {
            int ch;
            while ((ch = reader.read()) != -1) {
                System.out.println(ch);
            }
        }
    }
}
