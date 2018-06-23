class A<T> {

    static int CONST;

    class C {

        B b = null;
    }
}

class B {

    // move to b
    void m() {
        A.CONST = 0;
    }
}
