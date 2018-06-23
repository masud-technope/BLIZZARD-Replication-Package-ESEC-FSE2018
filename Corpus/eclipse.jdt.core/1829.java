package javadoc.testBug65253;

/**
 * Comment 
 * @@@@see Unknown Should not complain on ref
 */
public class Test {

    /**
	 * Comment
	 * @@@param xxx Should not complain on param
	 * @@return int Should not be '@return' tag element 
	 */
    // should warn on missing tag for return type
    int foo() {
        return 0;
    }
}
