/**
 * Test unsupported @noreference tag on static final fields in a class in the default package
 */
public class test4 {

    /**
	 * @noreference
	 */
    public static final Object f1 = null;

    /**
	 * @noreference
	 */
    protected static final int f2 = 0;

    /**
	 * @noreference
	 */
    private static final char[] f3 = {};

    /**
	 * @noreference
	 */
    static final long f4 = 0L;
}
