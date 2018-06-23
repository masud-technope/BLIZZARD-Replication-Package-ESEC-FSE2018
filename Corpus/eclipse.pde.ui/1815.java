/**
 * Test unsupported @noinstantiate tag on methods in an enum in the default package
 */
public enum test4 implements  {

    A() {
    }
    ;

    /**
	 * @noinstantiate
	 * @return
	 */
    public int m1() {
        return 0;
    }

    /**
	 * @noinstantiate
	 * @return
	 */
    public final char m2() {
        return 's';
    }
}
