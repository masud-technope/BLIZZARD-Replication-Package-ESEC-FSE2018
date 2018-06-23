// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class TestInlineLambda {

    static {
        // [1]	
        FI a =  x -> x++;
        FI b;
        // [2]		
        b =  x -> x++;
    }

    private FI fun1() {
        // [3]
        return  x -> x++;
    }

    // [4]
    FI[] c = new FI[] {  x -> x++,  x -> x++ };

    // [5]
    FI[][] d = new FI[][] { {  x -> x++,  x -> x++ }, {  x -> x++ } };

    // [6]
    FI[] e = {  x -> x++,  x -> x++ };

    // [7]
    FI[][] f = { {  x -> x++ }, {  x -> x++ } };

    // [8]
    int g = fun2( x -> x++);

    // [9]
    TestInlineLambda h = new TestInlineLambda( x -> x++);

    private int fun2(FI fi) {
        return 0;
    }

    public  TestInlineLambda(FI fi) {
    }

    private void fun3() {
        // [10]
        F f1 = ( fi_p) ->  x -> x++;
        F f2 = ( fi_p) -> {
            // [11]
            return  x -> x++;
        };
        boolean flag = true;
        // [12]
        FI fi4 = flag ?  x -> x++ :  x -> x++;
    }

    enum E implements  {

        // [13]
        E_C1( x -> x++) {
        }
        ;

         E(FI fi) {
        }
    }
}

enum E1 implements  {

    // [14]
    E_C1( x -> x++) {
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
