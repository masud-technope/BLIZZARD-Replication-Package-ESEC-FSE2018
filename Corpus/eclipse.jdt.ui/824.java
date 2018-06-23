package p;

class TestInlineMethodReference1 {

    private Object fun1() {
        final FI fi = this::<>m;
        // [1]
        Object fi1 = fi;
        Object fi2;
        // [2]	
        fi2 = fi;
        // [3]
        Object[] a = new Object[] { fi, fi };
        // [4]
        Object[][] b = new Object[][] { { fi, fi }, { fi } };
        // [5]
        Object[] c = { fi, fi };
        // [6]
        Object[][] d = { { fi }, { fi } };
        // [7]
        int x1 = fun2(fi);
        // [8]
        TestInlineMethodReference1 c1 = new TestInlineMethodReference1(fi);
        // [9]
        F f1 = ( fi_p) -> fi;
        F f2 = ( fi_p) -> {
            // [10]
            return fi;
        };
        // [11]
        f1.bar(fi);
        // [12]
        Object fi4 = true ? fi : fi;
        // [13]
        return fi;
    }

    private int fun2(Object fi) {
        return 0;
    }

    public  TestInlineMethodReference1(Object fi) {
    }

    int m(int x) {
        return x++;
    }
}

@FunctionalInterface
interface FI {

    int foo(int x);
}

@FunctionalInterface
interface F {

    Object bar(Object fi);
}
