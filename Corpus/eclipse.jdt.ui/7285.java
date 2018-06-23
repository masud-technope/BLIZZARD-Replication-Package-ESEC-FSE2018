package p;

public class Enum_in {

    public void foo() {
        E e = E.A;
    }
}

enum E implements  {

    A(1) {
    }
    , B(2) {
    }
    , C(3) {
    }
    ;

    public  E(/*]*/
    int i) {
    }
}
