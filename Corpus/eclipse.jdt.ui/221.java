package p;

public class E1 {

    public class E1Inner1 {

        public void foo() {
        }

        protected int n;
    }

    public class E1Inner2 {

        public void run() {
        }
    }

    /**
	 * @param e1Inner1
	 */
    public static void bar(E1Inner1 e1Inner1) {
        e1Inner1.foo();
    }

    public void foo() {
        E1Inner1 inner = new E1Inner1() {

            public void foo() {
                E1Inner2 inner2 = new E1Inner2() {

                    public void run() {
                        foo();
                    }
                };
            }
        };
    }

    {
        E1Inner1 inner1 = new E1Inner1();
        // <--------- invoke here
        E1.bar(inner1);
    }
}
