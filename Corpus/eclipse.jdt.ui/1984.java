package p;

import org.eclipse.osgi.util.NLS;

public class A extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "p.messages";

    public static String f;

    static {
        NLS.initializeMessages(BUNDLE_NAME, A.class);
    }

    private  A() {
    }
}
