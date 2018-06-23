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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.events.IContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.core.sharedobject.SharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectDeactivatedEvent;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.pubsub.IPublishedServiceDirectory;
import org.eclipse.ecf.pubsub.IPublishedServiceDirectoryListener;
import org.eclipse.ecf.pubsub.PublishedServiceDescriptor;
import org.eclipse.ecf.pubsub.PublishedServiceDirectoryChangeEvent;

public class PublishedServiceDirectory extends PlatformObject implements ISharedObject, IPublishedServiceDirectory {

    protected static final String SHARED_OBJECT_ID = IPublishedServiceDirectory.class.getName();

    protected ISharedObjectConfig config;

    private final ListenerList listeners = new ListenerList();

    private final Map services = new HashMap();

    private ID discoveryAgentID;

    public synchronized void addReplicatedServiceListener(final IPublishedServiceDirectoryListener listener) {
        listeners.add(listener);
        PublishedServiceDescriptor[] buf = new PublishedServiceDescriptor[services.values().size()];
        services.values().toArray(buf);
        final PublishedServiceDirectoryChangeEvent event = new PublishedServiceDirectoryChangeEvent(this, PublishedServiceDirectoryChangeEvent.ADDED, buf);
        SafeRunner.run(new ISafeRunnable() {

            public void run() throws Exception {
                listener.publishedServiceDirectoryChanged(event);
            }

            public void handleException(Throwable exception) {
                // TODO Auto-generated method stub
                exception.printStackTrace();
            }
        });
    }

    public void removeReplicatedServiceListener(IPublishedServiceDirectoryListener listener) {
        listeners.remove(listener);
    }

    protected void fireServiceChangedEvent(final PublishedServiceDirectoryChangeEvent event) {
        Object[] l = listeners.getListeners();
        for (int i = 0; i < l.length; ++i) {
            final IPublishedServiceDirectoryListener listener = (IPublishedServiceDirectoryListener) l[i];
            SafeRunner.run(new ISafeRunnable() {

                public void run() throws Exception {
                    listener.publishedServiceDirectoryChanged(event);
                }

                public void handleException(Throwable exception) {
                    // TODO Auto-generated method stub
                    exception.printStackTrace();
                }
            });
        }
    }

    void handleDiscovery(ID containerID, DiscoveryMessage msg) {
        PublishedServiceDescriptor[] descriptors = msg.getDescriptors();
        synchronized (this) {
            Collection values = (Collection) services.get(containerID);
            if (values == null) {
                values = new HashSet();
                services.put(containerID, values);
            }
            if (msg.getKind() == DiscoveryMessage.ADDED) {
                values.addAll(Arrays.asList(descriptors));
            } else {
                values.removeAll(Arrays.asList(descriptors));
                if (values.isEmpty())
                    services.remove(containerID);
            }
            int kind = msg.getKind() == DiscoveryMessage.ADDED ? PublishedServiceDirectoryChangeEvent.ADDED : PublishedServiceDirectoryChangeEvent.REMOVED;
            fireServiceChangedEvent(new PublishedServiceDirectoryChangeEvent(this, kind, descriptors));
        }
    }

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        this.config = config;
    }

    public void handleEvent(Event event) {
        if (event instanceof ISharedObjectActivatedEvent)
            activated(((ISharedObjectActivatedEvent) event).getActivatedID());
        else if (event instanceof ISharedObjectDeactivatedEvent)
            deactivated(((ISharedObjectDeactivatedEvent) event).getDeactivatedID());
        else if (event instanceof IContainerDisconnectedEvent)
            disconnected((IContainerDisconnectedEvent) event);
    }

    protected void activated(final ID sharedObjectID) {
        if (sharedObjectID.equals(config.getSharedObjectID())) {
            ISharedObjectManager mgr = config.getContext().getSharedObjectManager();
            if (discoveryAgentID == null) {
                try {
                    discoveryAgentID = IDFactory.getDefault().createGUID();
                    mgr.createSharedObject(createDiscoveryAgentDescription());
                } catch (ECFException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void deactivated(ID sharedObjectID) {
        if (sharedObjectID.equals(config.getSharedObjectID())) {
            if (discoveryAgentID != null) {
                config.getContext().getSharedObjectManager().removeSharedObject(discoveryAgentID);
                discoveryAgentID = null;
            }
        }
    }

    protected void disconnected(IContainerDisconnectedEvent event) {
        ID containerID = event.getTargetID();
        if (!containerID.equals(event.getLocalContainerID())) {
            synchronized (this) {
                Collection values = (Collection) services.remove(event.getTargetID());
                if (values != null) {
                    PublishedServiceDescriptor[] buf = new PublishedServiceDescriptor[values.size()];
                    values.toArray(buf);
                    fireServiceChangedEvent(new PublishedServiceDirectoryChangeEvent(this, PublishedServiceDirectoryChangeEvent.REMOVED, buf));
                }
            }
        }
    }

    public void handleEvents(Event[] events) {
        for (int i = 0; i < events.length; ++i) handleEvent(events[i]);
    }

    public void dispose(ID containerID) {
        listeners.clear();
        synchronized (this) {
            services.clear();
        }
        config = null;
    }

    protected SharedObjectDescription createDiscoveryAgentDescription() {
        HashMap props = new HashMap(1);
        props.put(DiscoveryAgent.DIRECTORY_KEY, this);
        return new SharedObjectDescription(DiscoveryAgent.class, discoveryAgentID, props);
    }
}
