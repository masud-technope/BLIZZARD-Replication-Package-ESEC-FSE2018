package p;

interface I1 {

    void m(/*target*/
    List l);
}

interface I2 {

    void m(/*ripple*/
    List l);
}

class I implements I1, I2 {

    void m(/*ripple*/
    List l);
}
