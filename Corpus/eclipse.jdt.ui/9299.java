package p;

class TestInlineMethodReference0 {

    private FI fun1() {
        final FI fi = this::<>m;
        // [1]
        FI fi1 = fi;
        FI fi2;
        // [2]	
        fi2 = fi;
        // [3]
        FI[] a = new FI[] { fi, fi };
        // [4]
        FI[][] b = new FI[][] { { fi, fi }, { fi } };
        // [5]
        FI[] c = { fi, fi };
        // [6]
        FI[][] d = { { fi }, { fi } };
        // [7]
        int x1 = fun2(fi);
        // [8]
        TestInlineMethodReference0 c1 = new TestInlineMethodReference0(fi);
        // [9]
        F f1 = ( fi_p) -> fi;
        F f2 = ( fi_p) -> {
            // [10]
            return fi;
        };
        // [11]
        f1.bar(fi);
        // [12]
        FI fi4 = true ? fi : fi;
        // [13]
        return fi;
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
