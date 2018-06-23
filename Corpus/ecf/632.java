/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc.. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.telephony.call;

import org.eclipse.ecf.telephony.call.events.ICallSessionEvent;

/**
 * Listener for receiving call session events from remotes. 
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
 * Further, the code in the implementations of these methods should <b>not block</b>
 * via I/O operations or blocking UI calls.
 */
public interface ICallSessionListener {

    /**
	 * Handle call session event listener.  Instances implementing this interface are provided
	 * during calls to {@link ICallSessionContainerAdapter#sendCallRequest(org.eclipse.ecf.core.identity.ID, ICallSessionListener, java.util.Map)}
	 * and allow the caller to be asynchronously notified of subsequent {@link ICallSessionEvent}s.  
	 * <p></p>
	 * Note that this
	 * method may be called by an arbitrary thread (not necessarily the UI-thread), so implementers must be prepared for
	 * this.  Implementers of this method also should not block.
	 * 
	 * @param event
	 *            the event to handle.  Will not be <code>null</code>.
	 */
    public void handleCallSessionEvent(ICallSessionEvent event);
}
