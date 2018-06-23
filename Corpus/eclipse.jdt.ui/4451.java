package p;

interface I1 {

    void m(/*target*/
    int i);
}

interface I2 {

    void m(/*ripple*/
    int integer);
}

interface I extends I1, I2 {
}
