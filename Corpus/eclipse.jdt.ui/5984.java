package p;

public class Foo<E> {

    static class Bar<F> {

        static class FooBar<G> {

            static <E, F, G, H> void foo(E e, F f, G g, H h) {
            }
        }
    }

    {
        // <-- invoke here
        Foo.Bar.FooBar.foo(null, null, null, null);
    }
}
