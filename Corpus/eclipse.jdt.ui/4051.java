package p;

class A {

    enum Enum implements  {

        ONE() {
        }
        ;
    }

    Enum g;

    public Enum getG() {
        return g;
    }

    public void setG(Enum f) {
        this.g = f;
    }
}
