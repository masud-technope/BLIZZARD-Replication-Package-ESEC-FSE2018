public class ByteArrayTests {

    public byte[] bytes = null;

    public static void main(String[] args) {
        ByteArrayTests tests = new ByteArrayTests();
        tests.existingArray();
        tests.nullArray();
    }

    public void existingArray() {
        bytes = new byte[10000];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i % 128);
        }
        System.out.println(bytes);
    }

    public void nullArray() {
        bytes = null;
        System.out.println(bytes);
    }
}
