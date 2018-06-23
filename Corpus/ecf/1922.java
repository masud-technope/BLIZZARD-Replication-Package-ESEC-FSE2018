/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.ecf.tests.remoteservice.rest.service;

import java.io.IOException;
import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.server.SimpleHttpServer;

public class RestServiceTest extends TestCase {

    private SimpleRestService service;

    protected void setUp() throws Exception {
        if (service == null) {
            service = new SimpleRestService();
        }
    }

    protected void tearDown() throws Exception {
        service.shutdown();
    }

    public void testServerCreation() {
        assertNotNull(service);
        SimpleHttpServer server = service.getServer();
        assertNotNull(server);
        assertTrue(server.isRunning());
    }

    public void testStart() {
        SimpleHttpServer server = service.getServer();
        assertNotNull(server);
        service.run();
        assertTrue(server.isRunning());
    }

    public void testStop() {
        SimpleHttpServer server = service.getServer();
        assertNotNull(server);
        service.shutdown();
        server = service.getServer();
        assertNull(server);
    }

    public void testServerUrl() {
        String url = service.getServerUrl();
        assertEquals("http://localhost:" + service.getServer().getLocalPort(), url);
    }

    public void testSimpleRequest() {
        String serverUrl = service.getServerUrl();
        HttpClient client = new HttpClient();
        HttpMethod method = new HttpMethodBase(serverUrl + "/getTest") {

            public String getName() {
                return "GET";
            }
        };
        int responseCode = 0;
        try {
            responseCode = client.executeMethod(method);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(HttpStatus.SC_OK, responseCode);
    }

    public void testXMLRequest() {
        String serverUrl = service.getServerUrl();
        HttpClient client = new HttpClient();
        HttpMethod method = new HttpMethodBase(serverUrl + "/test.xml") {

            public String getName() {
                return "GET";
            }
        };
        int responseCode = 0;
        try {
            responseCode = client.executeMethod(method);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(HttpStatus.SC_OK, responseCode);
        try {
            String body = method.getResponseBodyAsString();
            assertEquals(SimpleRestService.XML_RESPONSE, body);
        } catch (IOException e) {
            e.printStackTrace();
            fail("body was not set correctly");
        }
    }

    public void testJsonResponse() {
        String serverUrl = service.getServerUrl();
        HttpClient client = new HttpClient();
        HttpMethod method = new HttpMethodBase(serverUrl + "/test.json") {

            public String getName() {
                return "GET";
            }
        };
        int responseCode = 0;
        try {
            responseCode = client.executeMethod(method);
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(HttpStatus.SC_OK, responseCode);
        try {
            String body = method.getResponseBodyAsString();
            assertEquals(SimpleRestService.JSON_RESPONSE, body);
        } catch (IOException e) {
            e.printStackTrace();
            fail("body was not set correctly");
        }
    }

    public void testPost() {
        String serverUrl = service.getServerUrl();
        HttpClient client = new HttpClient();
        HttpMethod method = new HttpMethodBase(serverUrl + "/test.json") {

            public String getName() {
                return "POST";
            }
        };
        try {
            client.executeMethod(method);
        } catch (HttpException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            assertTrue(e instanceof NoHttpResponseException);
        }
    }
}
