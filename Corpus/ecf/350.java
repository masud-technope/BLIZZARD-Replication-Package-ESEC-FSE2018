/*******************************************************************************
* Copyright (c) 2009 IBM, and others. 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   IBM Corporation - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.filetransfer.events.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import org.eclipse.core.runtime.Assert;

public abstract class AbstractSocketWrapper extends Socket {

    private Socket socket;

    /**
	 * @throws IOException if some problem
	 */
    protected void checkCancel() throws IOException {
    // default does not check for cancel
    }

    public Socket getSocket() {
        return socket;
    }

    /**
	 * @param socket for the wrapper
	 */
    public  AbstractSocketWrapper(Socket socket) {
        super();
        Assert.isNotNull(socket);
        this.socket = socket;
    }

    public void bind(SocketAddress bindpoint) throws IOException {
        checkCancel();
        socket.bind(bindpoint);
    }

    public void close() throws IOException {
        socket.close();
    }

    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        checkCancel();
        socket.connect(endpoint, timeout);
    }

    public void connect(SocketAddress endpoint) throws IOException {
        checkCancel();
        socket.connect(endpoint);
    }

    public boolean equals(Object obj) {
        return socket.equals(obj);
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public InputStream getInputStream() throws IOException {
        checkCancel();
        return socket.getInputStream();
    }

    public boolean getKeepAlive() throws SocketException {
        return socket.getKeepAlive();
    }

    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public SocketAddress getLocalSocketAddress() {
        return socket.getLocalSocketAddress();
    }

    public boolean getOOBInline() throws SocketException {
        return socket.getOOBInline();
    }

    public OutputStream getOutputStream() throws IOException {
        checkCancel();
        return socket.getOutputStream();
    }

    public int getPort() {
        return socket.getPort();
    }

    public int getReceiveBufferSize() throws SocketException {
        return socket.getReceiveBufferSize();
    }

    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    public boolean getReuseAddress() throws SocketException {
        return socket.getReuseAddress();
    }

    public int getSendBufferSize() throws SocketException {
        return socket.getSendBufferSize();
    }

    public int getSoLinger() throws SocketException {
        return socket.getSoLinger();
    }

    public int getSoTimeout() throws SocketException {
        return socket.getSoTimeout();
    }

    public boolean getTcpNoDelay() throws SocketException {
        return socket.getTcpNoDelay();
    }

    public int getTrafficClass() throws SocketException {
        return socket.getTrafficClass();
    }

    public int hashCode() {
        return socket.hashCode();
    }

    public boolean isBound() {
        return socket.isBound();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public boolean isInputShutdown() {
        return socket.isInputShutdown();
    }

    public boolean isOutputShutdown() {
        return socket.isOutputShutdown();
    }

    public void sendUrgentData(int data) throws IOException {
        checkCancel();
        socket.sendUrgentData(data);
    }

    public void setKeepAlive(boolean on) throws SocketException {
        socket.setKeepAlive(on);
    }

    public void setOOBInline(boolean on) throws SocketException {
        socket.setOOBInline(on);
    }

    public void setReceiveBufferSize(int size) throws SocketException {
        socket.setReceiveBufferSize(size);
    }

    public void setReuseAddress(boolean on) throws SocketException {
        socket.setReuseAddress(on);
    }

    public void setSendBufferSize(int size) throws SocketException {
        socket.setSendBufferSize(size);
    }

    public void setSoLinger(boolean on, int linger) throws SocketException {
        socket.setSoLinger(on, linger);
    }

    public void setSoTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }

    public void setTcpNoDelay(boolean on) throws SocketException {
        socket.setTcpNoDelay(on);
    }

    public void setTrafficClass(int tc) throws SocketException {
        socket.setTrafficClass(tc);
    }

    public void shutdownInput() throws IOException {
        socket.shutdownInput();
    }

    public void shutdownOutput() throws IOException {
        socket.shutdownOutput();
    }

    public String toString() {
        return socket.toString();
    }
}
