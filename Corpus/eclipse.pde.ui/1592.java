/**
 * Test unsupported @nooverride tag on methods in an enum in the default package
 */
public enum test8 implements  {

    A() {
    }
    ;

    /**
	 * @nooverride
	 * @return
	 */
    public int m1() {
        return 0;
    }

    /**
	 * @nooverride
	 * @return
	 */
    public final char m2() {
        return 's';
    }
}
