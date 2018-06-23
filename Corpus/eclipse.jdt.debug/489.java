public class ThrowsException {

    public static void main(String[] args) {
        ThrowsException anObject = new ThrowsException();
        try {
            anObject.throwBaby();
        } catch (TestException ie) {
        }
        anObject.throwBaby();
    }

    public void throwBaby() {
        throw new TestException();
    }
}
