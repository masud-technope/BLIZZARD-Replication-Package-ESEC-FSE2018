package p;

public class Foo {

    class X extends Bar {

        {
            Foo.bar(this);
        }
    }

    /**
	 * @param bar
	 */
    public static void bar(Bar bar) {
        bar.getDisplay();
    }
}
