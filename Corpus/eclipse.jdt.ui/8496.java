//renaming A to B
package p;

public class A {

    static A A;
}

class X extends p.A {

    void x() {
        //fields come first
        p.A.A = A.A;
    }
}
