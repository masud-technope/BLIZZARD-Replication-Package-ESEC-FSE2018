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
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContext;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.core.sharedobject.ReplicaSharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectCreateResponseEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectDeactivatedEvent;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.pubsub.IPublishedService;
import org.eclipse.ecf.pubsub.ISubscribedService;
import org.eclipse.ecf.pubsub.ISubscriber;
import org.eclipse.ecf.pubsub.ISubscription;
import org.eclipse.ecf.pubsub.ISubscriptionCallback;

public class SubscriptionAgent extends PlatformObject implements ISharedObject, ISubscriber {

    protected static final Object CONTAINER_ID_KEY = new Integer(0);

    protected static final Object SHARED_OBJECT_ID_KEY = new Integer(1);

    protected static final Object CALLBACK_KEY = new Integer(2);

    protected ISharedObjectConfig config;

    protected ID containerID;

    protected ID sharedObjectID;

    protected ISubscriptionCallback callback;

    protected ISubscribedService svc;

    protected boolean disposed;

    public synchronized void subscribed(ISubscribedService svc) {
        if (disposed)
            throw new IllegalStateException("Already disposed.");
        this.svc = svc;
        callback.subscribed(new Subscription());
    }

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        Map props = config.getProperties();
        if (config.getContext().getLocalContainerID().equals(config.getHomeContainerID())) {
            containerID = (ID) props.get(CONTAINER_ID_KEY);
            if (containerID == null)
                throw new SharedObjectInitException("containerID is required");
            callback = (ISubscriptionCallback) props.get(CALLBACK_KEY);
            if (callback == null)
                throw new SharedObjectInitException("callback is required");
        }
        sharedObjectID = (ID) props.get(SHARED_OBJECT_ID_KEY);
        if (sharedObjectID == null)
            throw new SharedObjectInitException("sharedObjectID is required");
        this.config = config;
    }

    public void handleEvent(Event event) {
        if (event instanceof ISharedObjectActivatedEvent) {
            ISharedObjectActivatedEvent e = (ISharedObjectActivatedEvent) event;
            if (e.getActivatedID().equals(config.getSharedObjectID()))
                activated();
        } else if (event instanceof ISharedObjectDeactivatedEvent) {
            ISharedObjectDeactivatedEvent e = (ISharedObjectDeactivatedEvent) event;
            if (e.getDeactivatedID().equals(config.getSharedObjectID()))
                deactivated();
        } else if (event instanceof ISharedObjectCreateResponseEvent)
            received((ISharedObjectCreateResponseEvent) event);
    }

    protected boolean isPrimary() {
        return config.getContext().getLocalContainerID().equals(config.getHomeContainerID());
    }

    protected void activated() {
        ISharedObjectContext ctx = config.getContext();
        if (isPrimary()) {
            try {
                ctx.sendCreate(containerID, createReplicaDescription());
            } catch (IOException e) {
                callback.requestFailed(e);
                ctx.getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
            }
            return;
        }
        ISharedObjectManager mgr = ctx.getSharedObjectManager();
        ISharedObject so = mgr.getSharedObject(sharedObjectID);
        try {
            ID homeContainerID = config.getHomeContainerID();
            if (so instanceof IPublishedService) {
                IPublishedService svc = (IPublishedService) so;
                svc.subscribe(homeContainerID, config.getSharedObjectID());
            } else {
                ctx.sendCreateResponse(homeContainerID, new IllegalArgumentException("Not an IPublishedService."), -1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ctx.getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
        }
    }

    protected void deactivated() {
        if (isPrimary()) {
            synchronized (this) {
                disposed = true;
                if (svc != null)
                    svc.unsubscribe(config.getSharedObjectID());
            }
        }
    }

    protected void received(ISharedObjectCreateResponseEvent e) {
        if (e.getRemoteContainerID().equals(containerID) && e.getSenderSharedObjectID().equals(config.getSharedObjectID())) {
            callback.requestFailed(e.getException());
            config.getContext().getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
        }
    }

    protected ReplicaSharedObjectDescription createReplicaDescription() {
        Map props = new HashMap(1);
        props.put(SHARED_OBJECT_ID_KEY, sharedObjectID);
        return new ReplicaSharedObjectDescription(getClass(), config.getSharedObjectID(), config.getHomeContainerID(), props);
    }

    public void handleEvents(Event[] events) {
        for (int i = 0; i < events.length; ++i) handleEvent(events[i]);
    }

    public void dispose(ID containerID) {
        config = null;
        callback = null;
        svc = null;
    }

    protected class Subscription implements ISubscription {

        public ID getID() {
            return sharedObjectID;
        }

        public ID getHomeContainerID() {
            return containerID;
        }

        public ISubscribedService getSubscribedService() {
            return svc;
        }

        public void dispose() {
            synchronized (SubscriptionAgent.this) {
                if (!disposed)
                    config.getContext().getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
            }
        }
    }
}
