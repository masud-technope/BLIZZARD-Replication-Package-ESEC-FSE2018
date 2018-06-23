package org.eclipse.ecf.internal.provider.msn.ui;

import java.net.URI;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

public class MSNHyperlinkDetector extends AbstractURLHyperlinkDetector {

    //$NON-NLS-1$
    public static final String MSN_PROTOCOL = "msn";

    public  MSNHyperlinkDetector() {
        setProtocols(new String[] { MSN_PROTOCOL });
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#createHyperLinksForURI(org.eclipse.jface.text.IRegion, java.net.URI)
	 */
    protected IHyperlink[] createHyperLinksForURI(IRegion region, URI uri) {
        return new IHyperlink[] { new MSNHyperlink(region, uri) };
    }
}
