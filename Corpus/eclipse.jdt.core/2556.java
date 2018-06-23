package testBug420894;

public class TestClass {

    // works fine
    SomeUndeclaredType<?>[] undeclaredField;

    public static void main(String[] args) {
        //ClassCastException
        SomeUndeclaratedType<?>[] undeclaredLocalDeclaration;
    }
}
