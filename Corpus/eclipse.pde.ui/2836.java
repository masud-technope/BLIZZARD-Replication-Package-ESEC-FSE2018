/**
 * Test unsupported @noreference tag on a static final field in an interface in the default package
 */
public interface test12 {

    /**
	 * @noreference
	 */
    public static final Object f1 = null;
}
