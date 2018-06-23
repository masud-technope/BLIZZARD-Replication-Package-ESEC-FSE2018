public class NativeDropTests {

    public static void foo() {
        System.out.println("foo");
        // breakpoint on this line
        System.out.println("breakpoint");
    }

    public static void bar() {
        foo();
    }

    public static void main(String[] args) throws Exception {
        Class clazz = NativeDropTests.class;
        java.lang.reflect.Method method = clazz.getMethod("bar", new Class[] {});
        method.invoke(null, new Object[] {});
    }
}
