package p;

public class A {

    /**
	 * @deprecated Use {@link #bar(String,String)} instead
	 */
    static void foo(/*abstract*/
    String /*aaa*/
    a, /*x*/
    String /*bar*/
    b) /*foo*/
    {
        bar(a, b);
    }

    //bar
    static void foo(/*abstract*/
    String /*aaa*/
    a, /*x*/
    String /*bar*/
    b) /*foo*/
    {
    }
    //bar
}
