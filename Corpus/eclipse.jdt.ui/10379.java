package p;

class Test<T> {

    enum TEST implements  {

        FIRST() {
        }
        , SECOND() {
        }
        ;

        int n() {
            return 0;
        }
    }

    void m() {
        Object object = TEST.FIRST;
        int i = ((TEST) object).n();
    }
}
