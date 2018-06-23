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
package org.eclipse.ecf.pubsub.model.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContext;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.example.pubsub.SerializationUtil;
import org.eclipse.ecf.pubsub.ISubscribedService;
import org.eclipse.ecf.pubsub.ISubscriber;
import org.eclipse.ecf.pubsub.impl.SubscribeMessage;
import org.eclipse.ecf.pubsub.impl.UnsubscribeMessage;
import org.eclipse.ecf.pubsub.model.IModelUpdater;
import org.eclipse.ecf.pubsub.model.IReplicaModel;

public class RemoteAgent extends AgentBase implements IReplicaModel, ISubscribedService {

    private Collection subscribers;

    private final Object subscriptionMutex = new Object();

    public void unsubscribe(ID requestorID) {
        synchronized (subscriptionMutex) {
            if (subscribers == null)
                return;
            subscribers.remove(requestorID);
            if (subscribers.isEmpty()) {
                ISharedObjectContext ctx = config.getContext();
                try {
                    ctx.sendMessage(config.getHomeContainerID(), new UnsubscribeMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ctx.getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
            }
        }
    }

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        super.init(config);
        subscribers = new HashSet();
        ID requestorID = (ID) config.getProperties().get(REQUESTOR_ID);
        if (requestorID != null)
            subscribers.add(requestorID);
    }

    protected void initializeData(Object data) throws SharedObjectInitException {
        try {
            this.data = SerializationUtil.deserialize((byte[]) data);
        } catch (IOException e) {
            throw new SharedObjectInitException(e);
        } catch (ClassNotFoundException e) {
            throw new SharedObjectInitException(e);
        }
    }

    protected void initializeUpdater() throws SharedObjectInitException {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        if (registry == null)
            throw new SharedObjectInitException("No Platform Extension Registry.");
        IConfigurationElement[] elements = registry.getConfigurationElementsFor("org.eclipse.ecf.example.pubsub.modelUpdater");
        for (int i = 0; i < elements.length; ++i) {
            if (updaterID.equals(elements[i].getAttribute("id"))) {
                try {
                    updater = (IModelUpdater) elements[i].createExecutableExtension("class");
                } catch (CoreException e) {
                    throw new SharedObjectInitException(e);
                } catch (ClassCastException e) {
                    throw new SharedObjectInitException(e);
                }
                break;
            }
        }
        if (updater == null)
            throw new SharedObjectInitException("Could not find specified Model Updater.");
    }

    protected void activated() {
        ID requestorID = (ID) config.getProperties().get(REQUESTOR_ID);
        if (requestorID != null) {
            Object svc = config.getContext().getSharedObjectManager().getSharedObject(requestorID);
            if (svc instanceof ISubscriber)
                ((ISubscriber) svc).subscribed(this);
        }
    }

    protected void disconnected() {
        config.getContext().getSharedObjectManager().removeSharedObject(config.getSharedObjectID());
    }

    protected void disconnected(ID containerID) {
        if (containerID.equals(config.getHomeContainerID()))
            disconnected();
    }

    protected void received(ID containerID, Object data) {
        if (!(data instanceof byte[]))
            return;
        if (data instanceof SubscribeMessage) {
            SubscribeMessage msg = (SubscribeMessage) data;
            synchronized (subscriptionMutex) {
                subscribers.add(msg.getRequestorID());
            }
            return;
        }
        try {
            updater.update(this.data, SerializationUtil.deserialize((byte[]) data));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
