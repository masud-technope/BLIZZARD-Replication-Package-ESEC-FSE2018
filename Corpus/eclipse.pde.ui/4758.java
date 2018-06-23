/**
 * Tests all tags are invalid when parent enum is private or package default
 */
public enum test11 implements  {

    ENUM() {
    }
    ;

    enum inner1 implements  {

        /**
		 * @noreference
		 */
        ENUM() {
        }
        ;

        /**
		 * @noreference
		 */
        public enum inner2 implements  {

            ;
        }
    }
}
