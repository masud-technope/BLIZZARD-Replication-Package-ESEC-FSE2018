import org.eclipse.pde.api.tools.annotations.NoOverride;

/**
 * Test unsupported @NoOverride annotation on fields in a class in the default package
 */
public class test10 {

    @NoOverride
    public Object f1 = null;

    @NoOverride
    protected int f2 = 0;

    @NoOverride
    private static char[] f3 = {};

    @NoOverride
    long f4 = 0L;
}
