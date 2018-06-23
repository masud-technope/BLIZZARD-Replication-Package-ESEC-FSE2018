class A<T> {

    static int CONST;

    class C {

        B b = null;

        // move to b
        void m() {
            CONST = 0;
        }
    }
}

class B {
}
