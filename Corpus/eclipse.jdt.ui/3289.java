package p;

class A {

    void foo() {
        //expected FOO_BAR
        boolean isfb = isFooBar();
    }

    static boolean isFooBar() {
        return false;
    }
}
