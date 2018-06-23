import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Test unsupported @NoReference tag on a final field in an annotation in the default package
 */
public @interface test11 {

    /**
	 */
    @NoReference
    public final Object f1 = null;
}
