package enum_in;

public enum TestBasic implements  {

    A() {
    }
    , B() {
    }
    ;

    void foo() {
        /*]*/
        /*[*/
        bar();
    }

    void bar() {
        System.out.println("Hello Eclipse");
    }
}
