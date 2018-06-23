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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContext;
import org.eclipse.ecf.core.sharedobject.ReplicaSharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.example.pubsub.SerializationUtil;
import org.eclipse.ecf.pubsub.IPublishedService;
import org.eclipse.ecf.pubsub.impl.SubscribeMessage;
import org.eclipse.ecf.pubsub.impl.UnsubscribeMessage;
import org.eclipse.ecf.pubsub.model.IMasterModel;

public class LocalAgent extends AgentBase implements IPublishedService, IMasterModel {

    protected Collection subscriptions;

    private final Object subscriptionMutex = new Object();

    protected void initializeData(Object data) throws SharedObjectInitException {
        this.data = data;
    }

    public synchronized void update(Object data) throws IOException {
        config.getContext().sendMessage(null, SerializationUtil.serialize(data));
    }

    public Map getProperties() {
        return Collections.EMPTY_MAP;
    }

    public void subscribe(ID containerID, ID requestorID) {
        synchronized (subscriptionMutex) {
            if (subscriptions == null)
                subscriptions = new HashSet();
            ISharedObjectContext ctx = config.getContext();
            try {
                if (subscriptions.add(containerID)) {
                    ctx.sendCreate(containerID, createRemoteAgentDescription(requestorID));
                } else {
                    SubscribeMessage msg = new SubscribeMessage(requestorID);
                    ctx.sendMessage(containerID, SerializationUtil.serialize(msg));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void received(ID containerID, Object data) {
        if (!(data instanceof byte[]))
            return;
        Object msg;
        try {
            msg = SerializationUtil.deserialize((byte[]) data);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (!(msg instanceof UnsubscribeMessage))
            return;
        synchronized (subscriptionMutex) {
            if (subscriptions != null)
                subscriptions.remove(containerID);
        }
    }

    protected void disconnected(ID containerID) {
        synchronized (subscriptionMutex) {
            if (subscriptions != null)
                subscriptions.remove(containerID);
        }
    }

    protected void deactivated() {
        if (isConnected())
            try {
                config.getContext().sendDispose(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    protected ReplicaSharedObjectDescription createRemoteAgentDescription(ID requestorID) {
        Map props = new HashMap(3);
        try {
            props.put(INITIAL_DATA_KEY, SerializationUtil.serialize(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        props.put(MODEL_UPDATER_KEY, updaterID);
        props.put(REQUESTOR_ID, requestorID);
        return new ReplicaSharedObjectDescription(RemoteAgent.class, config.getSharedObjectID(), config.getHomeContainerID(), props);
    }
}
