package classes;

/**
 * Tests that various arrangements of non-generic classes are scanned properly 
 */
public class Test3 {

    class Inner {
    }

    static class Inner2 {

        class Inner3 {
        }
    }
}

class Test3Outer {

    static class Inner {
    }
}
