/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rest.service;

import java.io.IOException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.server.HttpRequestHandler;
import org.apache.commons.httpclient.server.SimpleHttpServer;
import org.apache.commons.httpclient.server.SimpleHttpServerConnection;
import org.apache.commons.httpclient.server.SimpleRequest;
import org.apache.commons.httpclient.server.SimpleResponse;

public class SimpleRestService {

    public static final int PORT = 12550;

    private SimpleHttpServer server;

    private HttpRequestHandler reqHandler = new HttpRequestHandler() {

        public boolean processRequest(SimpleHttpServerConnection conn, SimpleRequest request) throws IOException {
            String uri = request.getRequestLine().getUri();
            request.getBodyBytes();
            boolean get = request.getRequestLine().getMethod().equals("GET");
            if (uri.equals("/getTest") && get) {
                // normal response
                hookResponse(conn, "success");
                return true;
            } else if (uri.equals("/test.xml") && get) {
                // XML Response
                hookResponse(conn, XML_RESPONSE);
                return true;
            } else if (uri.equals("/test.json") && get) {
                // JSON Response
                hookResponse(conn, JSON_RESPONSE);
                return true;
            }
            return false;
        }
    };

    public static final String XML_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<root><aNode param=\"ok\"/></root>";

    public static final String JSON_RESPONSE = "{\"aNode\":\"aValue\"}";

    public  SimpleRestService() {
        run();
    }

    protected void hookResponse(SimpleHttpServerConnection conn, String body) throws IOException {
        SimpleResponse res = new SimpleResponse();
        res.setStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK);
        res.setBodyString(body);
        conn.setKeepAlive(false);
        conn.writeResponse(res);
    }

    public void shutdown() {
        if (server != null) {
            server.destroy();
            server = null;
        }
    }

    public void run() {
        if (server == null) {
            createServer();
        } else {
            if (!server.isRunning()) {
                server.destroy();
                server = null;
                createServer();
            }
        }
    }

    private void createServer() {
        try {
            server = new SimpleHttpServer();
            server.setRequestHandler(reqHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SimpleHttpServer getServer() {
        return server;
    }

    public String getServerUrl() {
        return "http://localhost:" + server.getLocalPort();
    }
}
