package g2.f.m.def;

import g1.t.m.def.Generic;

public class R2 {

    {
        Generic.Member member = new Generic().new Member();
        member.m.toString();
    }

    {
        Generic<Object, Exception, RuntimeException>.Member<Object, Exception, RuntimeException> member = new Generic<Object, Exception, RuntimeException>().new Member();
        member.m.toString();
    }

    {
        Generic<Exception, Exception, RuntimeException>.Member<Exception, Exception, RuntimeException> member = new Generic<Exception, Exception, RuntimeException>().new Member();
        member.m.toString();
    }

    {
        Generic<?, ?, ?>.Member<?, ?, ?> member = new Generic<?, ?, ?>().new Member();
        member.m.toString();
    }

    {
        Generic<? extends Throwable, ? extends Exception, ? extends RuntimeException>.Member<? extends Throwable, ? extends Exception, ? extends RuntimeException> member = new Generic<Exception, RuntimeException, IllegalMonitorStateException>().new Member();
        member.m.toString();
    }

    {
        Generic<? extends Throwable, ? extends Exception, ? extends RuntimeException>.Member<? extends Throwable, ? extends Exception, ? extends RuntimeException> member = new Generic<? extends Throwable, ? extends Exception, ? extends RuntimeException>().new Member();
        member.m.toString();
    }

    {
        Generic<? super RuntimeException, ? super IllegalMonitorStateException, ? super IllegalMonitorStateException>.Member<? super RuntimeException, ? super IllegalMonitorStateException, ? super IllegalMonitorStateException> member = new Generic<Exception, RuntimeException, RuntimeException>().new Member();
        member.m.toString();
    }

    {
        Generic<? super RuntimeException, ? super IllegalMonitorStateException, ? super IllegalMonitorStateException>.Member<? super RuntimeException, ? super IllegalMonitorStateException, ? super IllegalMonitorStateException> member = new Generic<? super RuntimeException, ? super IllegalMonitorStateException, ? super IllegalMonitorStateException>().new Member();
        member.m.toString();
    }
}
