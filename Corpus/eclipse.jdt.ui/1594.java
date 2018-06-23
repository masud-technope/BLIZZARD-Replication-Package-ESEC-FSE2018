package p;

public class A {

    void foo() {
        X v1[] = null;
        X v2[] = null;
        // BUG
        v2[1] = (true ? null : v1[1]);
        v1[1].dot(v2[1]);
    }
}
