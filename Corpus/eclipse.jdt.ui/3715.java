package p;

//disallow - shadowing
public class A {
}

class C {

    void m() {
        class B {
        }
        new A();
    }
}
