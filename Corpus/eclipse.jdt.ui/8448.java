package p;

public class Foo<E, F, G extends Comparable<E>> {

    <H> H getFoo(H h) {
        return null;
    }

    {
        Foo f = new Foo();
        // <-- invoke here
        f.getFoo(null);
    }
}
