import java.util.Vector;

/**
 * A loop adding to a collection
 */
public class PerfLoop {

    public static void main(String[] args) {
        Vector v = new Vector(200);
        for (int i = 0; i < 100000; i++) {
            v.add(new Integer(i));
        }
    }
}
