package p;

class Test {

    public int[] foo() {
        return null;
    }

    public void bar(Test test) {
        // refactor this
        int[] i = test.foo();
    }
}
