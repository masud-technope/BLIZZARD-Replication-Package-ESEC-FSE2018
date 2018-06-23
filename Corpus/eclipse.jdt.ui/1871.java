package p;

class A {

    /**
	 * @see #getYou()
	 * @see #setYou(int)
	 */
    //use getMe and setMe to update fMe
    private int fYou;

    public int getYou() {
        return fYou;
    }

    /** @param me stored into {@link #fYou}*/
    public void setYou(int me) {
        fYou = me;
    }
}
