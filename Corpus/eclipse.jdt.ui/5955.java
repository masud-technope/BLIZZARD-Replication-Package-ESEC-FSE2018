//selection: 8, 17, 8, 18
package invalid;

class NoMethodBinding {

    void method() {
    }

    void method() {
        //<-- introduce 3 as a parameter
        int x = 3;
    }
}
