/**
 *
 */
public class test15 {

    /**
	 * @noreference This annotation is not intended to be referenced by clients.
	 */
    private @interface inner1 {

        /**
		 * @noreference
		 */
        @interface inner2 {
        }
    }
}
