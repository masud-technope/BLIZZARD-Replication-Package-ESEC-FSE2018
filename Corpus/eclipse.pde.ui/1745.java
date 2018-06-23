/**
 * Test unsupported @noimplement tag on fields in a enum in the default package
 */
public enum test8 implements  {

    A() {
    }
    ;

    /**
	 * @noimplement
	 */
    public Object f1 = null;

    /**
	 * @noimplement
	 */
    protected int f2 = 0;

    /**
	 * @noimplement
	 */
    private static char[] f3 = {};
}
