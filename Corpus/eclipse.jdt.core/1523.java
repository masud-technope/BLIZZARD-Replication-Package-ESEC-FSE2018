public @interface X {

    @Marker
    public String value();

    @Marker
    String value2();

    @Marker
    public String value3();
}

@interface Marker {
}
