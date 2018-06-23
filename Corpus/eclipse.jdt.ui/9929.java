// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class TestInlineLambda {

    public static final FI fi =  x -> x++;

    static {
        // [1]	
        FI a = fi;
        FI b;
        // [2]		
        b = fi;
    }

    private FI fun1() {
        // [3]
        return fi;
    }

    // [4]
    FI[] c = new FI[] { fi, fi };

    // [5]
    FI[][] d = new FI[][] { { fi, fi }, { fi } };

    // [6]
    FI[] e = { fi, fi };

    // [7]
    FI[][] f = { { fi }, { fi } };

    // [8]
    int g = fun2(fi);

    // [9]
    TestInlineLambda h = new TestInlineLambda(fi);

    private int fun2(FI fi) {
        return 0;
    }

    public  TestInlineLambda(FI fi) {
    }

    private void fun3() {
        // [10]
        F f1 = ( fi_p) -> fi;
        F f2 = ( fi_p) -> {
            // [11]
            return fi;
        };
        boolean flag = true;
        // [12]
        FI fi4 = flag ? fi : fi;
    }

    enum E implements  {

        // [13]
        E_C1(fi) {
        }
        ;

         E(FI fi) {
        }
    }
}

enum E1 implements  {

    // [14]
    E_C1(TestInlineLambda.fi) {
    }
    ;

     E1(FI fi) {
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
