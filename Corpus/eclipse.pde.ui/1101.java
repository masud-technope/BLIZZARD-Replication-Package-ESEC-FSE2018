/**
 * Test supported @noinstantiate tag on class methods in the default package
 */
public class test6 {

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

    /**
	 * @noinstantiate
	 */
    protected void m3() {
    }

    /**
	 * @noinstantiate
	 * @return
	 */
    protected static Object m4() {
        return null;
    }
}
