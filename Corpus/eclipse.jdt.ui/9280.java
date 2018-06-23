// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class Test {

    static int m(int x) {
        return x--;
    }
}

enum E implements  {

    // [1] 
    E_C1(Test::<>m) {
    }
    ;

     E(FI fi) {
    }
}

@FunctionalInterface
interface FI {

    int foo(int x);
}
