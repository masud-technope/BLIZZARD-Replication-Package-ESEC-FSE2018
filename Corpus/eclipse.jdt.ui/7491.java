package p;

class A {

    void foo() {
        //temp comment
        final int value = 42;
        // some valuable important comment which will be erased
        System.out.println(value);
    }
}
