package p;

@interface A {

    String value() default "";
}

@interface Main {

    A child() default @A("Void");
}

@Main(child = @/*test*/
A(""))
@A
class Client {

    @Deprecated
    @Main()
    @A()
    void bad() {
        @A final int local = 0;
    }
}
