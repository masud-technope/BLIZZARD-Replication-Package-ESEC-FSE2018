/**
 * Test unsupported @noinstantiate tag on fields in a enum in the default package
 */
public enum test12 implements  {

    A() {
    }
    ;

    /**
	 * @noinstantiate
	 */
    public Object f1 = null;

    /**
	 * @noinstantiate
	 */
    protected int f2 = 0;

    /**
	 * @noinstantiate
	 */
    private static char[] f3 = {};
}
