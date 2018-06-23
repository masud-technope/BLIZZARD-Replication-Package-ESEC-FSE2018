package g2.f.s.def;

import g1.t.s.def.Generic;

public class R2 {

    {
        Generic.Member member = new Generic().new Member();
        member.m.toString();
    }

    {
        Generic<Object>.Member<Object> member = new Generic<Object>().new Member();
        member.m.toString();
    }

    {
        Generic<Exception>.Member<Exception> member = new Generic<Exception>().new Member();
        member.m.toString();
    }

    {
        Generic<?>.Member<?> member = new Generic<?>().new Member();
        member.m.toString();
    }

    {
        Generic<? extends Throwable>.Member<? extends Throwable> member = new Generic<Exception>().new Member();
        member.m.toString();
    }

    {
        Generic<? extends Throwable>.Member<? extends Throwable> member = new Generic<? extends Throwable>().new Member();
        member.m.toString();
    }

    {
        Generic<? super RuntimeException>.Member<? super RuntimeException> member = new Generic<Exception>().new Member();
        member.m.toString();
    }

    {
        Generic<? super RuntimeException>.Member<? super RuntimeException> member = new Generic<? super RuntimeException>().new Member();
        member.m.toString();
    }
}
