package p;

public class A {

    private int stop() {
        return 2;
    }
}

class B extends A {

    public //<-- pull up this method
    void stop() {
        System.out.println("pulled up!");
    }
}
