/*
 * package g1.t.s.ref is the package to define types which contain
 * references (ref) to generic (g1) types (t) which have only one single (s) type parameter
 */
package g1.t.s.ref;

import g1.t.s.def.Generic;

/*
 * This type is used to test reference to member type defined in generic type.
 */
public class R4 {

    // Simple name
    public Generic.Member gen;

    public Generic<Object>.Member<Object> gen_obj;

    public Generic<Exception>.Member<Exception> gen_exc;

    public Generic<?>.Member<?> gen_wld;

    public Generic<? extends Throwable>.Member<? extends Throwable> gen_thr;

    public Generic<? super RuntimeException>.Member<? super RuntimeException> gen_run;

    // Qualified name
    public g1.t.s.def.Generic.Member qgen;

    public g1.t.s.def.Generic<Object>.Member<Object> qgen_obj;

    public g1.t.s.def.Generic<Exception>.Member<Exception> qgen_exc;

    public g1.t.s.def.Generic<?>.Member<?> qgen_wld;

    public g1.t.s.def.Generic<? extends Throwable>.Member<? extends Throwable> qgen_thr;

    public g1.t.s.def.Generic<? super RuntimeException>.Member<? super RuntimeException> qgen_run;
}
