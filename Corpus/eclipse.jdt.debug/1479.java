public class StepResult1 {

    class Nested {

        String f() {
            return "f-" + g();
        }
    }

    // private to force usage of a synthetic accessor
    private String g() {
        String prefix = "g-";
        String val1 = prefix + h();
        String val2 = j(false);
        // bp2C
        return val1 + "-" + val2;
    }

    String h() {
        // bp1
        String prefix = "h-";
        try {
            return i(false);
        } catch (Exception e) {
            return prefix + e.getMessage();
        }
    }

    String i(boolean flag) {
        if (flag) {
            return "i";
        }
        // bp3
        String value = i(true);
        throw new RuntimeException(value);
    }

    String j(boolean flag) {
        if (flag) {
            return "j";
        }
        // bp2A
        String value = j(true);
        // bp2B
        return value;
    }

    static String x = "x";

    public static String s() {
        return // bp4
        "bla";
    }

    interface I {

        String get();
    }

    public void testViaInterface() {
        I i = new I() {

            public String get() {
                // bp5
                return "bla";
            }

            ;
        };
        i.get();
    }

    public static void main(String[] args) {
        new StepResult1().new Nested().f();
        s();
        "".length();
        new StepResult1().testViaInterface();
        ;
    }
}
