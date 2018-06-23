package p;

public enum TestEnum implements  {

    FIRST_ENUM("first type", new SomeClass(), new OtherEnumType[] { OtherEnumType.FOO }) {
    }
    , SECOND_ENUM("second type", new SomeClassOtherClass(), new OtherEnumType[] { OtherEnumType.BAR }) {
    }
    , THIRD_ENUM("third type", new SomeThirdClass(), new OtherEnumType[] { OtherEnumType.BAZ }) {
    }
    , FOURTH_ENUM("fourth type", new YetAnotherClass(), new OtherEnumType[] { OtherEnumType.FOOBAR, OtherEnumType.FOO, OtherEnumType.FOOBARBAZ, OtherEnumType.LONGERFOOBARBAZ, OtherEnumType.REALLYLONGFOOBARBAZ, OtherEnumType.MORELETTERSINHERE }) {
    }
    ;

    /* data members and methods go here */
     TestEnum(String s, Cls s1, OtherEnumType[] e) {
    }
}

enum OtherEnumType implements  {

    FOOBAR() {
    }
    , FOOBARBAZ() {
    }
    , FOO() {
    }
    , LONGERFOOBARBAZ() {
    }
    , REALLYLONGFOOBARBAZ() {
    }
    , MORELETTERSINHERE() {
    }
    , BAR() {
    }
    , BAZ() {
    }
    ;
}

class Cls {
}

class SomeClass extends Cls {
}

class SomeThirdClass extends Cls {
}

class SomeClassOtherClass extends Cls {
}

class YetAnotherClass extends Cls {
}
