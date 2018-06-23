public class InstanceFilterObject {

    public int field = 0;

    public boolean executedSimpleMethod = false;

    public void simpleMethod() {
        System.out.println("simpleMethod");
        executedSimpleMethod = true;
    }

    public int accessField() {
        int y = field;
        return field;
    }

    public void modifyField(int value) {
        field = value;
    }

    public void throwException() {
        throw new NullPointerException();
    }

    public static void main(String[] args) {
        InstanceFilterObject object1 = new InstanceFilterObject();
        InstanceFilterObject object2 = new InstanceFilterObject();
        object2.simpleMethod();
        object1.simpleMethod();
        object2.accessField();
        object1.accessField();
        object1.modifyField(23);
        object2.modifyField(45);
        try {
            object2.throwException();
        } catch (NullPointerException e) {
        }
        try {
            object1.throwException();
        } catch (NullPointerException e) {
        }
    }
}
