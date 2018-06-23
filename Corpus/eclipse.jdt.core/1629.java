public class ResolveDuplicateFieldDeclaration {

    class Inner {

        int /*1*/
        var;

        int /*1*/
        var;
    }

    class Inner {

        int /*2*/
        var;

        int /*2*/
        var;
    }
}
