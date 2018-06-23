/**
 * Test unsupported @noreference tag on annotation methods in the default package
 */
public @interface test11 {

    /**
	 * @noreference
	 * @return
	 */
    public int m1();

    /**
	 * @noreference
	 * @return
	 */
    public abstract char m2();
}
