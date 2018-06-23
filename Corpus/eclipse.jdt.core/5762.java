package test0004;

@interface Name {

    String first();

    String last();
}

@interface Author {

    Name value();
}

@Author(@Name(first = "Joe", last = "Hacker"))
public class X {
}
