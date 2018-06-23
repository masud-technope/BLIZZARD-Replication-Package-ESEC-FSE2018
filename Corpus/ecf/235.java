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
import org.eclipse.ecf.core.util.Event;

/**
 * Socket events are emitted by a {@link ISocketEventSource} and
 * are delivered to a {@link ISocketListener}.
 * <p>
 * Beware that the associated source may not be instance currently
 * using the socket. For example a socket may be put into a 
 * connection pool after it is used. It is then typically reused
 * by another source. 
 * When a socket is closed this is attributed to the source which
 * created it, not which currently or most recently used it.
 * </p>
 * <p> This limits the usefulness of these events to cases
 * where the caller can make broader assumptions for example
 * because it wants to close or monitor all sockets in the 
 * entire application. 
 * The events are also useful for implementing unit tests.
 * </p>
 * <p>
 * The {@link ISocketConnectedEvent} allows an 
 * {@link ISocketListener} to wrap the socket.
 * </p>
 *  
 * <p>
 * <strong>EXPERIMENTAL</strong>. This class or interface has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API without
 * consulting with the ECF team.
 * </p>
 *  
 * @since 3.0
 */
public abstract interface ISocketEvent extends Event {

    // IFileTransfer or IRemoteFileSystemRequest
    ISocketEventSource getSource();

    boolean isSameFactorySocket(ISocketEvent socketEvent);

    Socket getFactorySocket();

    Socket getSocket();
}
