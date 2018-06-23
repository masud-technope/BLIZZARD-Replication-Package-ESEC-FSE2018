package p;

class Test {

    public int[] foo() {
        return null;
    }

    public void bar(Test test) {
        int[] temp = test.foo();
        // refactor this
        int[] i = temp;
    }
}
