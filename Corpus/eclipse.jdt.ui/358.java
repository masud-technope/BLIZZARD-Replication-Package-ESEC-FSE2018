package r;

//(-import)
import p.A;
//-import (invalid)
import p.A.Inner;

public class B {

    //->Inner
    Inner iFromB;

    //->Inner
    A.Inner iiFromB;

    //->Inner
    p.A.Inner iiiFromB;
}
