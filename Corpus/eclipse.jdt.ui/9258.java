//10, 21 -> 10, 21  replaceAll == true, removeDeclaration == true
package p;

class Test {

    private enum Color implements  {

        PINK() {
        }
        , YELLOW() {
        }
        ;

        static final Color CORPORATE_COLOR = Color.PINK;
    }

    private enum Box implements  {

        FIRST(Color.CORPORATE_COLOR) {
        }
        ;

        public  Box(Color c) {
        }
    }

    Color c = Color.CORPORATE_COLOR;
}
