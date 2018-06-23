public class A {

     A(String s) {
    }

    protected void foo() {
        A a = new //$NON-NLS-1$
        A(//$NON-NLS-1$
        "") {

            public void run() {
            }
        };
    }
}
