package bugs_out;

public class Test_287378 {

    Test_287378 other;

    int x;

    protected void f() {
        /*A*/
        other.other.x = 5;
        int a = /*A*/
        other.other.x;
    }
}
