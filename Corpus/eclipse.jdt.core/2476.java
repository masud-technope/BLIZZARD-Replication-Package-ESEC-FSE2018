package p6;

/* Test case for PR 1GKB9YH: ITPJCORE:WIN2000 - search for field refs - incorrect results */
public class A {

    protected int f;

    void m() {
        f++;
    }
}

class AA extends A {

    protected int f;
}

class B {

    A a;

    AA b;

    A ab = new AA();

    void m() {
        a.f = /*1*/
        0;
        b.f = /*2*/
        0;
        ab.f = /*3*/
        0;
    }
}
