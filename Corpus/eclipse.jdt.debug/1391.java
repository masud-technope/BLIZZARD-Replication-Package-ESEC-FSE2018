public class StepFilterTwo {

    private StepFilterThree sf3;

    public  StepFilterTwo() {
        sf3 = new StepFilterThree();
    }

    protected void go() {
        sf3.go();
    }

    void test() {
        for (int i = 0; i < 10; i++) ;
    }

    /**
	 * This test method should only be called by the contributed step filter tests
	 * @see TestContributedStepFilter
	 */
    void contributed() {
    }
}
