import org.eclipse.pde.api.tools.annotations.NoExtend;

/**
 * Test unsupported @NoExtend annotation on fields in a class in the default package
 */
public class test6 {

    @NoExtend
    public Object f1 = null;

    @NoExtend
    protected int f2 = 0;

    @NoExtend
    private static char[] f3 = {};

    @NoExtend
    long f4 = 0L;
}
