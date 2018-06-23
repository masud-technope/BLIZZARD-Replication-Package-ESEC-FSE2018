//9, 28, 9, 36
package p;

class A {

    private static final Enum NUM = Enum.ONE;

    enum Enum implements  {

        ONE() {
        }
        , TWO() {
        }
        ;
    }

    public void run() {
        System.out.println(NUM.name());
    }
}
