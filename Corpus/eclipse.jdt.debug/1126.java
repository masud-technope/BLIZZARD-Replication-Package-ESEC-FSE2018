public class ConditionalStepReturn {

    public static void main(String[] args) {
        new ConditionalStepReturn().foo();
    }

    public void foo() {
        // BREAKPOINT HERE
        boolean bool = true;
        // CONDITIONAL BREAKPOINT HERE
        System.out.println("grah");
    }
}
