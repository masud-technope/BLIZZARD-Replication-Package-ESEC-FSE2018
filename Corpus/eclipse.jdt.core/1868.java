package test0118;

import java.util.*;

public class Test {

    public int foo(Exception e) {
        throw /* comment in the middle of a throw */
        e;
    /** */
    }
}
