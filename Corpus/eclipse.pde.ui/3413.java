/**
 * Test unsupported @noinstantiate tag on methods in an annotation in the default package
 */
public @interface test4 {

    /**
	 * @noinstantiate
	 * @return
	 */
    public int m1();

    /**
	 * @noinstantiate
	 * @return
	 */
    public abstract char m2();
}
