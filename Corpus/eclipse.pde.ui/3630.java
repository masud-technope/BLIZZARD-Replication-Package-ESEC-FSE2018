/**
 *
 */
public class test17 {

    /**
	 * @noreference
	 */
    private @interface inner {
    }

    public static class C1 {

        /**
		 * @noreference This annotation is not intended to be referenced by clients.
		 */
        @interface A1 {

            class C2 {

                /**
				 * @noreference
				 */
                @interface A2 {
                }
            }
        }
    }
}
