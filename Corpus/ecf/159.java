/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.presence.ui.dnd;

import org.eclipse.ecf.presence.roster.IRosterItem;
import org.eclipse.swt.dnd.TransferData;

/**
 * Roster viewer drop target.  This interface defines the 
 * required methods for implementers of the rosterViewerDropTarget
 * extension point.
 */
public interface IRosterViewerDropTarget {

    /**
	 * Validates dropping on the given roster item. This method is called whenever some 
	 * aspect of the drop operation changes.
	 * 
	 * @param rosterItem the roster item that the mouse is currently hovering over, or
	 *   <code>null</code> if the mouse is hovering over empty space
	 * @param operation the current drag operation (copy, move, etc.)
	 * @param transferData the current transfer type
	 * @return <code>true</code> if the drop is valid, and <code>false</code>
	 *   otherwise
	 */
    public boolean validateDrop(IRosterItem rosterItem, int operation, TransferData transferData);

    /**
	 * Performs any work associated with the drop.
	 *
	 * @param data the drop data
	 * @return <code>true</code> if the drop was successful, and 
	 *   <code>false</code> otherwise
	 */
    public boolean performDrop(Object data);
}
