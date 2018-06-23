//14, 12, 14, 15
package p;

import static p.Color.RED;

enum Color implements  {

    RED() {
    }
    , BLUE() {
    }
    , YELLOW() {
    }
    ;

    public static final Color fColor = RED;
}

class ColorUser {

    void use() {
        Color c = Color.fColor;
        c = RED;
        switch(c) {
            //extract constant "RED"
            case RED:
                break;
            default:
                break;
        }
    }
}
