//6, 17, 6, 24
package p;

class A {

    private static final int INT = foo + 5;

    public void m() {
        // foo is undeclared.
        int a = INT;
    }
}
