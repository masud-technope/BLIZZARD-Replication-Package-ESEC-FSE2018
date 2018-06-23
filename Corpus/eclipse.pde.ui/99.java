package classes;

/**
 * 
 */
public class Test4<List> {

    static class Inner<String> {

        class Inner2<E> {
        }
    }

    class Inner2<Map> {
    }
}

class Test4Outer<Integer> {

    static class Inner<Double> {
    }
}
