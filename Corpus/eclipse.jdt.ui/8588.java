package p;

class A {

    enum TEST implements  {

        PROBE() {
        }
        ;
    }

    void m(int i) {
        TEST x = TEST.PROBE;
    }
}
