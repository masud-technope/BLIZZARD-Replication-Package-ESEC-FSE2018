/**
 * Test unsupported @noextend tag on fields in a enum in the default package
 */
public enum test6 implements  {

    A() {
    }
    ;

    /**
	 * @noextend
	 */
    public Object f1 = null;

    /**
	 * @noextend
	 */
    protected int f2 = 0;

    /**
	 * @noextend
	 */
    private static char[] f3 = {};
}
