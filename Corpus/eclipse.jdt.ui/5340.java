enum E implements  {

    A(1) {
    }
    , B(2) {
    }
    , C(3) {
    }
    ;

    public  E(int i) {
    }
}

class Z {

    E foo() {
        //<=== disable generalize type here.
        E e = null;
        return e;
    }
}
