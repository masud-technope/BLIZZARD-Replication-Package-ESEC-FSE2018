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

import java.util.Map;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IIdentifiable;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectDeactivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectMessageEvent;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.pubsub.model.IModelUpdater;

public abstract class AgentBase extends PlatformObject implements ISharedObject, IIdentifiable {

    public static final Object INITIAL_DATA_KEY = new Integer(0);

    public static final Object MODEL_UPDATER_KEY = new Integer(1);

    protected static final Object REQUESTOR_ID = new Integer(2);

    protected ISharedObjectConfig config;

    protected Object data;

    protected String updaterID;

    protected IModelUpdater updater;

    public void init(ISharedObjectConfig config) throws SharedObjectInitException {
        Map props = config.getProperties();
        initializeData(props.get(INITIAL_DATA_KEY));
        updaterID = (String) props.get(MODEL_UPDATER_KEY);
        if (updaterID == null)
            throw new SharedObjectInitException("Model Updater is required.");
        initializeUpdater();
        this.config = config;
    }

    protected abstract void initializeData(Object data) throws SharedObjectInitException;

    protected void initializeUpdater() throws SharedObjectInitException {
    }

    public Object getData() {
        return data;
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
        } else if (event instanceof ISharedObjectMessageEvent) {
            ISharedObjectMessageEvent e = (ISharedObjectMessageEvent) event;
            received(e.getRemoteContainerID(), e.getData());
        }
    }

    protected boolean isConnected() {
        return config.getContext().getConnectedID() != null;
    }

    protected void activated(ID sharedObjectID) {
    }

    protected void activated() {
    }

    protected void deactivated(ID sharedObjectID) {
    }

    protected void deactivated() {
    }

    protected void connected(ID containerID) {
    }

    protected void connected() {
    }

    protected void disconnected(ID containerID) {
    }

    protected void disconnected() {
    }

    protected void received(ID containerID, Object data) {
    }

    public void handleEvents(Event[] events) {
        for (int i = 0; i < events.length; ++i) handleEvent(events[i]);
    }

    public ID getID() {
        return config.getSharedObjectID();
    }

    public void dispose(ID containerID) {
        config = null;
        data = null;
        updater = null;
    }
}
