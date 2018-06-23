/**
 * Test unsupported @noreference tag on a final field in an interface in the default package
 */
public interface test10 {

    /**
	 * @noreference
	 */
    public final Object f1 = null;
}
