import java.util.Arrays;
import java.util.List;

public class EvalTest18 {

    public static void main(String[] args) {
        List<String> strings = Arrays.asList("One", "Two", "Three");
        System.out.println("Count of strings in stream from array =" + strings.stream().count());
    }
}
