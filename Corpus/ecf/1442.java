/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

/**
 * Listener for receiving service events
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
 * Further, the code in the implementations of these methods should <b>not
 * block</b> via I/O operations or blocking UI calls.
 */
public interface IServiceListener {

    /**
	 * @since 5.0
	 * 
	 * @return true iff this {@link IServiceListener} request re-discovery by
	 *         the {@link IDiscoveryLocator}. The discovery
	 *         {@link IServiceEvent} will be fired asynchronously.
	 */
    public boolean triggerDiscovery();

    /**
	 * Notification that a service has been discovered (the service is fully
	 * resolved).
	 * 
	 * @param anEvent
	 *            Will not be <code>null</code>
	 */
    public void serviceDiscovered(IServiceEvent anEvent);

    /**
	 * Notification that a previously discovered service has been undiscovered.
	 * 
	 * @param anEvent
	 *            Will not be <code>null</code>
	 */
    public void serviceUndiscovered(IServiceEvent anEvent);
}
