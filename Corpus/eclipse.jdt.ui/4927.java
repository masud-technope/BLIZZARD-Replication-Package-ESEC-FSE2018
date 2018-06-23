package p;

//(-import)
import other.C;

public class A {

    //+import r.B.Inner
    Inner i;

    //+import r.B
    A.Inner ii;

    p.A.Inner iii;

    public static int a;

    public static class Inner {

        //move to r.B
        Inner buddy;

        public  Inner(A.Inner other) {
            // ^ is direct access to enclosing type
            //+import other.C
            buddy = C.ii;
            int ia = a;
        }
    }
}
