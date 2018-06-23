package p;

class A {

    class Inner {

        //conflicting name
        int a;

        //needs enclosing instance
        {
            foo();
        }
    }

    void foo() {
    }
}
