public class LabelTest {

    public static void main(String[] args) {
        label1: {
            System.out.println("Label 1");
        }
        label2: {
            label3: lable4: System.out.println("Label 2");
        }
    }
}
