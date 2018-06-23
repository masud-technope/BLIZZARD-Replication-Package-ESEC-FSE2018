/**
 * Test supported @noreference tag on private class methods in the default package
 */
public class test12 {

    /**
	 * @noreference 
	 * @return
	 */
    private int m1() {
        return 0;
    }
}
