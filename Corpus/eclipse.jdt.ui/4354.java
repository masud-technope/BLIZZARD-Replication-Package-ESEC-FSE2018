package p;

public class Foo {

    // Test error, method already exists
    void foo(p.Bar bar, String foo) {
        // <- invoke here with same name and target type Foo
        new Bar().foo(null);
    }
}
