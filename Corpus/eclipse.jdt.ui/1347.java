public class A_testVarArg_in {

    @XSet(value = { @X })
    public // generalize String
    String foo() {
        return "";
    }
}

@interface XSet {

    public X[] value();
}

@interface X {
}
