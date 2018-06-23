public class GotNewName {

    public static void main(String[] args) {
        GotNewName anObject = new GotNewName();
        try {
            anObject.throwBaby();
        } catch (NullPointerException ne) {
        }
        anObject.killTime();
        try {
            anObject.throwBaby();
        } catch (NullPointerException ne) {
        }
        try {
            anObject.throwAnotherBaby();
        } catch (IllegalArgumentException ie) {
        }
    }

    public void throwBaby() {
        throw new NullPointerException();
    }

    public void throwAnotherBaby() {
        throw new IllegalArgumentException();
    }

    public void killTime() {
        int j = 0;
        while (j < 1000) {
            j++;
        }
    }
}
