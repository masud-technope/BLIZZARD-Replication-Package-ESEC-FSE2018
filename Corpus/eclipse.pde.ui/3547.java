/**
 * Test unsupported @noreference tag on a final field in an annotation in the default package
 */
public @interface test11 {

    /**
	 * @noreference
	 */
    public final Object f1 = null;
}
