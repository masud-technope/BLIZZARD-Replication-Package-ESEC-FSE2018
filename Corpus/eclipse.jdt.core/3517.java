public class A {

     A(String s) {
    }

    protected void foo() {
        A //$NON-NLS-1$
        a = new A("") {

            public void run() {
            }
        };
    }
}
