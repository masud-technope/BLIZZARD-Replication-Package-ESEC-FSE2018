class Autoboxing {

    void m(int pi, Integer pBigI) {
        // vardeclFragment
        int i = pBigI;
        // vardeclFragment
        Number bigN = i;
        Integer bigI = null;
        // method invocation
        m(i, bigI);
        // method invocation
        m(bigI, bigI);
        // nested method invocation
        m(bigI, m(bigI, i));
        // nested method invocation
        m(bigI, m(i, bigI));
        // method return value
        m(bigI, foo());
        // assignment to Number
        bigN = pBigI;
        // assignment
        bigN = pi;
        // assignment
        bigN = i;
        // assignment to Integer
        bigI = pBigI;
        // assignment
        bigI = pi;
        // assignment
        bigI = i;
        // assignment to int
        i = pBigI;
        i = i + 1;
        i = pBigI + i;
        // conditionals and parenthesized expr
        i = true ? pBigI + i : bigI + (i + pBigI);
        // conditionals
        bigI = true ? pBigI + i : i + pBigI;
        bigI = true ? pBigI : i;
        bigI = true ? i : pBigI;
        // array creation
        int[] array = new int[bigI];
        // array assignment
        array[i] = bigI;
        // array access
        array[bigI] = i;
        // infix expression
        bigI = array[bigI + i];
        for (int index = bigI; index < pBigI; index += bigI) {
        // var decl fragments, infix comparisons
        }
        // multi var declarations
        int bar = bigI, foo = pBigI;
        // prefix expr
        i = -bigI;
        i = ~bigI;
        bigI = -42;
        // method return value
        bigI = foo();
        if (// comparison
        foo() == number())
            return;
    }

    int foo() {
        return 0;
    }

    Integer number() {
        return null;
    }
}
