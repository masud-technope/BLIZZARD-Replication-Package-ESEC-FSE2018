package p;

public class A {

    class B {

        @Deprecated
        @Stuff
        private static final String TAG1 = "tag1", TAG2, TAG_END = "z" + 9;
    }

    String X = B.TAG1, Y = B.TAG2;
}
