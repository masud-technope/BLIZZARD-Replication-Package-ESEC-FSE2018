/**
 * Test unsupported @nooverride tag on fields in a class in the default package
 */
public class test10 {

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

    /**
	 * @nooverride
	 */
    long f4 = 0L;
}
