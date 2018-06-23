public class A {

    public void setBorderType(JComponent c, int borderType) {
        myOtherData.setBorderType(borderType);
        if (c != null) {
            switch(borderType) {
                // none
                case 0:
                    c.setBorder(null);
                    break;
                // line
                case 1:
                    c.setBorder(BorderFactory.createLineBorder(Color.black));
                    break;
                // bevel lowered
                case 3:
                    c.setBorder(BorderFactory.createLoweredBevelBorder());
                    break;
                // bevel raised
                case 4:
                    c.setBorder(BorderFactory.createRaisedBevelBorder());
                    break;
                // etched
                case 5:
                    c.setBorder(BorderFactory.createEtchedBorder());
                    break;
                default:
            }
        // end switch
        }
    }
}
