public class ConsoleVariableLineLength {

    public static void main(String[] args) {
        int repeat = Integer.parseInt(args[0]);
        System.out.println("---- START ----");
        for (int i = 0; i < repeat; i++) {
            System.out.println("---------1---------2---------3---------4---------5---------6");
            System.out.println("---------1---------2---------3---------4---------5---------6---------7---------8");
            System.out.println("---------1---------2---------3---------4---------5---------6---------7---------8-");
            System.out.println("---------1---------2---------3---------4---------5---------6---------7---------8---------9");
        }
        System.out.println("---- END ----");
    }
}
