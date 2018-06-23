/**
 * Test supported @noextend tag on constructors in the default package
 */
public class test20 {

    /**
	 * Constructor
	 * @noextend This constructor is not intended to be referenced by clients.
	 */
    public  test20() {
    }

    /**
	 * Constructor
	 * @noextend This constructor is not intended to be referenced by clients.
	 */
    protected  test20(int i) {
    }
}
