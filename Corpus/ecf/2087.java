/****************************************************************************
 * Copyright (c) 2013 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.server.generic.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.PermissionCollection;
import java.util.*;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.security.IConnectHandlerPolicy;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerGroupManager;
import org.eclipse.ecf.provider.generic.*;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 * @since 6.0
 */
public class SSLGenericServer implements IApplication {

    private static Map serverGroups = new HashMap();

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
    public Object start(IApplicationContext context) throws Exception {
        try {
            //$NON-NLS-1$
            final String[] args = mungeArguments((String[]) context.getArguments().get("application.args"));
            if (//$NON-NLS-1$ //$NON-NLS-2$
            args.length == 1 && (args[0].equals("-help") || args[0].equals("-h"))) {
                usage();
                return IApplication.EXIT_OK;
            } else if (//$NON-NLS-1$ //$NON-NLS-2$
            args.length == 2 && (args[0].equals("-config") || args[0].equals("-c"))) {
                // Setup from configuration file (expected after -c <file>
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(args[1]);
                    setupServerFromConfig(new ServerConfigParser().load(fis));
                } finally {
                    if (fis != null)
                        fis.close();
                }
            } else {
                String hostname = SSLServerSOContainer.DEFAULT_HOST;
                int port = SSLServerSOContainer.DEFAULT_PORT;
                String name = SSLServerSOContainer.DEFAULT_NAME;
                int keepAlive = SSLServerSOContainer.DEFAULT_KEEPALIVE;
                switch(args.length) {
                    case 4:
                        keepAlive = Integer.parseInt(args[3]);
                    case 3:
                        hostname = args[2];
                    case 2:
                        name = args[1];
                        if (//$NON-NLS-1$
                        !name.startsWith("/"))
                            //$NON-NLS-1$
                            name = "/" + name;
                    case 1:
                        port = Integer.parseInt(args[0]);
                }
                setupServerFromParameters(hostname, port, name, keepAlive);
            }
            synchronized (this) {
                this.wait();
            }
            return IApplication.EXIT_OK;
        } catch (final Exception e) {
            stop();
            throw e;
        }
    }

    private void usage() {
        System.out.println(//$NON-NLS-1$
        "Usage: eclipse.exe -application " + this.getClass().getName() + //$NON-NLS-1$
        "[port [groupname [hostname [keepAlive]]]] | [-config|-c <configfile.xml>]");
        //$NON-NLS-1$
        System.out.println("   Examples: eclipse -application org.eclipse.ecf.provider.SSLGenericServer");
        //$NON-NLS-1$
        System.out.println("             eclipse -application org.eclipse.ecf.provider.SSLGenericServer " + 7777);
        System.out.println(//$NON-NLS-1$
        "             eclipse -application org.eclipse.ecf.provider.SSLGenericServer " + 7777 + //$NON-NLS-1$
        " mygroup foobarhost.wherever.com 35000");
        //$NON-NLS-1$
        System.out.println("             eclipse -application org.eclipse.ecf.provider.SSLGenericServer -c myconfig.xml");
    }

    /**
	 * @param hostname
	 * @param port
	 * @param name
	 * @param keepAlive
	 */
    protected void setupServerFromParameters(String hostname, int port, String name, int keepAlive) throws IOException, IDCreateException {
        //$NON-NLS-1$
        final String hostnamePort = hostname + ":" + port;
        synchronized (serverGroups) {
            SSLServerSOContainerGroup serverGroup = (SSLServerSOContainerGroup) serverGroups.get(hostnamePort);
            if (serverGroup == null) {
                //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("Putting server " + hostnamePort + " on the air...");
                try {
                    serverGroup = new SSLServerSOContainerGroup(hostname, port);
                    final String url = //$NON-NLS-1$
                    SSLServerSOContainer.DEFAULT_PROTOCOL + //$NON-NLS-1$
                    "://" + hostnamePort + name;
                    // Create
                    final SSLServerSOContainer container = createServerContainer(url, serverGroup, name, keepAlive);
                    // Configure
                    configureServerContainer(container);
                    // Put on the air
                    serverGroup.putOnTheAir();
                } catch (final IOException e) {
                    e.printStackTrace(System.err);
                    throw e;
                } catch (IDCreateException e) {
                    e.printStackTrace(System.err);
                    throw e;
                }
                serverGroups.put(hostnamePort, serverGroup);
                //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println("SSLGenericServerContainer " + hostnamePort + " on the air.");
            } else {
                System.out.println(//$NON-NLS-1$
                "SSLGenericServerContainer " + //$NON-NLS-1$
                hostnamePort + " already on the air.  No changes made.");
            }
        }
    }

    /**
	 * This method may be overridden by subclasses in order to customize the configuration of the
	 * newly created server containers (before they are put on the air).  For example, to set the appropriate
	 * connect policy.
	 * 
	 * @param container the container to configure
	 */
    protected void configureServerContainer(SSLServerSOContainer container) {
        // Setup join policy
        ((ISharedObjectContainerGroupManager) container).setConnectPolicy(new JoinListener());
    }

    protected void setupServerFromConfig(List connectors) throws IOException, IDCreateException {
        for (final Iterator i = connectors.iterator(); i.hasNext(); ) {
            final Connector connector = (Connector) i.next();
            final String hostname = connector.getHostname();
            final int port = connector.getPort();
            //$NON-NLS-1$
            final String hostnamePort = hostname + ":" + port;
            SSLServerSOContainerGroup serverGroup = null;
            synchronized (serverGroups) {
                serverGroup = (SSLServerSOContainerGroup) serverGroups.get(hostnamePort);
                if (serverGroup == null) {
                    //$NON-NLS-1$ //$NON-NLS-2$
                    System.out.println("Putting server " + hostnamePort + " on the air...");
                    serverGroup = new SSLServerSOContainerGroup(hostname, port);
                    final List groups = connector.getGroups();
                    for (final Iterator g = groups.iterator(); g.hasNext(); ) {
                        final NamedGroup group = (NamedGroup) g.next();
                        // Create
                        final SSLServerSOContainer container = createServerContainer(group.getIDForGroup(), serverGroup, group.getName(), connector.getTimeout());
                        // Configure
                        configureServerContainer(container);
                    }
                    serverGroup.putOnTheAir();
                    serverGroups.put(hostnamePort, serverGroup);
                    System.out.println(//$NON-NLS-1$
                    "GenericServerContainer " + //$NON-NLS-1$
                    hostnamePort + //$NON-NLS-1$
                    " on the air.");
                } else {
                    System.out.println(//$NON-NLS-1$
                    "GenericServerContainer " + //$NON-NLS-1$
                    hostnamePort + " already on the air.  No changes made.");
                }
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
    public void stop() {
        synchronized (serverGroups) {
            for (final Iterator i = serverGroups.keySet().iterator(); i.hasNext(); ) {
                final SSLServerSOContainerGroup serverGroup = (SSLServerSOContainerGroup) serverGroups.get(i.next());
                serverGroup.takeOffTheAir();
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                System.out.println("Taking " + serverGroup.getName() + ":" + serverGroup.getPort() + " off the air");
                final Iterator iter = serverGroup.elements();
                for (; iter.hasNext(); ) {
                    final SSLServerSOContainer container = (SSLServerSOContainer) iter.next();
                    container.dispose();
                }
            }
        }
        serverGroups.clear();
        synchronized (this) {
            this.notify();
        }
    }

    private String[] mungeArguments(String originalArgs[]) {
        if (originalArgs == null)
            return new String[0];
        final List l = new ArrayList();
        for (int i = 0; i < originalArgs.length; i++) if (//$NON-NLS-1$
        !originalArgs[i].equals("-pdelaunch"))
            l.add(originalArgs[i]);
        return (String[]) l.toArray(new String[] {});
    }

    private static SSLServerSOContainer createServerContainer(String id, SSLServerSOContainerGroup group, String path, int keepAlive) throws IDCreateException {
        //$NON-NLS-1$ //$NON-NLS-2$ 
        System.out.println("  Creating container with id=" + id + " keepAlive=" + keepAlive);
        final ID newServerID = IDFactory.getDefault().createStringID(id);
        final SOContainerConfig config = new SOContainerConfig(newServerID);
        return new SSLServerSOContainer(config, group, path, keepAlive);
    }

    static class JoinListener implements IConnectHandlerPolicy {

        public PermissionCollection checkConnect(Object addr, ID fromID, ID targetID, String targetGroup, Object joinData) throws Exception {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
            System.out.println("CLIENT CONNECT: fromAddress=" + addr + ";fromID=" + fromID + ";targetGroup=" + targetGroup);
            return null;
        }

        public void refresh() {
        // nothing to do
        }
    }
}
