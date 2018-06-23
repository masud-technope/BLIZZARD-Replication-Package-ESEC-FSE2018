import org.eclipse.pde.api.tools.annotations.NoReference;

/**
 * Tests the @NoReference annotation on inner classes, enums and interfaces 
 */
public @interface test3 {

    public @interface inner1 {

        /**
		 */
        @NoReference
        public static class Clazz {
        }

        /**
		 */
        @NoReference
        public interface inter {
        }

        /**
		 */
        public int field = 0;

        /**
		 */
        @NoReference
        public @interface annot {
        }
    }
}
