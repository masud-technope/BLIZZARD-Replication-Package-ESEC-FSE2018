/**
 * Copyright (c) 2006 Parity Communications, Inc. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sergey Yakovlev - initial API and implementation
 */
package org.eclipse.ecf.internal.provider.rss.container;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.IConnectHandlerPolicy;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerGroupManager;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.core.sharedobject.ReplicaSharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.security.ISharedObjectPolicy;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.rss.RssDebugOptions;
import org.eclipse.ecf.internal.provider.rss.RssPlugin;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.ecf.provider.generic.ServerSOContainer;

/**
 * The RssServerSOContainer implements the basic RSS server functionality.
 * 
 */
public class RssServerSOContainer extends ServerSOContainer {

    public static final int DEFAULT_KEEPALIVE = 30000;

    public static final int DEFAULT_PORT = 80;

    // Keep alive value
    protected int keepAlive;

    protected RssServerSOContainerGroup group;

    private boolean isSingle = false;

    /**
	 * The constructors
	 * @param config 
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
    public  RssServerSOContainer(ISharedObjectContainerConfig config) throws URISyntaxException, IOException {
        this(config, null, DEFAULT_KEEPALIVE);
    }

    public  RssServerSOContainer(ISharedObjectContainerConfig config, int keepAlive) throws URISyntaxException, IOException {
        this(config, null, keepAlive);
    }

    public  RssServerSOContainer(ISharedObjectContainerConfig config, RssServerSOContainerGroup group, int keepAlive) throws URISyntaxException, IOException {
        super(config);
        this.keepAlive = keepAlive;
        final URI actualURI = new URI(getID().getName());
        final int urlPort = actualURI.getPort();
        if (group == null) {
            isSingle = true;
            this.group = new RssServerSOContainerGroup(urlPort);
            this.group.putOnTheAir();
        } else {
            this.group = group;
        }
        this.group.add(String.valueOf(urlPort), this);
    }

    protected void trace(String msg) {
        Trace.trace(RssPlugin.PLUGIN_ID, RssDebugOptions.DEBUG, msg);
    }

    protected void dumpStack(String msg, Throwable e) {
        Trace.catching(RssPlugin.PLUGIN_ID, RssDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "", e);
    }

    public void dispose() {
        URI aURI = null;
        try {
            aURI = new URI(getID().getName());
            group.remove(String.valueOf(aURI.getPort()));
        } catch (final URISyntaxException e) {
        }
        if (isSingle) {
            group.takeOffTheAir();
        }
        group = null;
        super.dispose();
    }

    public static void main(String args[]) throws Exception {
        RssServerSOContainerGroup serverGroups[] = null;
        final List servers = new ArrayList();
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            if (args[0].equals("-p")) {
                port = Integer.parseInt(args[1]);
            }
        }
        // Get server identity
        final String serverName = "//" + InetAddress.getLocalHost().getHostName() + ":" + port;
        serverGroups = new RssServerSOContainerGroup[1];
        // Setup server group
        serverGroups[0] = new RssServerSOContainerGroup(port);
        // Create identity for server
        final ID id = IDFactory.getDefault().createStringID(serverName);
        // Create server config object with identity and default timeout
        final SOContainerConfig config = new SOContainerConfig(id);
        // Make server instance
        System.out.println("Creating ECF server container...");
        final RssServerSOContainer server = new RssServerSOContainer(config, serverGroups[0], DEFAULT_KEEPALIVE);
        // Setup join policy
        ((ISharedObjectContainerGroupManager) server).setConnectPolicy(new IConnectHandlerPolicy() {

            public PermissionCollection checkConnect(Object address, ID fromID, ID targetID, String targetGroup, Object connectData) throws Exception {
                System.out.println("JOIN Addr=" + address + ";From=" + fromID + ";Group=" + targetGroup + ";Data=" + connectData);
                return null;
            }

            public void refresh() {
                System.out.println("joinPolicy.refresh()");
            }
        });
        // Setup add shared object policy
        final ISharedObjectManager manager = server.getSharedObjectManager();
        manager.setRemoteAddPolicy(new ISharedObjectPolicy() {

            public PermissionCollection checkAddSharedObject(ID fromID, ID toID, ID localID, ReplicaSharedObjectDescription newObjectDescription) throws SecurityException {
                System.out.println("ADDSHAREDOBJECT From=" + fromID + ";To=" + toID + ";SharedObjectDesc=" + newObjectDescription);
                return null;
            }

            public void refresh() {
                System.out.println("sharedObjectPolicy.refresh()");
            }
        });
        serverGroups[0].putOnTheAir();
        servers.add(server);
        System.out.println("success!");
        System.out.println("Waiting for JOIN requests at '" + id.getName() + "'...");
        System.out.println("<Ctrl>+C to stop server");
    }
}
