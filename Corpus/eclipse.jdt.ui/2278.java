// 5, 28 -> 5, 30  replaceAll = true, removeDeclaration = true
package p;

class TestClass extends SuperClass {

    private static int m(int x) {
        return x++;
    }

    {
        // [1]
        bar(0, (FI) TestClass::<>m);
        // [2]
        super.bar(0, (FI) TestClass::<>m);
    }

     TestClass() {
        this(// [3]
        0, // [3]
        (FI) TestClass::<>m);
    }

     TestClass(int i, FI a) {
        super(// [4]
        i, // [4]
        (FI) TestClass::<>m);
    }

     TestClass(int i, FX b) {
    }

    {
        // [5]
        new TestClass(0, (FI) TestClass::<>m);
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

@FunctionalInterface
interface FI {

    int foo(int x);
}

@FunctionalInterface
interface FX {

    int foo(String s);
}
