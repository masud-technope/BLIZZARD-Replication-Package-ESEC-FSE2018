package org.eclipse.ecf.internal.filetransfer.ui.hyperlink;

import java.net.URI;
import org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.widgets.Shell;

public class SCPHyperlinkDetector extends AbstractURLHyperlinkDetector {

    //$NON-NLS-1$
    public static final String SCP_PROTOCOL = "scp";

    public  SCPHyperlinkDetector() {
        setProtocols(new String[] { SCP_PROTOCOL });
    }

    Shell shell;

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion, boolean)
	 */
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        IHyperlink[] links = super.detectHyperlinks(textViewer, region, canShowMultipleHyperlinks);
        if (links != null)
            shell = textViewer.getTextWidget().getShell();
        return links;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.ui.hyperlink.AbstractURLHyperlinkDetector#createHyperLinksForURI(org.eclipse.jface.text.IRegion, java.net.URI)
	 */
    protected IHyperlink[] createHyperLinksForURI(IRegion region, URI uri) {
        return new IHyperlink[] { new SCPHyperlink(shell, region, uri) };
    }
}
