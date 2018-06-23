package tests;

public class QualifiedTests {

    static {
        p.p.ATest aQualifiedTest;
        q.A aQualified;
    }

    static {
        p.p.ATest aQualifiedTest;
        //unreadable
        q.A aQualified;
    }
}
