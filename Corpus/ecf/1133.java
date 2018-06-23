/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.server.generic.app;

import java.net.InetAddress;
import java.util.*;
import org.eclipse.ecf.provider.generic.TCPServerSOContainer;

public class Connector {

    public static final int DEFAULT_PORT = TCPServerSOContainer.DEFAULT_PORT;

    public static final int DEFAULT_TIMEOUT = TCPServerSOContainer.DEFAULT_KEEPALIVE;

    public static final String DEFAULT_HOSTNAME = TCPServerSOContainer.DEFAULT_HOST;

    public static final String DEFAULT_SERVERNAME = TCPServerSOContainer.DEFAULT_NAME;

    public static final String DEFAULT_PROTOCOL = TCPServerSOContainer.DEFAULT_PROTOCOL;

    int port = DEFAULT_PORT;

    int timeout = DEFAULT_TIMEOUT;

    String protocol = DEFAULT_PROTOCOL;

    String hostname = DEFAULT_HOSTNAME;

    boolean discovery = false;

    List groups = new ArrayList();

    public  Connector(String protocol, String host, int port, int timeout, boolean discovery) {
        //$NON-NLS-1$
        if (protocol != null && !protocol.equals(""))
            this.protocol = protocol;
        if (//$NON-NLS-1$
        host != null && !host.equals(""))
            //$NON-NLS-1$
            this.hostname = host;
        else {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                this.hostname = getHostNameForAddressWithoutLookup(addr);
            } catch (Exception e) {
                this.hostname = "localhost";
            }
        }
        this.port = port;
        this.timeout = timeout;
        this.discovery = discovery;
    }

    private String getHostNameForAddressWithoutLookup(InetAddress inetAddress) {
        // First get InetAddress.toString(), which returns
        // the inet address in this form:  "hostName/address".
        // If hostname is not resolved the result is: "/address"
        // So first we detect the location of the "/" to determine
        // whether the host name is there or not
        String inetAddressStr = inetAddress.toString();
        int slashPos = inetAddressStr.indexOf('/');
        if (slashPos == 0)
            // off '/' and return address as string
            return inetAddressStr.substring(1);
        // hostname is there/non-null, so we use it
        return inetAddressStr.substring(0, slashPos);
    }

    public  Connector(String protocol, String host, int port, int timeout) {
        this(protocol, host, port, timeout, false);
    }

    public boolean shouldRegisterForDiscovery() {
        return discovery;
    }

    public boolean addGroup(NamedGroup grp) {
        if (grp == null)
            return false;
        for (Iterator i = groups.iterator(); i.hasNext(); ) {
            NamedGroup namedGroup = (NamedGroup) i.next();
            if (namedGroup.getName().equals(grp.getName()))
                return false;
        }
        groups.add(grp);
        grp.setParent(this);
        return true;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public int getTimeout() {
        return timeout;
    }

    public List getGroups() {
        return groups;
    }

    public String getID() {
        //$NON-NLS-1$ //$NON-NLS-2$
        return getProtocol() + "://" + getHostname() + ":" + getPort();
    }
}
