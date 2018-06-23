import java.io.*;

public class A_testVarArg_in {

    public void checkTrue() {
        // generalize i
        Integer i = linesPass("B", "A");
    }

    private Integer linesPass(String... lines) {
        return 1;
    }
}
