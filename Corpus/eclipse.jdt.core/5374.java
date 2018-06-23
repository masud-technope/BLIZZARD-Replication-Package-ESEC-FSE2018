package test0455;

public class A {

    public void foo() {
        for (// for 1
        int i = 0; // for 1
        i < 10; // for 1
        i++) for (// for 2
        int j = 0; // for 2
        j < 10; // for 2
        j++) if (true) {
        }
    }
}
