/**
 * Test unsupported @noimplement tag on fields in a class in the default package
 */
public class test8 {

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

    /**
	 * @noimplement
	 */
    long f4 = 0L;
}
