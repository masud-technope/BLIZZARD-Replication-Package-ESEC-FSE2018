package test0047;

public enum X implements  {

    TOTO() {
    }
    ;

    private static final long[] overflows = { // unused
    0, Long.MAX_VALUE / 1000, Long.MAX_VALUE / (1000 * 1000), Long.MAX_VALUE / (1000 * 1000 * 1000) };
}
