package p;

class A {

    public  A() {
        new Object() {
        };
        System.out.println(new Inner(this).getName());
    }

    public String getTopName() {
        return "Top";
    }

    public static void main(String[] argv) {
        new A();
    }
}
