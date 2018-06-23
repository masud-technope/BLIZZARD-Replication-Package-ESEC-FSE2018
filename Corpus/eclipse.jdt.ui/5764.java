package p;

class TestInlineLambda0 {

    private FI fun1() {
        // [1]
        FI fi1 =  x -> x++;
        FI fi2;
        // [2]	
        fi2 =  x -> x++;
        // [3]
        FI[] a = new FI[] {  x -> x++,  x -> x++ };
        // [4]
        FI[][] b = new FI[][] { {  x -> x++,  x -> x++ }, {  x -> x++ } };
        // [5]
        FI[] c = {  x -> x++,  x -> x++ };
        // [6]
        FI[][] d = { {  x -> x++ }, {  x -> x++ } };
        // [7]
        int x1 = fun2( x -> x++);
        // [8]
        TestInlineLambda0 c1 = new TestInlineLambda0( x -> x++);
        // [9]
        F f1 = ( fi_p) ->  x -> x++;
        F f2 = ( fi_p) -> {
            // [10]
            return  x -> x++;
        };
        // [11]
        f1.bar( x -> x++);
        // [12]
        FI fi4 = true ?  x -> x++ :  x -> x++;
        // [13]
        return  x -> x++;
    }

    private int fun2(FI fi) {
        return 0;
    }

    public  TestInlineLambda0(FI fi) {
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
