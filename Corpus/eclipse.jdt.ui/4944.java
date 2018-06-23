package p;

/**
 * @see #ident()
 * @see I#ident()
 * @see p.I#ident()
 */
@I(ident = "X")
@interface I {

    @I()
    String ident() default IDefault.NAME;

    @I
    interface IDefault {

        @I(ident = IDefault.NAME)
        public final String NAME = "Me";
    }
}
