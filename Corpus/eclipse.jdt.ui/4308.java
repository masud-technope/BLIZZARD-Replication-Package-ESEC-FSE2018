package p;

class Generic<G> {

    void take(/*target*/
    G g) {
    }
}

class Impl extends Generic<Integer> {

    void take(/*ripple*/
    Integer g) {
    }
}

class Impl2 extends Generic<String> {

    void take(/*ripple*/
    String g) {
    }
}
