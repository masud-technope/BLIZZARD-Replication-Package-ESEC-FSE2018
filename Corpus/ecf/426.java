/**
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 */
package org.eclipse.ecf.pubsub.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContext;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.core.sharedobject.ReplicaSharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectDeactivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectMessageEvent;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.example.pubsub.SerializationUtil;
import org.eclipse.ecf.pubsub.IPublishedService;
import org.eclipse.ecf.pubsub.PublishedServiceDescriptor;

public class DiscoveryAgent extends PlatformObject implements ISharedObject {

    protected static final Object DIRECTORY_KEY = new Integer(0);

    protected ISharedObjectConfig config;

    protected PublishedServiceDirectory directory;

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        if (config.getContext().getLocalContainerID().equals(config.getHomeContainerID())) {
            directory = (PublishedServiceDirectory) config.getProperties().get(DIRECTORY_KEY);
            if (directory == null)
                throw new SharedObjectInitException("Directory is required.");
        }
        this.config = config;
    }

    public void handleEvent(Event event) {
        if (event instanceof ISharedObjectActivatedEvent) {
            ISharedObjectActivatedEvent e = (ISharedObjectActivatedEvent) event;
            if (e.getActivatedID().equals(config.getSharedObjectID()))
                activated();
            else
                activated(e.getActivatedID());
        } else if (event instanceof ISharedObjectDeactivatedEvent) {
            ISharedObjectDeactivatedEvent e = (ISharedObjectDeactivatedEvent) event;
            if (e.getDeactivatedID().equals(config.getSharedObjectID()))
                deactivated();
            else
                deactivated(e.getDeactivatedID());
        } else if (event instanceof IContainerConnectedEvent) {
            IContainerConnectedEvent e = (IContainerConnectedEvent) event;
            if (e.getTargetID().equals(e.getLocalContainerID()))
                connected();
            else
                connected(e.getTargetID());
        } else if (event instanceof IContainerDisconnectedEvent) {
            IContainerDisconnectedEvent e = (IContainerDisconnectedEvent) event;
            if (e.getTargetID().equals(e.getLocalContainerID()))
                disconnected();
            else
                disconnected(e.getTargetID());
        } else if (event instanceof ISharedObjectMessageEvent)
            received((ISharedObjectMessageEvent) event);
    }

    protected boolean isConnected() {
        return config.getContext().getConnectedID() != null;
    }

    protected boolean isPrimary() {
        return config.getContext().getLocalContainerID().equals(config.getHomeContainerID());
    }

    protected void activated(ID sharedObjectID) {
        if (isPrimary())
            return;
        ISharedObjectContext ctx = config.getContext();
        Object object = ctx.getSharedObjectManager().getSharedObject(sharedObjectID);
        if (object instanceof IPublishedService) {
            IPublishedService svc = (IPublishedService) object;
            Map props = svc.getProperties();
            PublishedServiceDescriptor desc = new PublishedServiceDescriptor(ctx.getLocalContainerID(), sharedObjectID, props);
            try {
                ctx.sendMessage(config.getHomeContainerID(), SerializationUtil.serialize(new DiscoveryMessage(DiscoveryMessage.ADDED, desc)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void activated() {
        if (isConnected())
            connected();
    }

    protected void deactivated(ID sharedObjectID) {
        if (isPrimary())
            return;
        ISharedObjectContext ctx = config.getContext();
        Object object = ctx.getSharedObjectManager().getSharedObject(sharedObjectID);
        if (object instanceof IPublishedService) {
            IPublishedService svc = (IPublishedService) object;
            Map props = svc.getProperties();
            PublishedServiceDescriptor desc = new PublishedServiceDescriptor(ctx.getLocalContainerID(), sharedObjectID, props);
            try {
                ctx.sendMessage(config.getHomeContainerID(), SerializationUtil.serialize(new DiscoveryMessage(DiscoveryMessage.REMOVED, desc)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void deactivated() {
        if (isPrimary() && isConnected())
            try {
                config.getContext().sendDispose(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    protected void connected(ID containerID) {
        if (isPrimary())
            try {
                config.getContext().sendCreate(containerID, new ReplicaSharedObjectDescription(getClass(), config.getSharedObjectID()));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    protected void connected() {
        if (isPrimary()) {
            try {
                config.getContext().sendCreate(null, new ReplicaSharedObjectDescription(getClass(), config.getSharedObjectID()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ArrayList published = new ArrayList();
            ISharedObjectContext ctx = config.getContext();
            ISharedObjectManager mgr = ctx.getSharedObjectManager();
            ID[] ids = mgr.getSharedObjectIDs();
            ID containerID = ctx.getLocalContainerID();
            for (int i = 0; i < ids.length; ++i) {
                Object object = mgr.getSharedObject(ids[i]);
                if (object instanceof IPublishedService) {
                    IPublishedService svc = (IPublishedService) object;
                    Map props = svc.getProperties();
                    published.add(new PublishedServiceDescriptor(containerID, ids[i], props));
                }
            }
            if (published.isEmpty())
                return;
            PublishedServiceDescriptor[] descriptors = new PublishedServiceDescriptor[published.size()];
            published.toArray(descriptors);
            try {
                ctx.sendMessage(config.getHomeContainerID(), SerializationUtil.serialize(new DiscoveryMessage(DiscoveryMessage.ADDED, descriptors)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void disconnected(ID containerID) {
        if (containerID.equals(config.getHomeContainerID()))
            config.getContext().getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
    }

    protected void disconnected() {
        config.getContext().getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
    }

    protected void received(ISharedObjectMessageEvent event) {
        Object data = event.getData();
        if (!(data instanceof byte[]))
            return;
        try {
            data = SerializationUtil.deserialize((byte[]) data);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (!(data instanceof DiscoveryMessage))
            return;
        directory.handleDiscovery(event.getRemoteContainerID(), (DiscoveryMessage) data);
    }

    public void handleEvents(Event[] events) {
        for (int i = 0; i < events.length; ++i) handleEvent(events[i]);
    }

    public void dispose(ID containerID) {
        config = null;
        directory = null;
    }
}
