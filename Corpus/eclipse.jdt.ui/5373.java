package p;

import java.util.*;

class B {

    public void bar() {
        List<A> people = new ArrayList();
        // Case1 w/o space
        Collections.sort(people, Comparator.comparing(A::<>newName));
        // Case2 with space
        Collections.sort(people, Comparator.comparing(A::<>newName));
    }
}

public class A {

    String newName() {
        return null;
    }
}
