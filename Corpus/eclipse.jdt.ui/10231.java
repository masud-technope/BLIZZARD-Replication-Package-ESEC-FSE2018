package p;

class A {

    enum TEST implements  {

        PROBE() {
        }
        ;
    }

    void m(int i) {
        A.TEST temp = TEST.PROBE;
        TEST x = temp;
    }
}
