//4, 59 -> 4, 59  replaceAll == true, removeDeclaration == true
package p;

enum Letter implements  {

    A() {
    }
    , B() {
    }
    , C() {
    }
    ;
}

class EnumRef {

    Letter l = Letter.A;
}
