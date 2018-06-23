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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.httpclient.server.HttpRequestHandler;
import org.apache.commons.httpclient.server.ResponseWriter;
import org.apache.commons.httpclient.server.SimpleHttpServer;
import org.apache.commons.httpclient.server.SimpleHttpServerConnection;
import org.apache.commons.httpclient.server.SimpleRequest;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.ecf.filetransfer.FileTransferJob;
import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.UserCancelledException;
import org.eclipse.ecf.filetransfer.events.IFileTransferConnectStartEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketConnectedEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEvent;
import org.eclipse.ecf.filetransfer.events.socket.ISocketEventSource;
import org.eclipse.ecf.filetransfer.events.socket.ISocketListener;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.internal.tests.filetransfer.httpserver.SimpleServer;
import org.eclipse.ecf.provider.filetransfer.retrieve.AbstractRetrieveFileTransfer;
import org.eclipse.ecf.tests.filetransfer.SocketEventTestUtil.SocketInReadWrapper;
import org.eclipse.ecf.tests.filetransfer.SocketEventTestUtil.TrackSocketEvents;

public class URLRetrieveTestCancelConnectJob extends AbstractRetrieveTestCase {

    File tmpFile = null;

    private TrackSocketEvents socketEvents;

    private SocketInReadWrapper socketInReadWrapper;

