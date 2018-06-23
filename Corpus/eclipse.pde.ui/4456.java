/**
 * Test supported @nooverride tag on constructors in the default package
 */
public class test22 {

    /**
	 * Constructor
	 * @nooverride This constructor is not intended to be referenced by clients.
	 */
    public  test22() {
    }

    /**
	 * Constructor
	 * @nooverride This constructor is not intended to be referenced by clients.
	 */
    protected  test22(int i) {
    }
}
