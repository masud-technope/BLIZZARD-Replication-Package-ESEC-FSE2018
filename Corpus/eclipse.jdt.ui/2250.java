package p;

class A {

    static int field;

    static {
        //field
        String doIt = "field";
        //A.field
        doIt = "A.field";
        //B. #field
        doIt = "B. #field";
        //p.A#field
        doIt = "p.A#field";
        //x.p.A#field
        String dont = "x.p.A#field";
        //xp.A.field
        dont = "xp.A.field";
        //B.field
        dont = "B.field";
    }
}
