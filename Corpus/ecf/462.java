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
package org.eclipse.ecf.example.collab.share.io;

public interface StreamSender {

    /**
	 * Send a message with data to remote replica(s). This method is called by
	 * the SharedObjectOutputStream when sends some data via the replicated
	 * object. The replicated object that implements this interface should send
	 * a message to one or all remote replicas that contains the information
	 * contained in this method signature. Remote replicas should then turn
	 * around and call the 'add' method on their SharedObjectInputStream to
	 * receive the data.
	 * 
	 * @param currentCount
	 *            the length of the data (assuming data are compressed)
	 * @param data
	 *            the actual data to send
	 */
    public void sendDataMsg(int currentCount, byte[] data);
}
