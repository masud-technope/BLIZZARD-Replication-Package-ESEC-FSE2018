package p0;

public class Foo {

    // test visibility adjustment of intermediary type
    // because of existing references
    public // <- create im in Bar.
    void bar() {
    }

    {
        bar();
    }
}
