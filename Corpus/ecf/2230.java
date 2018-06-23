/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.generic;

import java.io.IOException;
import java.net.ServerSocket;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.eclipse.ecf.tests.remoteservice.IConcatService;
import org.eclipse.equinox.concurrent.future.IFuture;
import junit.framework.TestCase;

public class SSLSimpleTest extends TestCase {

    public static final String TEST_STRING_1 = "foo";

    public static final String TEST_STRING_2 = "bar";

    SSLSimpleConcatServer server;

    SSLSimpleConcatClient client;

    protected void setUp() throws Exception {
        super.setUp();
        int freePort = getFreePort();
        if (freePort == -1)
            throw new Exception("could not run test because could not find open port for server");
        server = new SSLSimpleConcatServer();
        server.start(freePort);
        client = new SSLSimpleConcatClient();
        client.start(freePort);
    }

    private int getFreePort() {
        int port = -1;
        try {
            ServerSocket ss = new ServerSocket(0);
            port = ss.getLocalPort();
            ss.close();
        } catch (IOException e) {
            return -1;
        }
        return port;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.stop();
        client = null;
        server.stop();
        server = null;
    }

    public void testSimpleClientAndServerWithProxy() throws Exception {
        IRemoteService remoteService = client.getRemoteService();
        assertNotNull(remoteService);
        // Use proxy
        String result = ((IConcatService) remoteService.getProxy()).concat(TEST_STRING_1, TEST_STRING_2);
        assertTrue(result != null && result.equals(TEST_STRING_1 + TEST_STRING_2));
    }

    private IRemoteCall getRemoteConcatCall(final String first, final String second) {
        return new IRemoteCall() {

            public String getMethod() {
                return "concat";
            }

            public Object[] getParameters() {
                return new String[] { first, second };
            }

            public long getTimeout() {
                return 3000;
            }
        };
    }

    public void testSimpleClientAndServerWithCallSync() throws Exception {
        IRemoteService remoteService = client.getRemoteService();
        assertNotNull(remoteService);
        // Use callSync
        String result = (String) remoteService.callSync(getRemoteConcatCall(TEST_STRING_2, TEST_STRING_1));
        assertTrue(result != null && result.equals(TEST_STRING_2 + TEST_STRING_1));
    }

    public void testSimpleClientAndServerWithFireAsync() throws Exception {
        IRemoteService remoteService = client.getRemoteService();
        assertNotNull(remoteService);
        // Use callSync
        remoteService.fireAsync(getRemoteConcatCall(TEST_STRING_2, TEST_STRING_1));
        Thread.sleep(1000);
    }

    public void testSimpleClientAndServerWithCallAsync() throws Exception {
        IRemoteService remoteService = client.getRemoteService();
        assertNotNull(remoteService);
        // Use callSync
        remoteService.callAsync(getRemoteConcatCall(TEST_STRING_2, TEST_STRING_1));
        Thread.sleep(1000);
    }

    String result = null;

    public void testSimpleClientAndServerWithCallAsyncListener() throws Exception {
        IRemoteService remoteService = client.getRemoteService();
        assertNotNull(remoteService);
        // Use callSync
        remoteService.callAsync(getRemoteConcatCall(TEST_STRING_2, TEST_STRING_1), new IRemoteCallListener() {

            public void handleEvent(IRemoteCallEvent event) {
                if (event instanceof IRemoteCallCompleteEvent) {
                    result = (String) ((IRemoteCallCompleteEvent) event).getResponse();
                }
            }
        });
        Thread.sleep(1000);
        assertNotNull(result);
        assertTrue(result.equals(TEST_STRING_2 + TEST_STRING_1));
    }

    public void testSimpleClientAndServerWithFuture() throws Exception {
        IRemoteService remoteService = client.getRemoteService();
        assertNotNull(remoteService);
        // Use callSync
        IFuture future = remoteService.callAsync(getRemoteConcatCall(TEST_STRING_2, TEST_STRING_1));
        assertNotNull(future);
        String result = (String) future.get();
        assertTrue(result.equals(TEST_STRING_2 + TEST_STRING_1));
    }
}
