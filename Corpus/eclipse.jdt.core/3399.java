public class A {

    public void foo() {
        final String happy = //$NON-NLS-1$
        "string is a happy place" + "string is a happy place" + //$NON-NLS-1$//$NON-NLS-2$
        "string is a happy place";
    }
}
