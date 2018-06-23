package p;

public class Foo {

    // Test qualification with outer type
    void foo() {
        Bar bar = new Bar() {

            {
                // <--- invoke here
                foo();
            }
        };
    }
}

class Bar {
}
