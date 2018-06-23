//rename A#m() to k() -> must rename all m()
package p;

abstract class Abstract {

    public abstract void m();

    void caller(Abstract abstr, A a, Interface inter, Impl2 impl2) {
        abstr.m();
        a.m();
        inter.m();
        impl2.m();
    }
}

class A extends Abstract {

    public // from Abstract
    void m() {
    }
}

interface Interface {

    //independent of Abstract
    void m();
}

class Impl2 extends Abstract implements Interface {

    public // from Abstract AND from Interface
    void m() {
    }
}
