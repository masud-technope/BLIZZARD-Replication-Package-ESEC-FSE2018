package p;

class Generic<E> {

    enum A implements  {

        ONE() {

            A getSquare() {
                return ONE;
            }
        }
        , TWO() {

            A getSquare() {
                return MANY;
            }
        }
        , MANY() {

            A getSquare() {
                return MANY;
            }
        }
        ;

        abstract A getSquare();
    }
}
