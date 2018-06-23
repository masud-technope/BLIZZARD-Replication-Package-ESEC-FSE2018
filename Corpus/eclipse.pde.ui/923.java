/**
 * Test unsupported @noextend tag on a field in an annotation in the default package
 */
public @interface test2 {

    /**
	 * @noextend
	 */
    public Object f1 = null;
}
