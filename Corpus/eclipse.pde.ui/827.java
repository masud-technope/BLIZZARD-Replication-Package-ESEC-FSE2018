/**
 * Test unsupported @noinstantiate tag on a field in an interface in the default package
 */
public interface test4 {

    /**
	 * @noinstantiate
	 */
    public Object f1 = null;
}