    private boolean CANCEL_SUPPORTED_ON_CONNECT = new Boolean(System.getProperty("org.eclipse.ecf.tests.filetransfer.cancelSupportedOnConnect", "true")).booleanValue();

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void createTempFile() throws IOException {
        tmpFile = File.createTempFile("ECFTest", "");
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
    protected void tearDown() throws Exception {
        super.tearDown();
        if (tmpFile != null)
            tmpFile.delete();
        tmpFile = null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.tests.filetransfer.AbstractRetrieveTestCase#
	 * handleStartConnectEvent
	 * (org.eclipse.ecf.filetransfer.events.IFileTransferConnectStartEvent)
	 */
    protected void handleStartConnectEvent(IFileTransferConnectStartEvent event) {
        super.handleStartConnectEvent(event);
        this.socketEvents = SocketEventTestUtil.observeSocketEvents(event);
        ISocketEventSource source = (ISocketEventSource) event.getAdapter(ISocketEventSource.class);
        source.addListener(new ISocketListener() {

            public void handleSocketEvent(ISocketEvent event) {
                if (event instanceof ISocketConnectedEvent) {
                    ISocketConnectedEvent connectedEvent = (ISocketConnectedEvent) event;
                    socketInReadWrapper = new SocketInReadWrapper(connectedEvent.getSocket(), startTime);
                    connectedEvent.setSocket(socketInReadWrapper);
                }
            }
        });
    }

    protected void handleDoneEvent(IIncomingFileTransferReceiveDoneEvent event) {
        super.handleDoneEvent(event);
        assertTrue(event.getSource().getException() instanceof UserCancelledException);
    }

    protected void testReceive(String url, IFileTransferListener listener) throws Exception {
        assertNotNull(retrieveAdapter);
        final IFileID fileID = createFileID(new URL(url));
        retrieveAdapter.sendRetrieveRequest(fileID, listener, null);
        waitForDone(10000);
    }

    public void testReceiveFile_cancelOnConnectEvent() throws Exception {
        if (!CANCEL_SUPPORTED_ON_CONNECT) {
            trace("WARNING:  Cancel not supported by this provider.  testReceiveFile_cancelOnConnectEvent cannot be used");
            return;
        }
        final IFileTransferListener listener = createFileTransferListener();
        final FileTransferListenerWrapper lw = new FileTransferListenerWrapper(listener) {

            protected void handleStartConnectEvent(IFileTransferConnectStartEvent event) {
                assertNotNull(event.getFileID());
                assertNotNull(event.getFileID().getFilename());
                assertNull(socketInReadWrapper);
                setDone(true);
                event.cancel();
            }
        };
        testReceive(URLRetrieveTest.HTTP_RETRIEVE, lw);
        assertHasEvent(startConnectEvents, IFileTransferConnectStartEvent.class);
        assertHasNoEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
        assertHasNoEvent(dataEvents, IIncomingFileTransferReceiveDataEvent.class);
        assertDoneCancelled();
        assertNull(tmpFile);
        socketEvents.validateNoSocketCreated();
    }

    // TODO: add test that cancel without connect job, when server does not
    // respond
    public void testReceiveFile_cancelConnectJob() throws Exception {
        if (!CANCEL_SUPPORTED_ON_CONNECT) {
            trace("WARNING:  Cancel not supported by this provider.  testReceiveFile_cancelConnectJob cannot be used");
            return;
        }
        final Object[] doCancel = new Object[1];
        final IFileTransferListener listener = createFileTransferListener();
        final FileTransferListenerWrapper lw = new FileTransferListenerWrapper(listener) {

            protected void handleStartConnectEvent(final IFileTransferConnectStartEvent event) {
                assertNotNull(event.getFileID());
                assertNotNull(event.getFileID().getFilename());
                FileTransferJob connectJob = event.prepareConnectJob(null);
                connectJob.addJobChangeListener(new JobChangeTraceListener(startTime) {

                    public void running(IJobChangeEvent jobEvent) {
                        super.running(jobEvent);
                        spawnCancelThread(doCancel, new ICancelable() {

                            public void cancel() {
                                assertNotNull(socketInReadWrapper);
                                assertTrue(socketInReadWrapper.inRead);
                                event.cancel();
                            }
                        });
                    }
                });
                event.connectUsingJob(connectJob);
            }
        };
        final SimpleServer server = new SimpleServer(getName());
        SimpleHttpServer simple = server.getSimpleHttpServer();
        simple.setRequestHandler(new HttpRequestHandler() {

            public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
                trace("Not responding to request " + request.getRequestLine());
                return stalledInRequestHandler(doCancel);
            }
        });
        try {
            // path does not matter as server does not respond.
            testReceive(server.getServerURL() + "/foo", lw);
            assertHasEvent(startConnectEvents, IFileTransferConnectStartEvent.class);
            assertHasNoEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
            assertHasNoEvent(dataEvents, IIncomingFileTransferReceiveDataEvent.class);
            IIncomingFileTransferReceiveDoneEvent doneEvent = getDoneEvent();
            assertTrue(doneEvent.getException().toString(), doneEvent.getException() instanceof UserCancelledException);
            assertTrue(doneEvent.getSource().isDone());
            assertSame(doneEvent.getException(), doneEvent.getSource().getException());
            assertNull(tmpFile);
            assertFalse(socketInReadWrapper.inRead);
            socketEvents.validateOneSocketCreatedAndClosed();
        } finally {
            server.shutdown();
        }
    }

    private static void writeLines(ResponseWriter w, String[] lines) throws IOException {
        for (int i = 0; i < lines.length; i++) {
            w.println(lines[i]);
        }
    }

    public void testReceiveFile_cancelTransferJob() throws Exception {
        if (!CANCEL_SUPPORTED_ON_CONNECT) {
            trace("WARNING:  Cancel not supported by this provider.  testReceiveFile_cancelTransferJob cannot be used");
            return;
        }
        final Object[] doCancel = new Object[1];
        final IFileTransferListener listener = createFileTransferListener();
        final FileTransferListenerWrapper lw = new FileTransferListenerWrapper(listener) {

            protected void handleStartConnectEvent(final IFileTransferConnectStartEvent event) {
                assertNotNull(event.getFileID());
                assertNotNull(event.getFileID().getFilename());
                FileTransferJob connectJob = event.prepareConnectJob(null);
                connectJob.addJobChangeListener(new JobChangeTraceListener(startTime));
                event.connectUsingJob(connectJob);
            }

            protected void handleStartEvent(final IIncomingFileTransferReceiveStartEvent event) {
                spawnCancelThread(doCancel, new ICancelable() {

                    public void cancel() {
                        waitForSocketInRead();
                        assertNotNull(socketInReadWrapper);
                        assertTrue(socketInReadWrapper.inRead);
                        event.cancel();
                    }
                });
                try {
                    createTempFile();
                    event.receive(tmpFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
            }
        };
        final SimpleServer server = new SimpleServer(getName());
        SimpleHttpServer simple = server.getSimpleHttpServer();
        simple.setRequestHandler(new HttpRequestHandler() {

            public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
                trace("Responding to request but never provide full body" + request.getRequestLine());
                ResponseWriter w = conn.getWriter();
                writeLines(w, new String[] { "HTTP/1.0 200 OK", "Content-Length: 9", "Content-Type: text/plain; charset=UTF-8", "" });
                w.flush();
                synchronized (doCancel) {
                    doCancel[0] = Boolean.TRUE;
                }
                conn.setKeepAlive(true);
                //
                return stalledInRequestHandler(doCancel);
            }
        });
        try {
            // path does not matter as server does not respond.
            testReceive(server.getServerURL() + "/foo", lw);
            assertHasEvent(startConnectEvents, IFileTransferConnectStartEvent.class);
            assertHasEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
            assertDoneCancelled();
            assertNotNull(tmpFile);
            assertTrue(tmpFile.exists());
            assertEquals(0, tmpFile.length());
            assertFalse(socketInReadWrapper.inRead);
            socketEvents.validateOneSocketCreatedAndClosed();
        } finally {
            server.shutdown();
        }
    }

    private void waitForSocketInRead() {
        assertNotNull(socketInReadWrapper);
        while (!socketInReadWrapper.inRead) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
            }
        }
        assertTrue(socketInReadWrapper.inRead);
    }

