import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Tests all tags are invalid when parent enum is private or package default
 */
public enum test11 implements  {

    ENUM() {
    }
    ;

    enum inner1 implements  {

        /**
		 */
        @NoReference
        ENUM() {
        }
        ;

        /**
		 */
        @NoReference
        public enum inner2 implements  {

            ;
        }
    }
}
