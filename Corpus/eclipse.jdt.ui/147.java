package p1;

public class A<T> {

    int i;

    class C {

        B b = null;

        // move to b
        void m() {
            i = 0;
        }
    }
}
