public class FinalBreakpointLocations {

    public int bar = 0;

    public final int foo = 0;

    public final FinalBreakpointLocations ft1 = new FinalBreakpointLocations() {

        public void method() {
            //bp here
            System.out.println("ft1");
        }

        ;

        public void method2() {
            final FinalBreakpointLocations ftinner = new FinalBreakpointLocations() {

                public void method() {
                    //bp here
                    System.out.println("ftinner");
                }

                ;
            };
        }
    };

    public final FinalBreakpointLocations ft2;

    public  FinalBreakpointLocations() {
        ft2 = new FinalBreakpointLocations() {
        };
    }
}
