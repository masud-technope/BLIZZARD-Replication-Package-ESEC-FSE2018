/*******************************************************************************
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.rpc.server;

import javax.servlet.ServletConfig;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import org.apache.xmlrpc.webserver.XmlRpcServletServer;

public class RemoteServiceXmlRpcServlet extends XmlRpcServlet {

    private static final long serialVersionUID = 654171179838565650L;

    class RemoteServiceHandlerMapping extends AbstractReflectiveHandlerMapping {
    }

    private RemoteServiceHandlerMapping mapping;

    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
        return (mapping == null) ? super.newXmlRpcHandlerMapping() : mapping;
    }

    class RemoteServiceXmlRpcServletServer extends XmlRpcServletServer {
    }

    private RemoteServiceXmlRpcServletServer server;

    protected XmlRpcServletServer newXmlRpcServer(ServletConfig pConfig) throws XmlRpcException {
        return (server == null) ? super.newXmlRpcServer(pConfig) : server;
    }
}
