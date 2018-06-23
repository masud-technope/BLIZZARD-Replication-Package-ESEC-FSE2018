/**
 * Test unsupported @noreference tag on final fields in a class in the default package
 */
public class test2 {

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

    /**
	 * @noreference
	 */
    final long f4 = 0L;
}
