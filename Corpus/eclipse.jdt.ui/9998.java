package p;

public class Foo extends Bar {

    // Test correct "thisification".
    void foo() {
        X x = new X() {

            {
                // <- invoke here
                getDisplay();
            }
        };
    }
}

class X {
}
