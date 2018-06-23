package javadoc.testBug55223;

public class TestA {

    private void foo() {
        /* a */
        foo();
        /* b */
        foo();
        foo();
    }
}
