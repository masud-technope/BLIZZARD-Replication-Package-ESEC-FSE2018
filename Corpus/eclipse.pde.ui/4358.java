/**
 * Test unsupported @noimplement tag on methods in an interface in the default package
 */
public interface test6 {

    /**
	 * @noimplement
	 * @return
	 */
    public int m1();

    /**
	 * @noimplement
	 * @return
	 */
    public abstract char m2();
}
