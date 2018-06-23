public class InspectTests {

    static {
        Outer s = new Outer() {

            char get() {
                return //bp here
                getchar();
            }

            char getchar() {
                return 's';
            }
        };
        s.get();
    }

    Outer a = new Outer() {

        char get() {
            return //bp here
            getchar();
        }

        char getchar() {
            return 'a';
        }
    };

    void m1() {
        Outer b = new Outer() {

            char get() {
                return //bp here
                getchar();
            }

            char getchar() {
                return 'b';
            }
        };
        b.get();
    }

    public static void main(String[] args) {
        InspectTests it = new InspectTests();
        it.a.get();
        it.m1();
    }
}

class Outer {

    char get() {
        return getchar();
    }

    char getchar() {
        return 'x';
    }
}
