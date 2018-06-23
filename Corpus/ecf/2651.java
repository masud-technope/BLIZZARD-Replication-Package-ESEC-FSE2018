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
package org.eclipse.ecf.presence.roster;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;

public interface IRosterSubscriptionSender {

    /**
	 * Send a request to add the userAccount (subscribe) to our roster. This
	 * initiates an asynchronous request to add the given userAccount to the
	 * local roster with the given nickname in the given groups. If successful,
	 * the roster will be asynchronously updated via calls to
	 * {@link IRosterListener#handleRosterEntryAdd(IRosterEntry)} and
	 * {@link IRosterListener#handleRosterUpdate(IRoster, IRosterItem)}
	 * 
	 * @param userAccount
	 *            the account of the roster add request. Must not be
	 *            <code>null</code>. e.g. "fliwatuet@foo.bar".
	 * @param nickname
	 *            or local alias for userAccount. May be <code>null</code>.
	 *            e.g. "Steve"
	 * @param groups
	 *            an array of group names that this use will belong to. May be
	 *            <code>null</code> if userAccount not intended to be in any
	 *            groups. e.g. [ "ECF", "Eclipse" ].
	 * 
	 * @exception ECFException
	 *                thrown if request cannot be sent (e.g. because of previous
	 *                disconnect) or server refuses to change as desired (e.g.
	 *                insufficient permissions).
	 */
    public void sendRosterAdd(String userAccount, String nickname, String[] groups) throws ECFException;

    /**
	 * Send a request to remove a given userID (unsubscribe) from our roster.
	 * This initiates an asynchronous request to remove the given userID from
	 * our local roster. If successfully removed, the roster will be
	 * asynchronously updated via calls to
	 * {@link IRosterListener#handleRosterEntryRemove(IRosterEntry)} and
	 * {@link IRosterListener#handleRosterUpdate(IRoster, IRosterItem)}.
	 * 
	 * @param userID
	 *            the user ID to remove. Must not be <code>null</code>.
	 * 
	 * @exception ECFException
	 *                thrown if request cannot be sent (e.g. because of previous
	 *                disconnect) or server refuses change as desired (e.g.
	 *                insufficient permissions).
	 */
    public void sendRosterRemove(ID userID) throws ECFException;
}
