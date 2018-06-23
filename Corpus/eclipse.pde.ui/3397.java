import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Test unsupported @NoReference annotation on final fields in a class in the default package
 */
public class test2 {

    @NoReference
    public final Object f1 = null;

    @NoReference
    protected final int f2 = 0;

    @NoReference
    private final char[] f3 = {};

    @NoReference
    final long f4 = 0L;
}
