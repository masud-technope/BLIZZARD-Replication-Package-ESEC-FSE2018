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
package org.eclipse.ecf.internal.tests.filetransfer.httpserver;

import java.io.IOException;
import org.apache.commons.httpclient.server.SimpleHttpServer;

public class SimpleServer implements ITestServer {

    private SimpleHttpServer server = null;

    public  SimpleServer(String testName) throws IOException {
        this(testName, testName);
    }

    public  SimpleServer(String serverName, String testName) throws IOException {
        this.server = new SimpleHttpServer(serverName);
        this.server.setTestname(testName);
    }

    public void shutdown() {
        server.destroy();
        server = null;
    }

    public SimpleHttpServer getSimpleHttpServer() {
        return server;
    }

    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        if (adapter.isInstance(getSimpleHttpServer())) {
            return getSimpleHttpServer();
        }
        return null;
    }

    public String getServerHost() {
        return "localhost";
    }

    public int getServerPort() {
        return server.getLocalPort();
    }

    public String getServerURL() {
        String url = "http://" + getServerHost() + ":" + getServerPort();
        return url;
    }
}
