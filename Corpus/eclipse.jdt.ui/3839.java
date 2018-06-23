package rewrite_in;

public class TestClassFile {

    int m(String arg) {
        return Integer/*]*/
        .parseInt(/*[*/
        arg);
    }
}
