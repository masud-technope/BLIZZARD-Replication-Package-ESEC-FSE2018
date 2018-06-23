import org.eclipse.pde.api.tools.annotations.NoInstantiate;

/**
 * Test unsupported @NoInstantiate tag on a field in an annotation in the default package
 */
public @interface test4 {

    /**
	 */
    @NoInstantiate
    public Object f1 = null;
}
