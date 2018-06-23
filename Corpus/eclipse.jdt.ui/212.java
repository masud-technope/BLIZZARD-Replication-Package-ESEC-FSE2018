package p;

@interface B {

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
 * @see p.B
 */
@B
class Client {

    @Deprecated
    @B()
    void bad() {
    }
}
