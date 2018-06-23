package org.eclipse.ecf.internal.provider.xmpp;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.osgi.service.url.AbstractURLStreamHandlerService;

public class XMPPFiletURLStreamHandlerService extends AbstractURLStreamHandlerService {

    public  XMPPFiletURLStreamHandlerService() {
    }

    public URLConnection openConnection(URL u) throws IOException {
        //$NON-NLS-1$
        throw new IOException("Cannot open connection with xmppfile protocol");
    }
}
