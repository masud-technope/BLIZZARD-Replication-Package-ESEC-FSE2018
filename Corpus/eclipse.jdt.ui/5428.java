package rewrite_in;

public class TestSwitchParameters {

    void m(String name, int count) {
        System.out.println(name + count);
    }

    static void better(int count, String name) {
        System.out.println(name);
        System.out.println(": ");
        System.out.println(count);
    }
}

class SwitchParametersClient {

    void user(TestSwitchParameters tsp) {
        tsp.m("question", 17);
        /*]*/
        tsp.m(/*[*/
        "answer", 42);
    }
}
