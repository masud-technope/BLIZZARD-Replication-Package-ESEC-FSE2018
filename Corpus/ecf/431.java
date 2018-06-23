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
package org.eclipse.ecf.provider.generic;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.*;
import org.eclipse.ecf.internal.provider.ProviderPlugin;
import org.eclipse.ecf.provider.comm.IConnectRequestHandler;
import org.eclipse.ecf.provider.comm.tcp.*;

public class SOContainerGroup implements ISocketAcceptHandler {

    /**
	 * @since 4.7
	 */
    //$NON-NLS-1$
    public static final String INVALID_CONNECT = "Invalid connect request.";

    String name;

    protected Map map;

    public  SOContainerGroup(String name) {
        this.name = name;
        map = Collections.synchronizedMap(new TreeMap());
    }

    public String add(String key, SOContainer aSpace) {
        if (key == null || aSpace == null)
            return null;
        map.put(key, aSpace);
        return key;
    }

    public SOContainer get(String key) {
        if (key == null)
            return null;
        return (SOContainer) map.get(key);
    }

    public SOContainer remove(String key) {
        if (key == null)
            return null;
        return (SOContainer) map.remove(key);
    }

    public boolean contains(String key) {
        if (key == null)
            return false;
        return map.containsKey(key);
    }

    public String getName() {
        return name;
    }

    public Iterator elements() {
        return map.values().iterator();
    }

    /**
	 * @param aSocket socket
	 * @return ObjectOutputStream new object output stream for socket
	 * @throws IOException if object output stream cannot be created
	 * @since 4.7
	 */
    protected ObjectOutputStream createObjectOutputStream(Socket aSocket) throws IOException {
        return new ObjectOutputStream(aSocket.getOutputStream());
    }

    /**
	 * @param aSocket socket
	 * @return ObjectInputStream new object input stream for socket
	 * @throws IOException if object input stream cannot be created
	 * @since 4.7
	 */
    protected ObjectInputStream createObjectInputStream(Socket aSocket) throws IOException {
        return ProviderPlugin.getDefault().createObjectInputStream(aSocket.getInputStream());
    }

    /**
	 * @param ins object input stream.  Will not be <code>null</code>
	 * @return ConnectRequestMessage connect request message read from object input stream
	 * @throws IOException if object cannot be read from stream
	 * @throws ClassNotFoundException if object cannot be read from stream
	 * @since 4.7
	 */
    protected ConnectRequestMessage readConnectRequestMessage(ObjectInputStream ins) throws IOException, ClassNotFoundException {
        ConnectRequestMessage req = (ConnectRequestMessage) ins.readObject();
        if (req == null)
            //$NON-NLS-1$
            throw new InvalidObjectException(INVALID_CONNECT + " Connect request message cannot be null");
        return req;
    }

    /**
	 * @param aSocket socket
	 * @since 4.7
	 */
    public void handleAccept(Socket aSocket) throws Exception {
        // Set tcp no delay option
        aSocket.setTcpNoDelay(true);
        final ObjectOutputStream oStream = createObjectOutputStream(aSocket);
        oStream.flush();
        final ObjectInputStream iStream = createObjectInputStream(aSocket);
        final ConnectRequestMessage req = readConnectRequestMessage(iStream);
        final URI uri = req.getTarget();
        if (uri == null)
            //$NON-NLS-1$
            throw new InvalidObjectException(INVALID_CONNECT + " URI connect target cannot be null");
        final String path = uri.getPath();
        if (path == null)
            //$NON-NLS-1$
            throw new InvalidObjectException(INVALID_CONNECT + " Path cannot be null");
        // Given path, lookup associated container
        final SOContainer srs = get(path);
        if (srs == null)
            //$NON-NLS-1$
            throw new InvalidObjectException("Container not found for path=" + path);
        // Create our local messaging interface
        final Client newClient = new Client(aSocket, iStream, oStream, srs.getMessageReceiver());
        // Get output stream lock so nothing is sent until we've responded
        Object outputStreamLock = newClient.getOutputStreamLock();
        // accepted or rejected connect request
        synchronized (outputStreamLock) {
            // Call checkConnect
            final Serializable resp = ((IConnectRequestHandler) srs).handleConnectRequest(aSocket, path, req.getData(), newClient);
            // Create connect response wrapper and send it back
            oStream.writeObject(new ConnectResultMessage(resp));
            oStream.flush();
        }
    }
}
