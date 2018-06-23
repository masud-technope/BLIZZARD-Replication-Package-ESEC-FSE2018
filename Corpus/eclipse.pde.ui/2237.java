/**
 * Test supported @noimplement tag on class methods in the default package
 */
public class test2 {

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

    /**
	 * @noimplement
	 */
    protected void m3() {
    }

    /**
	 * @noimplement
	 * @return
	 */
    protected static Object m4() {
        return null;
    }
}
