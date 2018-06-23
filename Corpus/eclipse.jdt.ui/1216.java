//7, 17, 7, 22
package p;

public class A {

    private static final int x = 9;

    private static final int y = 10;

    private static final int CONSTANT = x + y;

    void m() {
        int j = CONSTANT + x + y;
    }
}
