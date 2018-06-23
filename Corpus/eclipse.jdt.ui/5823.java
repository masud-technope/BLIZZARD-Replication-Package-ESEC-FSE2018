package p;

class Generic<E> {

    enum A implements  {

        ONE() {
        }
        ;

        A getSquare() {
            return ONE;
        }
    }

    boolean test() {
        return A.ONE == A.ONE.getSquare();
    }
}
