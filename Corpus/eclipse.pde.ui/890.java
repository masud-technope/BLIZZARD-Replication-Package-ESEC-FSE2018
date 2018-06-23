/**
 * Test supported @nooverride and @noreference tags on private/default constructors
 */
public class test25 {

    /**
	 * @nooverride
	 * @noreference
	 */
    private  test25() {
    }

    /**
	 * @nooverride
	 * @noreference
	 * @param num
	 */
     test25(int num) {
    }
}
