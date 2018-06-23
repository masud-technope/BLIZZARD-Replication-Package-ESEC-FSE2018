// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class TestInlineLambda_Cast {

    public static final FI fi =  x -> x++;

    private FI fun3() {
        F a =  o -> null;
        return // [1]
        a.bar(fi);
    }

    private Object fun5() {
        // [2]
        Object o = fi;
        F fx = ( z) -> {
            // [3]
            z = fi;
            return null;
        };
        Object fi2;
        // [4]
        fi2 = fi;
        // [5]
        Object[] b = new Object[] { fi, fi };
        // [6]
        Object[][] c = new Object[][] { { fi }, { fi } };
        // [7]
        Object[] d = { fi, fi };
        // [8]
        Object[][] e = { { fi }, { fi } };
        // [9]
        System.out.println(fi);
        // [10]
        Object fi4 = true ? fi : fi;
        // [11]
        System.out.println(true ? fi : fi);
        // [12]
        int x2 = fi.foo(10);
        // [13]
        return fi;
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
