//selection: 10, 21, 11, 40
//name: is -> ints
package simple;

public class Formatting1 {

    public void method1() {
        method2(new //newline
        int[] { 1, 2, /*important comment*/
        3 });
    }

    public void method2(int[] ints) {
        doSomething(ints);
    }

    private void doSomething(int[] is) {
    }
}
