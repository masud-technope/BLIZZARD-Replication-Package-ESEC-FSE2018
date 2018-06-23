import org.eclipse.pde.api.tools.annotations.NoImplment;

/**
 * Test unsupported @NoImplement annotation on fields in a class in the default package
 */
public class test8 {

    @NoImplement
    public Object f1 = null;

    @NoImplement
    protected int f2 = 0;

    @NoImplement
    private static char[] f3 = {};

    @NoImplement
    long f4 = 0L;
}
