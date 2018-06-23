package org.eclipse.ecf.presence.collab.ui.url;

import java.net.URL;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.ui.dnd.IRosterViewerDropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

public class URLRosterViewerDropTarget implements IRosterViewerDropTarget {

    IRosterEntry rosterEntry;

    public boolean performDrop(Object data) {
        if (rosterEntry == null)
            return false;
        final URLShare urlshare = getURLShare(rosterEntry);
        if (urlshare == null)
            return false;
        try {
            String s = (String) data;
            //$NON-NLS-1$
            s = s.substring(0, s.indexOf("\n"));
            final URL anURL = new URL(s);
            urlshare.sendURL(rosterEntry.getUser().getName(), rosterEntry.getUser().getID(), anURL.toExternalForm());
        } catch (final Exception e) {
            return false;
        }
        return true;
    }

    private URLShare getURLShare(IRosterEntry rosterEntry1) {
        final IPresenceContainerAdapter pca = rosterEntry1.getRoster().getPresenceContainerAdapter();
        if (pca == null)
            return null;
        final IContainer container = (IContainer) pca.getAdapter(IContainer.class);
        if (container == null)
            return null;
        return URLShare.getURLShare(container.getID());
    }

    public boolean validateDrop(IRosterItem rosterItem, int operation, TransferData transferType) {
        if (!TextTransfer.getInstance().isSupportedType(transferType))
            return false;
        if (rosterItem instanceof IRosterEntry) {
            final Object data = TextTransfer.getInstance().nativeToJava(transferType);
            // Check to see if URL...if not, return false;
            try {
                new URL((String) data);
            } catch (final Exception e) {
                rosterEntry = null;
                return false;
            }
            // Check to see that URLShare exists for roster entry
            if (getURLShare((IRosterEntry) rosterItem) == null)
                return false;
            rosterEntry = (IRosterEntry) rosterItem;
            return true;
        }
        return false;
    }
}
