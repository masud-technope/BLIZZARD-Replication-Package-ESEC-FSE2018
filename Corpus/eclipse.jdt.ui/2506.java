package p;

public abstract class A implements I {

    /* (non-Javadoc)
	 * @see p.I#x()
	 */
    public abstract void x();

    //	 TestRunListener implementation
    /* (non-Javadoc)
	 * @see p.I#y()
	 */
    public abstract void y();

    /* (non-Javadoc)
	 * @see p.I#z()
	 */
    public abstract // xx
    void z();

    /* (non-Javadoc)
	 * @see p.I#a()
	 */
    public abstract void a();

    /* (non-Javadoc)
	 * @see p.I#b()
	 */
    //abstract
    public abstract void b();

    //destruct
    /* (non-Javadoc)
	 * @see p.I#c()
	 */
    public abstract void c();

    //post
    /* (non-Javadoc)
	 * @see p.I#d()
	 */
    public abstract void d();
}
