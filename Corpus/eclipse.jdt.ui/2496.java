package p;

import java.io.IOException;

public class Foo extends Bar {

    /**
	 * @param bar
	 * @throws IOException
	 */
    public static void bar(Bar bar) throws IOException {
        bar.foo();
    }

    protected void foo() {
    }

    void myFoo() throws Exception {
        // <-- invoke here
        Foo.bar(this);
        Foo.bar(new Bar());
    }
}
