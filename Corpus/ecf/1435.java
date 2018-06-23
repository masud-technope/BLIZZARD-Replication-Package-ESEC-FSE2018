/*******************************************************************************
 * Copyright (c) 2004, 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.ui;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.user.IUser;
import org.eclipse.ecf.core.user.User;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.example.collab.share.SharedObjectEventListener;
import org.eclipse.ecf.internal.example.collab.*;

public class SharedObjectContainerUI {

    //$NON-NLS-1$
    public static final String JOIN_TIME_FORMAT = "hh:mm:ss a z";

    //$NON-NLS-1$
    public static final String FILE_DIRECTORY = "received_files";

    //$NON-NLS-1$ //$NON-NLS-2$
    public static final String ECFDIRECTORY = "ECF_" + FILE_DIRECTORY + "/";

    //$NON-NLS-1$
    public static final String COLLAB_SHARED_OBJECT_ID = "chat";

    ISharedObjectContainer soc = null;

    CollabClient collabclient = null;

    public  SharedObjectContainerUI(CollabClient client, ISharedObjectContainer soc) {
        this.collabclient = client;
        this.soc = soc;
    }

    protected String getSharedFileDirectoryForProject(IResource proj) {
        final String eclipseDir = Platform.getLocation().lastSegment();
        if (proj == null)
            //$NON-NLS-1$
            return eclipseDir + "/" + ECFDIRECTORY;
        else
            return FILE_DIRECTORY;
    }

    protected IUser getUserData(String containerType, ID clientID, String usernick, IResource project) {
        final Map topElements = new HashMap();
        topElements.put(Messages.SharedObjectContainerUI_PROJECT_LABEL, CollabClient.getNameForResource(project));
        final SimpleDateFormat sdf = new SimpleDateFormat(JOIN_TIME_FORMAT);
        topElements.put(Messages.SharedObjectContainerUI_TIME_LABEL, sdf.format(new Date()));
        try {
            //$NON-NLS-1$
            topElements.put(Messages.SharedObjectContainerUI_LANGUAGE_LABEL, System.getProperty("user.language"));
        } catch (final Exception e) {
        }
        try {
            //$NON-NLS-1$
            topElements.put(Messages.SharedObjectContainerUI_TIME_ZONE_LABEL, System.getProperty("user.timezone"));
        } catch (final Exception e) {
        }
        try {
            topElements.put(Messages.SharedObjectContainerUI_OS_LABEL, Platform.getOS());
        } catch (final Exception e) {
        }
        try {
            //$NON-NLS-1$ //$NON-NLS-2$
            topElements.put("Username", System.getProperty("user.name"));
        } catch (final Exception e) {
        }
        try {
            //$NON-NLS-1$
            topElements.put("Hostname/IP", InetAddress.getLocalHost().toString());
        } catch (final Exception e) {
        }
        return new User(clientID, usernick, usernick, topElements);
    }

    void addObjectToClient(ISharedObjectContainer soContainer, ClientEntry client, String username, IResource proj) throws Exception {
        final IResource project = (proj == null) ? CollabClient.getWorkspace() : proj;
        final IUser user = getUserData(client.getClass().getName(), client.getContainer().getID(), username, proj);
        createAndAddSharedObject(soContainer, client, project, user, getSharedFileDirectoryForProject(project));
    }

    public void setup(final ISharedObjectContainer soContainer, final ClientEntry newClientEntry, final IResource resource, String username) throws Exception {
        addObjectToClient(soContainer, newClientEntry, username, resource);
        soc.addListener(new IContainerListener() {

            public void handleEvent(IContainerEvent evt) {
                if (evt instanceof IContainerDisconnectedEvent || evt instanceof IContainerEjectedEvent) {
                    final ID departedContainerID = ((evt instanceof IContainerDisconnectedEvent) ? ((IContainerDisconnectedEvent) evt).getTargetID() : ((IContainerEjectedEvent) evt).getTargetID());
                    final ID connectedID = newClientEntry.getContainer().getConnectedID();
                    if (connectedID == null || connectedID.equals(departedContainerID)) {
                        if (!newClientEntry.isDisposed()) {
                            collabclient.disposeClient(resource, newClientEntry);
                        }
                    }
                }
            }
        });
    }

    protected void createAndAddSharedObject(final ISharedObjectContainer soContainer, final ClientEntry client, final IResource proj, IUser user, String fileDir) throws Exception {
        final EclipseCollabSharedObject sharedObject = new EclipseCollabSharedObject(soContainer, proj, user, fileDir);
        sharedObject.setListener(new SharedObjectEventListener() {

            public void memberRemoved(ID member) {
                final ID groupID = client.getContainer().getConnectedID();
                if (member.equals(groupID)) {
                    if (!client.isDisposed()) {
                        collabclient.disposeClient(proj, client);
                    }
                }
            }

            public void memberAdded(ID member) {
            }

            public void otherActivated(ID other) {
            }

            public void otherDeactivated(ID other) {
            }

            public void windowClosing() {
                final ID groupID = client.getContainer().getConnectedID();
                CollabClient.removeClientForResource(proj, groupID);
            }
        });
        final ID newID = IDFactory.getDefault().createStringID(COLLAB_SHARED_OBJECT_ID);
        soContainer.getSharedObjectManager().addSharedObject(newID, sharedObject, new HashMap());
        client.setSharedObject(sharedObject);
    }
}
