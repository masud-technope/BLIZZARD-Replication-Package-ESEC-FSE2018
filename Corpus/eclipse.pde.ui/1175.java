/**
 * Test unsupported @noextend tag on methods in an enum in the default package
 */
public enum test2 implements  {

    A() {
    }
    ;

    /**
	 * @noextend
	 * @return
	 */
    public int m1() {
        return 0;
    }

    /**
	 * @noextend
	 * @return
	 */
    public char m2() {
        return 's';
    }
}
