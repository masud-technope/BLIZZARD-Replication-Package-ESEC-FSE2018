package codeManipulation;

public class bug {

    private static int dummyFct3() {
        return 3;
    }

    private static int dummyFct2() {
        return 3;
    }

    private static void pipo() {
        int z = bug.dummyFct3(), y = bug.dummyFct2();
    }
}
