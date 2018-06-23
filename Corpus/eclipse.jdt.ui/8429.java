package p;

public class Foo extends Bar {

    void foo() {
    }

    void myFoo() throws Exception {
        // <-- invoke here
        foo();
        new Bar().foo();
    }
}
