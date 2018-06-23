package targets.model.enumfields;

public enum EnumWithFields implements  {

    CONST() {
    }
    ;

    private int field = 0;

    private void setField(int param) {
        this.field = param;
    }
}
