package p;

@interface B {

    String value() default "";
}

@interface Main {

    B child() default @B("Void");
}

@Main(child = @/*test*/
B(""))
@B
class Client {

    @Deprecated
    @Main()
    @B()
    void bad() {
        @B final int local = 0;
    }
}
