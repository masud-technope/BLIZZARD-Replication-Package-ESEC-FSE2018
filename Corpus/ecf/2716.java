package org.eclipse.ecf.remoteservice.ui.dosgi.handlers;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    //$NON-NLS-1$
    private static final String BUNDLE_NAME = "org.eclipse.ecf.remoteservice.ui.dosgi.handlers.messages";

    public static String DOSGiReflectiveRemoteServiceHandler_FilterCreationFailed;

    public static String DOSGiReflectiveRemoteServiceHandler_HandlerInvocationFailed;

    public static String DOSGiReflectiveRemoteServiceHandler_NoServiceMatch;

    public static String DOSGiReflectiveRemoteServiceHandler_RemoteServiceUnresolveable;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private  Messages() {
    }
}
