import org.eclipse.pde.api.tools.annotations.NoExtend;

/**
 * Test unsupported @NoExtend tag on a field in an annotation in the default package
 */
public @interface test2 {

    /**
	 */
    @NoExtend
    public Object f1 = null;
}
