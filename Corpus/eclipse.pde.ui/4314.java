/**
 * Test unsupported @noextend tag on fields in a class in the default package
 */
public class test6 {

    /**
	 * @noextend
	 */
    public Object f1 = null;

    /**
	 * @noextend
	 */
    protected int f2 = 0;

    /**
	 * @noextend
	 */
    private static char[] f3 = {};

    /**
	 * @noextend
	 */
    long f4 = 0L;
}
