import org.eclipse.pde.api.tools.annotations.NoImplement;

/**
 * Test unsupported @NoImplement tag on a field in an annotation in the default package
 */
public @interface test6 {

    /**
	 */
    @NoImplement
    public Object f1 = null;
}
