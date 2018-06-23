package p;

class A {

    enum Enum implements  {

        ONE() {
        }
        ;
    }

    Enum f;

    public Enum getF() {
        return f;
    }

    public void setF(Enum f) {
        this.f = f;
    }
}
