/**
 * Test unsupported @nooverride tag on fields in a enum in the default package
 */
public enum test10 implements  {

    A() {
    }
    ;

    /**
	 * @nooverride
	 */
    public Object f1 = null;

    /**
	 * @nooverride
	 */
    protected int f2 = 0;

    /**
	 * @nooverride
	 */
    private static char[] f3 = {};
}
