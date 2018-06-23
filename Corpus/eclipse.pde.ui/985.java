import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Test unsupported @NoReference annotation on static final fields in a class in the default package
 */
public class test4 {

    @NoReference
    public static final Object f1 = null;

    @NoReference
    protected static final int f2 = 0;

    @NoReference
    private static final char[] f3 = {};

    @NoReference
    static final long f4 = 0L;
}
