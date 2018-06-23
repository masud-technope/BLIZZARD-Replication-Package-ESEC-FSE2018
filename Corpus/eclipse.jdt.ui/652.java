package p;

import p.Foo.Bar.FooBar;

public class Foo {

    /**
	 * 
	 */
    public static void bar() {
        FooBar.foo();
    }

    Bar.FooBar bb;

    static class Bar {

        static class FooBar {

            static // <--- invoke here
            void foo() {
            }
        }
    }

    Bar.FooBar getFooBar() {
        return null;
    }

    {
        Bar.FooBar b = new Bar.FooBar();
        // not ok:
        b.foo();
        getFooBar().foo();
        this.bb.foo();
        bb.foo();
        // ok:
        Foo.bar();
        Foo.bar();
        Foo.bar();
    }
}
