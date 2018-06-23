import java.io.*;

public class A_testVarArg_in {

    public void checkTrue() {
        // generalize i
        Object i = linesPass("B", "A");
    }

    private Integer linesPass(String... lines) {
        return 1;
    }
}
