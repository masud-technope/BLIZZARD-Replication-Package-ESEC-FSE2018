// 7, 20, 7, 25
package p;

enum Bug implements  {

    Z() {

        @Override
        String method() {
            return BUG;
        }
    }
    ;

    private static final String BUG = "bug";
}