    public void testReceiveFile_cancelTransferJobAfterOneBlock() throws Exception {
        if (!CANCEL_SUPPORTED_ON_CONNECT) {
            trace("WARNING:  Cancel not supported by this provider.  testReceiveFile_cancelTransferJobAfterOneBlock cannot be used");
            return;
        }
        testReceiveFile_cancelTransferJobInMiddle(AbstractRetrieveFileTransfer.DEFAULT_BUF_LENGTH * 2, false);
    }

    public void testReceiveFile_cancelTransferJobInMiddle() throws Exception {
        if (!CANCEL_SUPPORTED_ON_CONNECT) {
            trace("WARNING:  Cancel not supported by this provider.  testReceiveFile_cancelTransferJobInMiddle cannot be used");
            return;
        }
        testReceiveFile_cancelTransferJobInMiddle(20000, true);
    }

    public void testReceiveFile_cancelTransferJobInMiddle(final long len, final boolean expectedSocketInRead) throws Exception {
        if (!CANCEL_SUPPORTED_ON_CONNECT) {
            trace("WARNING:  Cancel not supported by this provider.  testReceiveFile_cancelTransferJobInMiddle cannot be used");
            return;
        }
        final Object[] doCancel = new Object[1];
        final IFileTransferListener listener = createFileTransferListener();
        final FileTransferListenerWrapper lw = new FileTransferListenerWrapper(listener) {

            protected void handleStartConnectEvent(final IFileTransferConnectStartEvent event) {
                assertNotNull(event.getFileID());
                assertNotNull(event.getFileID().getFilename());
                FileTransferJob connectJob = event.prepareConnectJob(null);
                connectJob.addJobChangeListener(new JobChangeTraceListener(startTime));
                event.connectUsingJob(connectJob);
            }

            protected void handleStartEvent(final IIncomingFileTransferReceiveStartEvent event) {
                spawnCancelThread(doCancel, new ICancelable() {

                    public void cancel() {
                        if (expectedSocketInRead) {
                            waitForSocketInRead();
                        }
                        event.cancel();
                    }
                });
                try {
                    createTempFile();
                    event.receive(tmpFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    fail(e.toString());
                }
            }
        };
        final SimpleServer server = new SimpleServer(getName());
        SimpleHttpServer simple = server.getSimpleHttpServer();
        simple.setRequestHandler(new HttpRequestHandler() {

            public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
                trace("Responding to request but never provide only 50% of body" + request.getRequestLine());
                ResponseWriter w = conn.getWriter();
                writeLines(w, new String[] { "HTTP/1.0 200 OK", "Content-Length: " + len, "Content-Type: text/plain; charset=UTF-8", "" });
                w.flush();
                for (int i = 0; i < len / 2; i++) {
                    w.write("x");
                }
                w.flush();
                conn.setKeepAlive(true);
                try {
                    // give it a bit of time to receive the data
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
                return stalledInRequestHandler(doCancel);
            }
        });
        try {
            // path does not matter as server does not respond.
            testReceive(server.getServerURL() + "/foo", lw);
            assertHasEvent(startConnectEvents, IFileTransferConnectStartEvent.class);
            assertHasEvent(startEvents, IIncomingFileTransferReceiveStartEvent.class);
            assertHasMoreThanEventCount(dataEvents, IIncomingFileTransferReceiveDataEvent.class, 0);
            assertDoneCancelled();
            assertNotNull(tmpFile);
            assertTrue(tmpFile.exists());
            assertEquals(len / 2, tmpFile.length());
            assertFalse(socketInReadWrapper.inRead);
            socketEvents.validateOneSocketCreatedAndClosed();
        } finally {
            server.shutdown();
        }
    }

    private boolean stalledInRequestHandler(final Object[] doCancel) {
        Exception ex = null;
        try {
            synchronized (doCancel) {
                doCancel[0] = Boolean.TRUE;
                doCancel.wait();
            }
        } catch (InterruptedException e) {
            ex = e;
        }
        trace("Leaving request handler");
        assertTrue(ex instanceof InterruptedException);
        return false;
    }

    interface ICancelable {

        void cancel();
    }

    private void spawnCancelThread(final Object[] doCancel, final ICancelable cancelable) {
        Thread t = new Thread(new Runnable() {

            public void run() {
                trace("Cancel runnable started");
                while (true) {
                    boolean cancel = false;
                    synchronized (doCancel) {
                        cancel = doCancel[0] != null;
                    }
                    if (cancel) {
                        trace("Before calling cancel");
                        cancelable.cancel();
                        trace("After calling cancel");
                        break;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                trace("Cancel runnable ending");
            }
        });
        t.start();
    }
}
