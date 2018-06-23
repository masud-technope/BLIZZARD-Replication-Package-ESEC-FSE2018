package p;

class A {

    /**
	 * @see #e()
	 * @see #\u0065()
	 * @see A#\u0065()
	 */
    static void e() {
        e();
        e();
        new A().e();
        A.e();
        new A().e();
    }
}
