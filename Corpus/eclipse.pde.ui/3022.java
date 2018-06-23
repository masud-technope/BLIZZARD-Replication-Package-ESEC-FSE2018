/**
 * Test unsupported @nooverride tag on a field in an annotation in the default package
 */
public @interface test8 {

    /**
	 * @nooverride
	 */
    public Object f1 = null;
}
