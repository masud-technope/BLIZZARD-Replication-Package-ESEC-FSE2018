package p;

public class Foo extends Bar {

    // Test correct "thisification".
    /**
	 * @param bar
	 */
    public static void bar(Bar bar) {
        bar.getDisplay();
    }

    void foo() {
        X x = new X() {

            {
                // <- invoke here
                Foo.bar(Foo.this);
            }
        };
    }
}

class X {
}
