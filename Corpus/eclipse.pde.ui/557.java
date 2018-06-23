/**
 * Tests the valid use of field tags on an enum in the default package
 * 
 * @since 3.4
 */
public enum test3 implements  {

    A() {
    }
    , B() {
    }
    ;

    /**
	 * @noreference This enum field is not intended to be referenced by clients.
	 */
    public Object f1 = null;

    /**
	 * @noreference This enum field is not intended to be referenced by clients.
	 */
    protected int f2 = 0;

    /**
	 * @noreference This enum field is not intended to be referenced by clients.
	 */
    protected static char g = 'd';
}
