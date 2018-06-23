public class A {

    public void setBorderType(JComponent c, int borderType) {
        myOtherData.setBorderType(borderType);
        if (c != null) {
            switch(borderType) {
                case // none
                0:
                    c.setBorder(null);
                    break;
                case // line
                1:
                    c.setBorder(BorderFactory.createLineBorder(Color.black));
                    break;
                case // bevel lowered
                3:
                    c.setBorder(BorderFactory.createLoweredBevelBorder());
                    break;
                case // bevel raised
                4:
                    c.setBorder(BorderFactory.createRaisedBevelBorder());
                    break;
                case // etched
                5:
                    c.setBorder(BorderFactory.createEtchedBorder());
                    break;
                default:
            }
        // end switch
        }
    }
}
