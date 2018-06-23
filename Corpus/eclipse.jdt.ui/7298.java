//can't rename A.m to k
package p;

class A {

    void m() {
    }
}

class B {

    void k() {
    }

    class I extends A {

        void f() {
            //binds to A#k() iff that exists
            k();
        }
    }
}
