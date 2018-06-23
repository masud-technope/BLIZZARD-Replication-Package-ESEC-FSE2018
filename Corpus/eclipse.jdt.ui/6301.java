// 5, 23 -> 5, 38
class Foo {

    //$NON-NLS-1$
    public static final String BASE = "base.";

    private static final String CONSTANT = BASE + "suffix";

    public void m1() {
        String name = CONSTANT;
    }
}
