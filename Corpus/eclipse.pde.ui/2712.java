/**
 * Test supported @noextend tag on class methods in the default package
 */
public class test4 {

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
    public final char m2() {
        return 's';
    }

    /**
	 * @noextend
	 */
    protected void m3() {
    }

    /**
	 * @noextend
	 * @return
	 */
    protected static Object m4() {
        return null;
    }
}
