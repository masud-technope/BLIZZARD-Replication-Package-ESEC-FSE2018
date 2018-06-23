package p;

class A {

    /**
	 * @see #getMe()
	 * @see #setMe(int)
	 */
    //use getMe and setMe to update fMe
    private int fMe;

    public int getMe() {
        return fMe;
    }

    /** @param me stored into {@link #fMe}*/
    public void setMe(int me) {
        fMe = me;
    }
}
