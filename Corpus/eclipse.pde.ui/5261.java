/**
 *
 */
public class test16 {

    /**
	 * @noreference This interface is not intended to be referenced by clients.
	 */
    interface inner1 {

        /**
		 * @noreference This annotation is not intended to be referenced by clients.
		 */
        @interface inner2 {
        }

        static class C1 {

            /**
			 * @noreference
			 */
            @interface A1 {
            }
        }
    }
}
