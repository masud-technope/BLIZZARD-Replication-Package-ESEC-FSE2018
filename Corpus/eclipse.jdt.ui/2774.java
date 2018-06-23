package p;

import java.util.*;

class B {

    public void bar() {
        List<A> people = new ArrayList();
        // Case1 w/o space
        Collections.sort(people, Comparator.comparing(A::<>m));
        // Case2 with space
        Collections.sort(people, Comparator.comparing(A::<>m));
    }
}

public class A {

    String m() {
        return null;
    }
}
