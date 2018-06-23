package p;

interface I1 {

    void m(/*target*/
    int i);
}

interface I2 {

    void m(/*ripple*/
    int integer);
}

class I implements I1, I2 {

    void m(/*ripple*/
    int in);
}
