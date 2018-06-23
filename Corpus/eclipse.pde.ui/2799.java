/**
 * Test unsupported @noreference tag on final fields in an enum in the default package
 */
public enum test2 implements  {

    A() {
    }
    ;

    /**
	 * @noreference
	 */
    public final Object f1 = null;

    /**
	 * @noreference
	 */
    protected final int f2 = 0;

    /**
	 * @noreference
	 */
    private final char[] f3 = {};
}
