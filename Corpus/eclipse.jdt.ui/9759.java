//8, 32, 8, 44
package p;

import java.util.Collection;

class A {

    private void test(Collection c) {
        for (Object o : c) {
            final String temp = o.toString();
            System.out.println(temp);
        }
    }
}
