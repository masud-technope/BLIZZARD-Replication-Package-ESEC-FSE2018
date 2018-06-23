package a.b.c;

/**
 * @noextend
 * @since
 */
public class TestClass2 {

    public void foo() {
    }

    /**
	 * @noextend
	 * @noinstantiate
	 * @since
	 */
    public static class InnerTestClass2 {

        public void foo() {
        }
    }
}
