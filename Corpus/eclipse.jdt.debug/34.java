public class BreakpointListenerTest {

    public static void main(String[] args) {
        // conditional breakpoint here:  foo(); return false;
        foo();
        System.out.println("out of foo");
    }

    private static void foo() {
        // breakpoint here
        System.out.println("hello");
    }
}
