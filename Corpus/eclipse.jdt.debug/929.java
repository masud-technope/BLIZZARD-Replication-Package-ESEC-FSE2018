public class VariableChanges {

    public static void main(String[] args) {
        new VariableChanges().test();
    }

    int count = 0;

    private void test() {
        int i = 0;
        count++;
        i++;
        count++;
        i++;
        count++;
    }
}
