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

import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;

/**
 * Listener for remote call events. The IRemoteService.callAsynch method
 * supports the specification of a listener to receive and handle the results of
 * a remote call asynchronously. When non-null instance of a class implementing
 * this interface is provided to the IRemoteService.callAsynch method, it will
 * subsequently have it's {@link #handleEvent(IRemoteCallEvent)} method called
 * with<br>
 * <ol>
 * <li>An event that implements IRemoteCallStartEvent</li>
 * <li>An event that implements IRemoteCallCompleteEvent</li>
 * </ol>
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
 *
 * @see IRemoteService
 */
public interface IRemoteCallListener {

    /**
	 * Handle remote call events. The two remote call events are
	 * IRemoteCallStartEvent, and IRemoteCallCompleteEvent
	 * 
	 * @param event
	 *            the event. Will not be <code>null</code>.
	 */
    public void handleEvent(IRemoteCallEvent event);
}
