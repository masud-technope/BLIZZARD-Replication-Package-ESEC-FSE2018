interface I {

    void foo(Y<String>.Z<String> z, int x);
}

public class X {

    public static void main(String[] args) {
        I i = @Marker Y<String>.Z<String>::<>foo;
        i.foo(new Y<String>().new Z(), 10);
        Zork z;
    }
}

class Y<T> {

    class Z {

        void foo(int x) {
            System.out.println(x);
        }
    }
}
