package test0034;

enum Bar implements  {

    CONSTANT() {
    }
    ;
}

@interface Foo {

    Bar value();
}

@Foo(Bar.CONSTANT)
public class X {
}
