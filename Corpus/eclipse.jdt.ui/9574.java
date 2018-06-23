package p;

class TestInlineMethodReference0 {

    private FI fun1() {
        // [1]
        FI fi1 = this::<>m;
        FI fi2;
        // [2]	
        fi2 = this::<>m;
        // [3]
        FI[] a = new FI[] { this::<>m, this::<>m };
        // [4]
        FI[][] b = new FI[][] { { this::<>m, this::<>m }, { this::<>m } };
        // [5]
        FI[] c = { this::<>m, this::<>m };
        // [6]
        FI[][] d = { { this::<>m }, { this::<>m } };
        // [7]
        int x1 = fun2(this::<>m);
        // [8]
        TestInlineMethodReference0 c1 = new TestInlineMethodReference0(this::<>m);
        // [9]
        F f1 = ( fi_p) -> this::<>m;
        F f2 = ( fi_p) -> {
            // [10]
            return this::<>m;
        };
        // [11]
        f1.bar(this::<>m);
        // [12]
        FI fi4 = true ? this::<>m : this::<>m;
        // [13]
        return this::<>m;
    }

    private int fun2(FI fi) {
        return 0;
    }

    public  TestInlineMethodReference0(FI fi) {
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

    FI bar(FI fi);
}
