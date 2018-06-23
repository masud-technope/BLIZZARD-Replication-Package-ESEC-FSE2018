/**
 * Test unsupported @nooverride tag on private methods in an enum in the default package
 */
public enum test9 implements  {

    A() {
    }
    ;

    /**
	 * @nooverride
	 * @return
	 */
    private int m1() {
        return 0;
    }

    /**
	 * @nooverride
	 * @return
	 */
    private final char m2() {
        return 's';
    }
}
