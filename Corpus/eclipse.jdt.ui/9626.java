package p;

class A {

    static int member;

    static {
        //member
        String doIt = "member";
        //A.member
        doIt = "A.member";
        //B. #member
        doIt = "B. #member";
        //p.A#member
        doIt = "p.A#member";
        //x.p.A#field
        String dont = "x.p.A#field";
        //xp.A.field
        dont = "xp.A.field";
        //B.field
        dont = "B.field";
    }
}
