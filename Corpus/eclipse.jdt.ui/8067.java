// Move mA1 to parameter b, do not inline delegator
package p1;

import p2.B;

public class A {

    public void mA1() {
        B b = null;
        b.mB1();
        //test comment
        mA2();
        b.mB2();
        System.out.println(this);
    }

    public void mA2() {
    }
}
