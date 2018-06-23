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

/**
 * Listener for handling notifications of subscribe/unsubscribe requests.
 * 
 * <p>
 * </p>
 * Note these methods will be called asynchronously when notifications of remote
 * changes are received by the provider implementation code. The provider is
 * free to call the methods below with an arbitrary thread, so the
 * implementation of these methods must be appropriately prepared.
 * <p>
 * </p>
 * For example, if the code implementing any of these methods must interact with
 * user interface code, then it should use code such as the following to execute
 * on the SWT UI thread:
 * 
 * <pre>
 * 	Display.getDefault().asyncExec(new Runnable() {
 * 		public void run() {
 * 		... UI code here
 * 		}
 * 	});
 * </pre>
 * 
 * Further, the code in the implementations of these methods should <b>not block</b> via 
 * I/O operations or blocking UI calls.
 */
public interface IRosterSubscriptionListener {

    /**
	 * Receive subscribe request.
	 * 
	 * @param fromID
	 *            the sender of the subscribe request. Will not be
	 *            <code>null</code>.
	 */
    public void handleSubscribeRequest(ID fromID);

    /**
	 * Receive subscribed notification.
	 * 
	 * @param fromID
	 *            the sender of the subscribed notification. Will not be
	 *            <code>null</code>.
	 */
    public void handleSubscribed(ID fromID);

    /**
	 * Receive unsubscribed notification.
	 * 
	 * @param fromID
	 *            the sender of the unsubscribed notification. Will not be
	 *            <code>null</code>.
	 */
    public void handleUnsubscribed(ID fromID);
}
