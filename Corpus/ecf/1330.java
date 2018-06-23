/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.tests.remoteservice.rpc;

import javax.servlet.ServletConfig;
import org.apache.xmlrpc.XmlRpcConfig;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import javax.servlet.ServletInputStream;
import java.util.Enumeration;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import org.eclipse.equinox.http.jetty.JettyConfigurator;
import java.util.Hashtable;
import java.util.Dictionary;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.eclipse.ecf.tests.remoteservice.rpc.RpcConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    //$NON-NLS-1$
    private static final String HTTP_PORT_KEY = "http.port";

    //$NON-NLS-1$
    private static final String SERVER_NAME = "xmlrpcserver";

    private static BundleContext context;

    private HttpServiceConnector httpServiceConnector;

    static BundleContext getContext() {
        return context;
    }

    class XHttpServlet extends HttpServlet {

        private final XmlRpcServlet s = new XmlRpcServlet();

        @Override
        public void init(ServletConfig config) throws ServletException {
            // TODO Auto-generated method stub
            super.init(config);
            s.init(config);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            //System.out.println("doPost req="+readInputStreamAsString(req.getInputStream()));
            s.doPost(req, resp);
        }
    }

    public static String readInputStreamAsString(InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        httpServiceConnector = new HttpServiceConnector(context, RpcConstants.TEST_SERVLETS_PATH, new XHttpServlet());
        Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(HTTP_PORT_KEY, RpcConstants.HTTP_PORT);
        JettyConfigurator.startServer(SERVER_NAME, properties);
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
        JettyConfigurator.stopServer(SERVER_NAME);
        if (httpServiceConnector != null) {
            httpServiceConnector.close();
            httpServiceConnector = null;
        }
    }
}
