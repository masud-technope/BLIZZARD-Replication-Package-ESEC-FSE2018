package p;

@interface A {

    enum E implements  {

        ONE() {
        }
        , TWO() {
        }
        , THREE() {
        }
        ;
    }
}

/**
 * @see p.A
 */
@A
class Client {

    @Deprecated
    @A()
    void bad() {
    }
}
