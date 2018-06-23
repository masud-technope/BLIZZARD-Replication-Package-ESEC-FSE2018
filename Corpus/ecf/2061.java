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
package org.eclipse.ecf.provider.comm;

import java.util.Map;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;

/**
 * Connection interface to represent transport-level connections
 * 
 */
public interface IConnection extends IAdaptable {

    /**
	 * Connect to a remote process
	 * 
	 * @param targetID
	 *            the identity of the remote to connect to.  Must not be <code>null</code>.
	 * @param data
	 *            any data to send with the connection request (e.g. password or
	 *            other authentication data)
	 * @param timeout
	 *            the timeout (in ms) for the connection to occur
	 * @return a result object that is of type specific to provider
	 *         implementation
	 * @throws ECFException thrown if some problem with connect
	 */
    public Object connect(ID targetID, Object data, int timeout) throws ECFException;

    /**
	 * Disconnect
	 * 
	 */
    public void disconnect();

    /**
	 * @return true if the implementing class has been previously connected,
	 *         false if not connected
	 */
    public boolean isConnected();

    /**
	 * Get local ID for this connection
	 * 
	 * @return ID associated with local instance
	 */
    public ID getLocalID();

    /**
	 * Start connection
	 */
    public void start();

    /**
	 * Stop connection
	 */
    public void stop();

    /**
	 * 
	 * @return true if connection is started, false otherwise
	 */
    public boolean isStarted();

    /**
	 * Get properties for this connection
	 * 
	 * @return Map the properties associated with this connection. May be null.
	 */
    public Map getProperties();

    /**
	 * Add comm layer event listener
	 * 
	 * @param listener
	 *            the listener to add
	 */
    public void addListener(IConnectionListener listener);

    /**
	 * remove comm layer event listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
    public void removeListener(IConnectionListener listener);
}
