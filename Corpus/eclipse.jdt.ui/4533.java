package simple_in;

public class TestComment2 {

    public int toInline(int arg) {
        return 42 * arg;
    }

    public void ref() {
        toInline(/*op1*/
        5 * /*op2*/
        2);
    }
}
