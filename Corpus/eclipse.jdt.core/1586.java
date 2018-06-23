package test0039;

import static test0039.Z.*;

enum Z implements  {

    A() {
    }
    , B() {
    }
    , C() {
    }
    , D() {
    }
    ;
}

@interface Foo {

    Z[] value();
}

@interface Bar {

    Z value();
}

@Foo({ A, B, C, D })
@Bar(Z.B)
public @interface X {

    String[] bar();
}
