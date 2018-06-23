/**
 * Test supported @noimplement tag on constructors in the default package
 */
public class test24 {

    /**
	 * Constructor
	 * @noimplement This constructor is not intended to be referenced by clients.
	 */
    public  test24() {
    }

    /**
	 * Constructor
	 * @noimplement This constructor is not intended to be referenced by clients.
	 */
    protected  test24(int i) {
    }
}
