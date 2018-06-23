public class X {

    class Inner extends Exception {
    }
}

class DD extends X.Inner {

     DD() {
        new X().super();
    }

    public static final boolean DEBUG = true;

    public void foo0() {
        try {
            d();
        } catch (X.Inner e) {
        }
    }

    protected void d() throws X.Inner {
        if (DEBUG)
            //<<SELECT AND EXTRACT
            throw new X().new Inner();
    }
}
