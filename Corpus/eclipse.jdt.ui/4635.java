package p;

public abstract class A {

    /**
	 * @deprecated Use {@link #k()} instead
	 */
    abstract void m();

    abstract void k();
}

class B extends A {

    void m() {
        k();
    }

    void k() {
    //Foo
    }
}

class C extends B {

    void m() {
        k();
    }

    void k() {
    //Bar
    }
}
