public class Test {

    public static <T> void service() {
    }

    public static void main(String[] args) {
        //XXX <<<<
        Test.<String>service();
        Test t = new Test();
        //XXX <<<<
        t.<String>service2();
        //XXX <<<<
        new Test().<String>service2();
    }

    public <T> void service3() {
    }

    public <T> void service2() {
        //XXX <<<<
        this.<T>service3();
    }
}
