import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Test supported @NoReference annotations on fields in a class in the default package
 */
public class test8 {

    @NoReference
    public Object f1;

    @NoReference
    protected int f2;
}
