package javadoc.test013;

import java.util.*;

public class Test {

    /**
	 * Javadoc comment
	 */
    public static void main(String[] args) {
        /* method main */
        // comment
        System.out.println("Hello" + " world");
    }

    /**                    */
    public void foo() {
        System.out.println("Hello" + /* inside comment */
        " world");
    }
}
