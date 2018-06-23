package p;

class TestInlineMethodReference1 {

    private Object fun1() {
        // [1]
        Object fi1 = (FI) this::<>m;
        Object fi2;
        // [2]	
        fi2 = (FI) this::<>m;
        // [3]
        Object[] a = new Object[] { (FI) this::<>m, (FI) this::<>m };
        // [4]
        Object[][] b = new Object[][] { { (FI) this::<>m, (FI) this::<>m }, { (FI) this::<>m } };
        // [5]
        Object[] c = { (FI) this::<>m, (FI) this::<>m };
        // [6]
        Object[][] d = { { (FI) this::<>m }, { (FI) this::<>m } };
        // [7]
        int x1 = fun2((FI) this::<>m);
        // [8]
        TestInlineMethodReference1 c1 = new TestInlineMethodReference1((FI) this::<>m);
        // [9]
        F f1 = ( fi_p) -> ((FI) this::<>m);
        F f2 = ( fi_p) -> {
            // [10]
            return (FI) this::<>m;
        };
        // [11]
        f1.bar((FI) this::<>m);
        // [12]
        Object fi4 = true ? (FI) this::<>m : (FI) this::<>m;
        // [13]
        return (FI) this::<>m;
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
