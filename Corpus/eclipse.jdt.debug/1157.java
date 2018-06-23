import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * WatchItemTests
 */
public class WatchItemTests {

    public Vector fVector = null;

    public Map fMap = null;

    public static void main(String[] args) {
        WatchItemTests test = new WatchItemTests();
        test.fillVector();
        test.fillMap();
    }

    public  WatchItemTests() {
    }

    public void fillVector() {
        fVector = new Vector();
        for (int i = 0; i < 100; i++) {
            fVector.add(new Integer(i));
        }
    }

    public void fillMap() {
        fMap = new HashMap();
        Iterator iterator = fVector.iterator();
        while (iterator.hasNext()) {
            Integer i = (Integer) iterator.next();
            fMap.put(i, i.toString());
        }
    }
}
