/****************************************************************************
 * Copyright (c) 2004, 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.eclipse.ecf.remoteservice.events.*;

/**
 * Listener for remote service changes (register and unregister).
 * 
 * <p>
 * Note these methods will be called asynchronously when notifications of remote
 * changes are received by the provider implementation code. The provider is
 * free to call the methods below with an arbitrary thread, so the
 * implementation of these methods must be appropriately prepared.
 * <p>
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
public interface IRemoteServiceListener {

    /**
	 * Handle remote service events. The remote service events are
	 * {@link IRemoteServiceRegisteredEvent} and {@link IRemoteServiceUnregisteredEvent}.
	 * 
	 * @param event
	 *            the event. Will not be <code>null</code> .
	 */
    public void handleServiceEvent(IRemoteServiceEvent event);
}
