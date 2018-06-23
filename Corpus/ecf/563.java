/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.filetransfer.events.socket;

import java.net.Socket;

/**
 * Event issued after a socket successfully connected.
 * <p>
 * Can be used to wrap a socket by calling {@link #setSocket(Socket)}. 
 * </p> 
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API without
 * consulting with the ECF team.
 * </p>
 * 
 * @since 3.0
 */
public interface ISocketConnectedEvent extends ISocketEvent {

    /**
	 * Sets a socket to be used by the app.
	 * <p>
	 * If this method is not called {@link #getFactorySocket()} and
	 * {@link #getSocket()} will be the same. 
	 * Otherwise {@link #getSocket()} will return the passed in socket.  
	 * </p>
	 * @param socket socket
	 */
    void setSocket(Socket socket);
}
