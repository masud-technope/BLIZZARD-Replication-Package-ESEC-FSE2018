//11, 9, 11, 25
package p;

class A {

    void m() {
        System.out.println(calculateCount());
        calculateCount();
        System.out.println(calculateCount());
        int x = calculateCount();
        calculateCount();
    }

    private int calculateCount() {
        return 1;
    }
}
