package a.b.c;

/**
 * @since
 */
public class TestClass3 {

    public void foo() {
    }

    /**
	 * @noextend
	 * @since
	 */
    class InnerTestClass3 {

        /**
		 * @deprecated
		 */
        public void foo() {
        }
    }
}
