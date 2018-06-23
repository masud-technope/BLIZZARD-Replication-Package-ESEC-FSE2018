package classes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @since
 */
public class Test6<E extends ArrayList<String>> extends Test6Abstract<String> implements Iterable<Map<String, String>> {

    public Iterator<Map<String, String>> iterator() {
        return null;
    }
}

class Test6Abstract<T> {
}
