/**
 * Test supported @noreference tag on enum methods in the default package
 */
public enum test5 implements  {

    A() {
    }
    ;

    /**
	 * @noreference
	 * @return
	 */
    public int m1() {
        return 0;
    }

    /**
	 * @noreference
	 * @return
	 */
    public final char m2() {
        return 's';
    }
}
