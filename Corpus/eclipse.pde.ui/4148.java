/**
 * Test unsupported @noimplement tag on methods in an enum in the default package
 */
public enum test6 implements  {

    A() {
    }
    ;

    /**
	 * @noimplement
	 * @return
	 */
    public int m1() {
        return 0;
    }

    /**
	 * @noimplement
	 * @return
	 */
    public final char m2() {
        return 's';
    }
}
