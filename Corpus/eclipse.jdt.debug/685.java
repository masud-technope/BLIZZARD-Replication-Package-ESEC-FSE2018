/**
 * StackTraces
 */
public class StackTraces {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            try {
                String fred = null;
                fred.toString();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
