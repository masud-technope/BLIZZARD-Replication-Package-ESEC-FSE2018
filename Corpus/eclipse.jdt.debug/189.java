import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class StepResult3 {

    interface StringSupplier {

        String get();
    }

    static String f() throws Exception {
        // bp7
        System.class.hashCode();
        StringSupplier p = (StringSupplier) Proxy.newProxyInstance(StepResult3.class.getClassLoader(), new Class[] { StringSupplier.class }, new InvocationHandler() {

            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return "hello from proxy";
            }
        });
        p.get();
        h();
        // bp8
        g(0);
        String x = g(1);
        return x;
    }

    private static Object h() {
        return null;
    }

    private static String g(int a) throws Exception {
        if (a == 1)
            throw new Exception("YYY");
        else {
            return "XXX";
        }
    }

    public static void main(String[] args) {
        try {
            f();
        } catch (Exception e) {
            System.out.println("e:" + e.getMessage());
            System.currentTimeMillis();
        }
    }
}
