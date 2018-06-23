package classes;

/**
 * 
 * @since
 */
public class Test13 extends Super {

    public void m1() {
        System.out.println("not empty");
        m2();
        m3();
        m4();
    }

    private void m4() {
        System.out.println("not empty");
        Test13A a = new Test13A();
        a.getInteger();
    }

    public void m3() {
        System.out.println("not empty");
        Test13A.doSomething();
    }
}

class Super {

    protected void m2() {
        System.out.println("not empty");
    }
}
