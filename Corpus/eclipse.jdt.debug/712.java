/**
 * Print variable number of 80char (+2) lines of output
 */
public class Console80Chars {

    public static void main(String[] args) {
        int lines = Integer.parseInt(args[0]);
        System.err.println(" - THE BEGINING - ");
        for (int i = 0; i < lines; i++) {
            System.out.println("0---------1--------2--------3-------4--------5--------6--------7--------8");
        }
        System.err.println(" - THE END - ");
    }
}
