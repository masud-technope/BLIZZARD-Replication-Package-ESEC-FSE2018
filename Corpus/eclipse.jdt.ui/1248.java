// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class TestInlineMethodRef_Cast {

    private static int m(int x) {
        return x++;
    }

    private FI fun3() {
        F a =  o -> null;
        return // [1]
        a.bar((FI) TestInlineMethodRef_Cast::<>m);
    }

    private Object fun5() {
        // [2]
        Object o = (FI) TestInlineMethodRef_Cast::<>m;
        F fx = ( z) -> {
            z = (FI) TestInlineMethodRef_Cast::<>m;
            return null;
        };
        Object fi2;
        // [4]
        fi2 = (FI) TestInlineMethodRef_Cast::<>m;
        // [5]
        Object[] b = new Object[] { (FI) TestInlineMethodRef_Cast::<>m, (FI) TestInlineMethodRef_Cast::<>m };
        // [6]
        Object[][] c = new Object[][] { { (FI) TestInlineMethodRef_Cast::<>m }, { (FI) TestInlineMethodRef_Cast::<>m } };
        // [7]
        Object[] d = { (FI) TestInlineMethodRef_Cast::<>m, (FI) TestInlineMethodRef_Cast::<>m };
        // [8]
        Object[][] e = { { (FI) TestInlineMethodRef_Cast::<>m }, { (FI) TestInlineMethodRef_Cast::<>m } };
        // [9]
        System.out.println((FI) TestInlineMethodRef_Cast::<>m);
        // [10]
        Object fi4 = true ? (FI) TestInlineMethodRef_Cast::<>m : (FI) TestInlineMethodRef_Cast::<>m;
        // [11]
        System.out.println(true ? (FI) TestInlineMethodRef_Cast::<>m : (FI) TestInlineMethodRef_Cast::<>m);
        // [12]
        int x2 = ((FI) TestInlineMethodRef_Cast::<>m).foo(10);
        // [13]
        return (FI) TestInlineMethodRef_Cast::<>m;
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
