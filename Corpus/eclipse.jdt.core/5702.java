public class X {

    int x() {
        @Marker int p;
        @Marker final int q;
        @Marker final int r;
        return 10;
    }

    Zork z;
}

@java.lang.annotation.Target(java.lang.annotation.ElementType.TYPE_PARAMETER)
@interface Marker {
}
