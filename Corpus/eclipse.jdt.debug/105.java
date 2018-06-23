public class DefPkgReturnType {

    public static void main(String[] args) {
        new DefPkgReturnType().test();
    }

    private void test() {
        DefPkgReturnType object = new DefPkgReturnType();
        System.out.println(object.self());
    }

    protected DefPkgReturnType self() {
        return this;
    }
}
