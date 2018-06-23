// 5, 72, 5, 72
package p;

class A {

    void m() {
        @Unavailable(/*should implement*/
        "s") /*64*/
        double a = 0, b = 1, c = 2, d = 3;
    }
}
