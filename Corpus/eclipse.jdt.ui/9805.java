package p;

public class Foo {

    <E> void setE(E e) {
    }

    {
        // <-- invoke here
        this.<String>setE("");
    }
}
