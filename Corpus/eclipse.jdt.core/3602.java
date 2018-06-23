package test0027;

public enum X implements  {

    PENNY(1) {
    }
    , NICKEL(5) {
    }
    , DIME(10) {
    }
    , QUARTER(25) {
    }
    ;

     X(int val) {
        this.val = val;
    }

    private final int val;

    public int val() {
        return val;
    }
}
