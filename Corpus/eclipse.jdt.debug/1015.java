interface A {

    default int getOne() {
        return 1;
    }
}

interface B {

    default int getOne() {
        return 2;
    }
}

public class EvalIntfSuperDefault implements A, B {

    public int getOne() {
        //bp here and inspect B.super.getOne(), ensuring it evaluates to 2
        return 3;
    }

    public static void main(String[] args) {
        EvalIntfSuperDefault i = new EvalIntfSuperDefault();
        System.out.println(i.getOne());
    }
}
