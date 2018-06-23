/**
 * Test supported @noreference tag on interface methods in the default package
 */
public interface test5 {

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
