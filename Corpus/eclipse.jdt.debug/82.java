public class StepResult2 {

    public static void main(String[] args) {
        f();
    }

    private static void f() {
        try {
            g();
        } catch (Exception e) {
        }
    }

    private static void g() {
        try {
            h();
        } finally {
            "".length();
        }
    }

    private static void h() {
        i();
    }

    private static void i() {
        throw // bp6
        new RuntimeException("hi");
    }
}
