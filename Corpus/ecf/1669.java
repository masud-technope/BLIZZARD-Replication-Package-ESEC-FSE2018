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
package org.eclipse.ecf.tests.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import junit.framework.Assert;
import org.eclipse.ecf.filetransfer.IFileTransfer;
import org.eclipse.ecf.filetransfer.IIncomingFileTransfer;
import org.eclipse.ecf.filetransfer.IRetrieveFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.ecf.filetransfer.events.IFileTransferConnectStartEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketClosedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketConnectedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketCreatedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEventSource;
import org.eclipse.ecf.filetransfer.events.socket.ISocketListener;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.ecf.provider.filetransfer.events.socket.AbstractSocketWrapper;

public class SocketEventTestUtil {

    public static class TrackSocketEvents {

        private ISocketEventSource eventSource;

        private ISocketListener listener;

        final ArrayList socketEvents = new ArrayList();

         TrackSocketEvents(ISocketEventSource eventSource) {
            if (eventSource != null) {
                this.eventSource = eventSource;
                listener = new ISocketListener() {

                    public void handleSocketEvent(ISocketEvent event) {
                        socketEvents.add(event);
                    }
                };
                eventSource.addListener(listener);
            }
        }

        public void validateNoSocketCreated() {
            Assert.assertEquals(0, socketEvents.size());
        }

        public void validateOneSocketCreatedAndClosed() {
            //			if (eventSource == null) {
            //				return;
            //			}
            eventSource.removeListener(listener);
            Assert.assertTrue(socketEvents.size() > 0);
            ISocketEvent socketEvent = (ISocketEvent) socketEvents.remove(0);
            Assert.assertTrue(socketEvent.toString(), socketEvent instanceof ISocketCreatedEvent);
            ISocketCreatedEvent createdEvent = (ISocketCreatedEvent) socketEvent;
            ISocketEventSource source = createdEvent.getSource();
            Assert.assertNotNull(source.toString(), source);
            Object primary = canAdaptTo(source);
            Socket createdSocket = createdEvent.getSocket();
            Assert.assertNotNull(primary.toString(), createdSocket);
            Assert.assertTrue(socketEvents.size() > 0);
            socketEvent = (ISocketEvent) socketEvents.remove(0);
            Assert.assertTrue(socketEvent.toString(), socketEvent instanceof ISocketConnectedEvent);
            ISocketConnectedEvent connectedEvent = (ISocketConnectedEvent) socketEvent;
            Assert.assertSame(source, connectedEvent.getSource());
            Assert.assertTrue(createdEvent.isSameFactorySocket(connectedEvent));
            Assert.assertTrue(socketEvents.size() > 0);
            socketEvent = (ISocketEvent) socketEvents.remove(0);
            Assert.assertTrue(socketEvent.toString(), socketEvent instanceof ISocketClosedEvent);
            ISocketClosedEvent closedEvent = (ISocketClosedEvent) socketEvent;
            Assert.assertSame(source, closedEvent.getSource());
            Assert.assertTrue(createdEvent.isSameFactorySocket(closedEvent));
            Assert.assertEquals(0, socketEvents.size());
        }

        private Object canAdaptTo(ISocketEventSource source) {
            IRetrieveFileTransferContainerAdapter receive = (IRetrieveFileTransferContainerAdapter) source.getAdapter(IRetrieveFileTransferContainerAdapter.class);
            if (receive != null) {
                canAdaptTo(source, receive, new Class[] { IRetrieveFileTransfer.class, IIncomingFileTransfer.class, IFileTransfer.class });
                return receive;
            }
            ISendFileTransferContainerAdapter send = (ISendFileTransferContainerAdapter) source.getAdapter(ISendFileTransferContainerAdapter.class);
            if (send != null) {
                canAdaptTo(source, send, new Class[] { IIncomingFileTransfer.class, IFileTransfer.class });
                return send;
            }
            Assert.fail("Should be adapable to IRetrieveFileTransferContainerAdapter or ISendFileTransferContainerAdapter");
            // TODO: for browse as well.
            return null;
        }

        private void canAdaptTo(ISocketEventSource source, Object primary, Class[] classes) {
            for (int i = 0; i < classes.length; i++) {
                Class class1 = classes[i];
                Assert.assertNotNull(source.toString() + ".getAdapter(" + class1.getName() + ")", source.getAdapter(class1));
            }
        }
    }

    public static TrackSocketEvents observeSocketEvents(IFileTransferConnectStartEvent event) {
        ISocketEventSource socketSource = (ISocketEventSource) event.getAdapter(ISocketEventSource.class);
        return new TrackSocketEvents(socketSource);
    }

    public static class SocketInReadWrapper extends AbstractSocketWrapper {

        public volatile boolean inRead;

        public volatile int readCount;

        private long startTime;

        public  SocketInReadWrapper(Socket socket, long startTime) {
            super(socket);
            this.startTime = startTime;
            this.readCount = 0;
        }

        public InputStream getInputStream() throws IOException {
            return new SocketInReadInputStream(super.getInputStream(), startTime);
        }

        class SocketInReadInputStream extends InputStream {

            private InputStream in;

            private long startTime;

            protected  SocketInReadInputStream(InputStream in, long startTime) {
                this.in = in;
            }

            public int available() throws IOException {
                return in.available();
            }

            public void close() throws IOException {
                in.close();
            }

            public void mark(int readlimit) {
                in.mark(readlimit);
            }

            public boolean markSupported() {
                return in.markSupported();
            }

            public void reset() throws IOException {
                in.reset();
            }

            public long skip(long n) throws IOException {
                return in.skip(n);
            }

            private void enter() {
                Trace.trace(System.currentTimeMillis() - startTime, "enter read");
                inRead = true;
                readCount++;
            }

            private void leave() {
                Trace.trace(System.currentTimeMillis() - startTime, "leaving read");
                inRead = false;
            }

            public int read() throws IOException {
                enter();
                try {
                    int rc = in.read();
                    return rc;
                } finally {
                    leave();
                }
            }

            public int read(byte[] b, int off, int len) throws IOException {
                enter();
                try {
                    int rc = in.read(b, off, len);
                    return rc;
                } finally {
                    leave();
                }
            }

            public int read(byte[] b) throws IOException {
                enter();
                try {
                    int rc = in.read(b);
                    return rc;
                } finally {
                    leave();
                }
            }
        }
    }
}
