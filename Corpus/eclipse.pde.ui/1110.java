/**
 * Test supported @noinstantiate tag on constructors in the default package
 */
public class test18 {

    /**
	 * Constructor
	 * @noinstantiate This constructor is not intended to be referenced by clients.
	 */
    public  test18() {
    }

    /**
	 * Constructor
	 * @noinstantiate This constructor is not intended to be referenced by clients.
	 */
    protected  test18(int i) {
    }
}
