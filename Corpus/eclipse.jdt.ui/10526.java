package p;

class A {

    void foo() {
        //expected FOO_BAR
        String s = getFooBar();
    }

    static String getFooBar() {
        return null;
    }
}
