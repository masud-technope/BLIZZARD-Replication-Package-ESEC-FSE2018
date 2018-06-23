package p;

class A {

    A field;

    /**
	 * @see A # field
	 * @see A # fiel\u0064
	 * @see #fiel\u0064
	 */
     A(A a) {
        field = a.field;
        setField(getField());
    }

    A getField() {
        return field;
    }

    public void setField(A field) {
        /*TODO: create Getter*/
        this.field = field;
    }
}
