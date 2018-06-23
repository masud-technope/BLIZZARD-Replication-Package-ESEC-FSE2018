/****************************************************************************
 * Copyright (c) 2004, 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.example.collab;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.ConnectContextFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.internal.example.collab.ui.SharedObjectContainerUI;
import org.eclipse.ecf.provider.generic.GenericContainerInstantiator;

public class CollabClient {

    public static final String WORKSPACE_NAME = Messages.CollabClient_WORKSPACE_NAME;

    public static final String GENERIC_CONTAINER_CLIENT_NAME = GenericContainerInstantiator.TCPCLIENT_NAME;

    static Hashtable clients = new Hashtable();

    static CollabClient collabClient = new CollabClient();

    /**
	 * Create a new container instance, and connect to a remote server or group.
	 * 
	 * @param containerType
	 *            the container type used to create the new container instance.
	 *            Must not be null.
	 * @param uri
	 *            the uri that is used to create a targetID for connection. Must
	 *            not be null.
	 * @param nickname
	 *            an optional String nickname. May be null.
	 * @param connectData
	 *            optional connection data. May be null.
	 * @param resource
	 *            the resource that this container instance is associated with.
	 *            Must not be null.
	 * @throws Exception
	 */
    public void createAndConnectClient(final String containerType, String uri, String nickname, final Object connectData, final IResource resource) throws Exception {
        // First create the new container instance
        final IContainer newClient = ContainerFactory.getDefault().createContainer(containerType);
        // Create the targetID instance
        ID targetID = IDFactory.getDefault().createID(newClient.getConnectNamespace(), uri);
        // Setup username
        String username = setupUsername(targetID, nickname);
        // Create a new container entry to hold onto container once created
        final ClientEntry newClientEntry = new ClientEntry(containerType, newClient);
        // Setup sharedobject container if the new instance supports
        // this
        ISharedObjectContainer sharedObjectContainer = (ISharedObjectContainer) newClient.getAdapter(ISharedObjectContainer.class);
        SharedObjectContainerUI socui = new SharedObjectContainerUI(this, sharedObjectContainer);
        socui.setup(sharedObjectContainer, newClientEntry, resource, username);
        // Now connect
        try {
            newClient.connect(targetID, ConnectContextFactory.createUsernamePasswordConnectContext(username, connectData));
        } catch (ContainerConnectException e) {
            EclipseCollabSharedObject so = newClientEntry.getSharedObject();
            if (so != null)
                so.destroySelf();
            throw e;
        }
        // only add container if the connect was successful
        addClientForResource(newClientEntry, resource);
    }

    public ClientEntry isConnected(IResource project, String type) {
        ClientEntry entry = getClientEntry(project, type);
        return entry;
    }

    protected static void addClientForResource(ClientEntry entry, IResource proj) {
        synchronized (clients) {
            String name = getNameForResource(proj);
            Vector v = (Vector) clients.get(name);
            if (v == null) {
                v = new Vector();
            }
            v.add(entry);
            clients.put(name, v);
        }
    }

    public static void removeClientForResource(IResource proj, ID targetID) {
        synchronized (clients) {
            String resourceName = getNameForResource(proj);
            Vector v = (Vector) clients.get(resourceName);
            if (v == null)
                return;
            ClientEntry remove = null;
            for (Iterator i = v.iterator(); i.hasNext(); ) {
                ClientEntry e = (ClientEntry) i.next();
                ID connectedID = e.getContainer().getConnectedID();
                if (connectedID == null || connectedID.equals(targetID)) {
                    remove = e;
                }
            }
            if (remove != null)
                v.remove(remove);
            if (v.size() == 0) {
                clients.remove(resourceName);
            }
        }
    }

    public static String getNameForResource(IResource res) {
        String preName = res.getName().trim();
        if (//$NON-NLS-1$
        preName == null || preName.equals("")) {
            preName = WORKSPACE_NAME;
        }
        return preName;
    }

    public static IResource getWorkspace() throws Exception {
        IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
        return ws;
    }

    protected static Vector getClientEntries(IResource proj) {
        synchronized (clients) {
            return (Vector) clients.get(getNameForResource(proj));
        }
    }

    protected static ClientEntry getClientEntry(IResource proj, ID targetID) {
        synchronized (clients) {
            Vector v = getClientEntries(proj);
            if (v == null)
                return null;
            for (Iterator i = v.iterator(); i.hasNext(); ) {
                ClientEntry e = (ClientEntry) i.next();
                ID connectedID = e.getContainer().getConnectedID();
                if (connectedID == null)
                    continue;
                else if (connectedID.equals(targetID)) {
                    return e;
                }
            }
        }
        return null;
    }

    protected static ClientEntry getClientEntry(IResource proj, String containerType) {
        synchronized (clients) {
            Vector v = getClientEntries(proj);
            if (v == null)
                return null;
            for (Iterator i = v.iterator(); i.hasNext(); ) {
                ClientEntry e = (ClientEntry) i.next();
                ID connectedID = e.getContainer().getConnectedID();
                if (connectedID == null)
                    continue;
                else {
                    String contType = e.getContainerType();
                    if (contType.equals(containerType)) {
                        return e;
                    }
                }
            }
        }
        return null;
    }

    protected static boolean containsEntry(IResource proj, ID targetID) {
        synchronized (clients) {
            Vector v = (Vector) clients.get(getNameForResource(proj));
            if (v == null)
                return false;
            for (Iterator i = v.iterator(); i.hasNext(); ) {
                ClientEntry e = (ClientEntry) i.next();
                ID connectedID = e.getContainer().getConnectedID();
                if (connectedID == null)
                    continue;
                else if (connectedID.equals(targetID)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static synchronized ISharedObjectContainer getContainer(IResource proj) {
        ClientEntry entry = getClientEntry(proj, GENERIC_CONTAINER_CLIENT_NAME);
        if (entry == null) {
            entry = getClientEntry(ResourcesPlugin.getWorkspace().getRoot(), GENERIC_CONTAINER_CLIENT_NAME);
        }
        if (entry != null) {
            IContainer cont = entry.getContainer();
            if (cont != null)
                return (ISharedObjectContainer) cont.getAdapter(ISharedObjectContainer.class);
            else
                return null;
        } else
            return null;
    }

    public static CollabClient getDefault() {
        return collabClient;
    }

    public synchronized void disposeClient(IResource proj, ClientEntry entry) {
        entry.dispose();
        removeClientForResource(proj, entry.getContainer().getConnectedID());
    }

    protected String setupUsername(ID targetID, String nickname) throws URISyntaxException {
        String username = null;
        if (nickname != null) {
            username = nickname;
        } else {
            username = new URI(targetID.getName()).getUserInfo();
            if (//$NON-NLS-1$
            username == null || username.equals(""))
                username = //$NON-NLS-1$
                System.getProperty(//$NON-NLS-1$
                "user.name");
        }
        return username;
    }
}
