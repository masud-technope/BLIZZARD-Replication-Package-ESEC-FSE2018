class B {

    /**
	 * org.eclipse.TestTestPattern
	 * (org.eclipse.TestPattern)
	 * borg.eclipse.TestPattern
	 */
    void f() {
    }

    /*
	 * org.eclipse.TestTestPattern
	 * borg.eclipse.TestTestPattern
	 * rg.eclipse.TestTestPattern
	 * <org.eclipse.TestTestPattern>
	 * <org.eclipse.TestPatternTest>
	 * 
	 * org.eclipse. TestPattern
	 * org.eclipse .TestPattern
	 * 	x.TestPattern
	 */
    void f1() {
        //borg.TestPattern //borg.eclipse.TestPattern
        f1();
        //org.eclipse.TestTestPattern
        String g = "ork.TestPattern";
        String g2 = "org.eklipse.TestPattern";
        String g3 = "org.eclipse.TestPatternMatching";
    }
    /*
	 * #org.eclipse.TestPattern
	 * org.eclipse.TestPattern#
	 * 
	 * $org.eclipse.TestPattern
	 * org.eclipse.TestPattern$
	 * 
	 * 1org.eclipse.TestPattern
	 * org.eclipse.TestPattern1
	 * 
	 * *org.eclipse.TestPattern
	 * org.eclipse.TestPattern*
	 */
}
