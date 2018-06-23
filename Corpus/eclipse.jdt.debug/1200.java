public class LocalVariablesTests {

    public static void nop() {
    // used to allow breakpoint on otherwise empty lines
    }

    public static void simpleTest() {
        // should see no local variables here
        nop();
        int i1 = 0;
        // should see 1 local variable: i1
        nop();
        int i2 = 1;
        // should see 2 local variables: i1 && i2
        nop();
    }

    public static void outerMethod() {
        int i1 = 0;
        innerMethod();
        // i1 visible and i1==0, i2 not visible
        int i2 = 1;
        nop();
    }

    public static void innerMethod() {
        int i2 = 7;
        // i1 not visible in the top stack frame, i2 visible
        nop();
    }

    public static void testFor() {
        // should see no variable
        nop();
        for (int i = 0; i < 1; i++) {
            // should see i
            nop();
            for (int j = 0; j < 1; j++) {
                // 	should see i, j
                nop();
                Object obj = null;
                // should see i, j, obj
                nop();
                obj = "foo";
            }
        }
        // should not see i and j
        nop();
    }

    public static void testIf(boolean cond) {
        if (cond) {
            Object ifObj = new String("true");
            nop();
        } else {
            Object elseObj = new String("false");
            nop();
        }
        nop();
    }

    public static void testWhile() {
        int i = 0;
        while (i < 1) {
            int j = i / 2;
            // should see i & j
            nop();
            i++;
        }
    }

    public static void testTryCatch() {
        try {
            String str = null;
            // should see str
            nop();
            str.length();
        } catch (NullPointerException ex) {
            nop();
        } finally {
            // should see str
            nop();
        }
    }

    public static void testAliasing() {
        String str1 = new String("value");
        String str2 = str1;
        nop();
    }

    public static void main(String[] args) {
        // @see LocalVariablesTests.testSimple()
        simpleTest();
        // @see LocalVariablesTests.testMethodCall()
        outerMethod();
        // @see LocalVariablesTests.testFor()
        testFor();
        // @see LocalVariablesTests.testIf()
        testIf(true);
        // @see LocalVariablesTests.testIf()
        testIf(false);
        // @see LocalVariablesTests.testWhile()
        testWhile();
        // @see LocalVariablesTests.testTryCatch()
        testTryCatch();
        // @see LocalVariablesTests.testAliasing()
        testAliasing();
    }
}
