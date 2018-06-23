/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare;

import org.eclipse.ecf.datashare.events.IChannelEvent;

/**
 * Listener for receiving messages sent to a given channel. The following types
 * of events may be received asynchronously via this listener:
 * <p>
 * IChannelMessageEvent - delivered when this channel receives a message.
 * <p>
 * IChannelConnectEvent - delivered when this container or other remote
 * containers connect.
 * <p>
 * IChannelDisconnectEvent - delivered when this channel or other remote
 * containers disconnect.
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
 * Further, the code in the implementations of these methods should <b>not block</b>
 * via I/O operations or blocking UI calls.
 */
public interface IChannelListener {

    /**
	 * Handle events sent to the channel.
	 * 
	 * @param event
	 *            the event received. Will not be <code>null</code>.
	 */
    public void handleChannelEvent(IChannelEvent event);
}
