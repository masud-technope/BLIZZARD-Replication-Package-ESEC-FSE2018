/**
 * Test unsupported @noreference tag on private constructors in the default package
 */
public class test16 {

    /**
	 * Constructor
	 * @noreference This constructor is not intended to be referenced by clients.
	 */
    private  test16() {
    }

    /**
	 * Constructor
	 * @noreference This constructor is not intended to be referenced by clients.
	 */
    private  test16(int i) {
    }
}
