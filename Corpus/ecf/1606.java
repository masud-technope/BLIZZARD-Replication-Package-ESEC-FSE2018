package org.eclipse.ecf.internal.provider.xmpp.ui.hyperlink;

import java.net.URI;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class XMPPHyperlinkDetector extends AbstractURLHyperlinkDetector {

    public static final String XMPP_PROTOCOL = "xmpp";

    public static final String XMPPS_PROTOCOL = "xmpps";

    public  XMPPHyperlinkDetector() {
        setProtocols(new String[] { XMPP_PROTOCOL, XMPPS_PROTOCOL });
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#createHyperLinksForURI(org.eclipse.jface.text.IRegion, java.net.URI)
	 */
    protected IHyperlink[] createHyperLinksForURI(IRegion region, URI uri) {
        return new IHyperlink[] { new XMPPHyperlink(region, uri) };
    }
}
