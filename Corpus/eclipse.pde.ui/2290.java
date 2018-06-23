/**
 * Test unsupported @noextend tag on methods in an interface in the default package
 */
public interface test2 {

    /**
	 * @noextend
	 * @return
	 */
    public int m1();

    /**
	 * @noextend
	 * @return
	 */
    public abstract char m2();
}
