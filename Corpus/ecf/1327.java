package org.eclipse.ecf.internal.examples.webinar.dnd;

import org.eclipse.ecf.presence.roster.IRosterEntry;
import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.ecf.presence.ui.dnd.IRosterViewerDropTarget;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.TransferData;

public class RosterEntryDropTarget implements IRosterViewerDropTarget {

    protected TransferData transferData = null;

    protected IRosterEntry rosterEntry = null;

    public boolean validateDrop(IRosterItem rosterItem, int operation, TransferData transferType) {
        if (rosterItem instanceof IRosterEntry) {
            transferData = transferType;
            rosterEntry = (IRosterEntry) rosterItem;
            return true;
        } else {
            transferData = null;
            rosterEntry = null;
        }
        return false;
    }

    public boolean performDrop(Object data) {
        if (data instanceof String && rosterEntry != null) {
            // Right here, send data to channel
            // sendString(rosterEntry.getUser().getID(),(String) data);
            MessageDialog.openInformation(null, "Drop on Entry", "The following text was dropped on " + rosterEntry.getUser().getName() + ":\n\n" + data);
            return true;
        }
        return false;
    }
}
