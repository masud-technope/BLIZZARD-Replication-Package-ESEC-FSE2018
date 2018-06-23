// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class TestClass extends SuperClass {

    {
        // [1]
        bar(0, (FI)  x -> x++);
        // [2]
        super.bar(0, (FI)  x -> x++);
    }

     TestClass() {
        this(// [3]
        0, // [3]
        (FI)  x -> x++);
    }

     TestClass(int i, FI a) {
        super(// [4]
        i, // [4]
        (FI)  x -> x++);
    }

     TestClass(int i, FX b) {
    }

    {
        // [5]
        new TestClass(0, (FI)  x -> x++);
    }

    void bar(int x, FX fx) {
        System.out.println();
    }
}

class SuperClass {

    public  SuperClass() {
    }

     SuperClass(int i, FI fi) {
    }

     SuperClass(int x, FX fx) {
    }

    void bar(int i, FI fi) {
    }

    void bar(int x, FX fx) {
    }
}

enum E implements  {

    // [6]
    EE(0, (FI)  x -> x++) {
    }
    ;

     E(int i, FI fi) {
    }

     E(int s, FX fl) {
    }
}

@FunctionalInterface
interface FI {

    int foo(int x);
}

@FunctionalInterface
interface FX {

    int foo(String s);
}
