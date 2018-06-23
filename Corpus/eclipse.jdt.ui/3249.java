//9, 20 - 9, 23
package p;

class A {

    void f() {
        String x;
        if (true) {
            String temp = "i";
            try {
                x = temp;
            } catch (Exception e) {
                x = temp;
            }
        }
    }
}
