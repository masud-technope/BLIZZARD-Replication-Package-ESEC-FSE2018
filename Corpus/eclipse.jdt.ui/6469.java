// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class TestInlineMethodRef {

    private static int m(int x) {
        return x++;
    }

    static {
        // [1]	
        FI a = TestInlineMethodRef::<>m;
        FI b;
        // [2]		
        b = TestInlineMethodRef::<>m;
    }

    private FI fun1() {
        // [3]
        return TestInlineMethodRef::<>m;
    }

    // [4]
    FI[] c = new FI[] { TestInlineMethodRef::<>m, TestInlineMethodRef::<>m };

    // [5]
    FI[][] d = new FI[][] { { TestInlineMethodRef::<>m, TestInlineMethodRef::<>m }, { TestInlineMethodRef::<>m } };

    // [6]
    FI[] e = { TestInlineMethodRef::<>m, TestInlineMethodRef::<>m };

    // [7]
    FI[][] f = { { TestInlineMethodRef::<>m }, { TestInlineMethodRef::<>m } };

    // [8]
    int g = fun2(TestInlineMethodRef::<>m);

    // [9]
    TestInlineMethodRef h = new TestInlineMethodRef(TestInlineMethodRef::<>m);

    private int fun2(FI fi) {
        return 0;
    }

    public  TestInlineMethodRef(FI fi) {
    }

    private void fun3() {
        // [10]
        F f1 = ( fi_p) -> TestInlineMethodRef::<>m;
        F f2 = ( fi_p) -> {
            // [11]
            return TestInlineMethodRef::<>m;
        };
        boolean flag = true;
        // [12]
        FI fi4 = flag ? TestInlineMethodRef::<>m : TestInlineMethodRef::<>m;
    }
}

@FunctionalInterface
interface FI {

    int foo(int x);
}

@FunctionalInterface
interface F {

    FI bar(Object o);
}
