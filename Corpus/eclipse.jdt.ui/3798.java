// 15, 7, 15, 15
package p;

import a.A;

class C {

    b.A method() {
        return new b.A();
    }

    void failHere() {
        //extract local variable here
        method();
    }
}
