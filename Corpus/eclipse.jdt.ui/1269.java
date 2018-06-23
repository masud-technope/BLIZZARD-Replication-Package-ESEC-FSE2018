//11, 9, 11, 25
package p;

class A {

    void m() {
        int temp = calculateCount();
        System.out.println(temp);
        calculateCount();
        System.out.println(temp);
        int x = temp;
        calculateCount();
    }

    private int calculateCount() {
        return 1;
    }
}
