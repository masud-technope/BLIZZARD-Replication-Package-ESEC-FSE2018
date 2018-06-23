/**
 * Test unsupported @noinstantiate tag on fields in a class in the default package
 */
public class test12 {

    /**
	 * @noinstantiate
	 */
    public Object f1 = null;

    /**
	 * @noinstantiate
	 */
    protected int f2 = 0;

    /**
	 * @noinstantiate
	 */
    private static char[] f3 = {};

    /**
	 * @noinstantiate
	 */
    long f4 = 0L;
}
