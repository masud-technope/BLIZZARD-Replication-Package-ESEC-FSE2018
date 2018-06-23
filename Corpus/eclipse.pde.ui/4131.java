import org.eclipse.pde.api.tools.annotations.NoOverride;

/**
 * Test unsupported @NoOverride tag on a field in an annotation in the default package
 */
public @interface test8 {

    /**
	 */
    @NoOverride
    public Object f1 = null;
}
