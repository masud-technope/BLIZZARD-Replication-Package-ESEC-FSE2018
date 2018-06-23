package p;

public class Foo {

    /**
	 * @param a
	 */
    public static void bar(A a) {
        a.foo();
    }

    A a;

    B b;

    C c;

    {
        Foo.bar(a);
        // <--- invoke here
        Foo.bar(b);
        Foo.bar(c);
    }
}

class A {

    void foo() {
    }
}

class B extends A {

    void foo() {
    }
}

class C extends A {

    void foo() {
    }
}
