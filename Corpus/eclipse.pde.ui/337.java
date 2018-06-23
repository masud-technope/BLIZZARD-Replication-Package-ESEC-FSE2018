/**
 * Test unsupported @noreference tag on static final fields in an enum in the default package
 */
public enum test4 implements  {

    A() {
    }
    ;

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
}
