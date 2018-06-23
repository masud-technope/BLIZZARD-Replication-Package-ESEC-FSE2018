/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.TCPServerSOContainer;
import org.eclipse.ecf.provider.generic.TCPServerSOContainerGroup;
import org.eclipse.ecf.server.generic.app.Connector;
import org.eclipse.ecf.server.generic.app.NamedGroup;
import org.eclipse.ecf.server.generic.app.ServerConfigParser;

public class ServerStartup {

    static TCPServerSOContainerGroup serverGroups[] = null;

    //$NON-NLS-1$
    static final String SERVER_FILE_NAME = "ServerStartup.xml";

    static List servers = new ArrayList();

    public  ServerStartup() throws Exception {
        final InputStream ins = this.getClass().getResourceAsStream(SERVER_FILE_NAME);
        if (ins != null) {
            createServers(ins);
        }
    }

    protected boolean isActive() {
        return (servers.size() > 0);
    }

    public void dispose() {
        destroyServers();
    }

    protected synchronized void destroyServers() {
        for (final Iterator i = servers.iterator(); i.hasNext(); ) {
            final TCPServerSOContainer s = (TCPServerSOContainer) i.next();
            if (s != null) {
                try {
                    s.dispose();
                } catch (final Exception e) {
                    ClientPlugin.log("Exception destroying server " + s.getConfig().getID());
                }
            }
        }
        servers.clear();
        if (serverGroups != null) {
            for (int i = 0; i < serverGroups.length; i++) {
                serverGroups[i].takeOffTheAir();
            }
            serverGroups = null;
        }
    }

    protected synchronized void createServers(InputStream ins) throws Exception {
        final ServerConfigParser scp = new ServerConfigParser();
        final List connectors = scp.load(ins);
        if (connectors != null) {
            serverGroups = new TCPServerSOContainerGroup[connectors.size()];
            int j = 0;
            for (final Iterator i = connectors.iterator(); i.hasNext(); ) {
                final Connector connect = (Connector) i.next();
                serverGroups[j] = createServerGroup(connect.getHostname(), connect.getPort());
                final List groups = connect.getGroups();
                for (final Iterator g = groups.iterator(); g.hasNext(); ) {
                    final NamedGroup group = (NamedGroup) g.next();
                    final TCPServerSOContainer cont = createServerContainer(group.getIDForGroup(), serverGroups[j], group.getName(), connect.getTimeout());
                    servers.add(cont);
                    ClientPlugin.log("ECF group server created: " + //$NON-NLS-1$
                    cont.getConfig().getID().getName());
                }
                serverGroups[j].putOnTheAir();
                j++;
            }
        }
    }

    protected TCPServerSOContainerGroup createServerGroup(String name, int port) {
        final TCPServerSOContainerGroup group = new TCPServerSOContainerGroup(name, port);
        return group;
    }

    protected TCPServerSOContainer createServerContainer(String id, TCPServerSOContainerGroup group, String path, int keepAlive) throws IDCreateException {
        final ID newServerID = IDFactory.getDefault().createStringID(id);
        final SOContainerConfig config = new SOContainerConfig(newServerID);
        return new TCPServerSOContainer(config, group, path, keepAlive);
    }
}
