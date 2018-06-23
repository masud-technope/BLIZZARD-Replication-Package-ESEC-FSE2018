/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
/*
 * Created on Dec 6, 2004
 *  
 */
package org.eclipse.ecf.provider.generic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.sharedobject.util.IQueueEnqueue;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.internal.provider.ECFProviderDebugOptions;
import org.eclipse.ecf.internal.provider.ProviderPlugin;

public class SOContext implements ISharedObjectContext {

    protected SOContainer container = null;

    protected ID sharedObjectID;

    protected ID homeContainerID;

    protected boolean isActive;

    protected Map properties;

    protected IQueueEnqueue queue;

    public  SOContext(ID objID, ID homeID, SOContainer cont, Map props, IQueueEnqueue queue) {
        super();
        this.sharedObjectID = objID;
        this.homeContainerID = homeID;
        this.container = cont;
        this.properties = props;
        this.queue = queue;
        isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    protected void trace(String msg) {
        //$NON-NLS-1$
        Trace.trace(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.SHAREDOBJECTCONTEXT, msg + ":" + container.getID());
    }

    protected void traceStack(String msg, Throwable e) {
        //$NON-NLS-1$
        Trace.catching(ProviderPlugin.PLUGIN_ID, ECFProviderDebugOptions.EXCEPTIONS_CATCHING, SOContext.class, container.getID() + ":" + msg, e);
    }

    protected void makeInactive() {
        isActive = false;
    }

    protected boolean isInactive() {
        return !isActive;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#getContainerID()
	 */
    public ID getLocalContainerID() {
        return container.getID();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#getSharedObjectManager()
	 */
    public ISharedObjectManager getSharedObjectManager() {
        if (isInactive()) {
            return null;
        }
        return container.getSharedObjectManager();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#getQueue()
	 */
    public IQueueEnqueue getQueue() {
        if (isInactive()) {
            return null;
        }
        return queue;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#connect(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.core.security.IConnectContext)
	 */
    public void connect(ID groupID, IConnectContext joinContext) throws ContainerConnectException {
        if (isInactive())
            return;
        container.connect(groupID, joinContext);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#disconnect()
	 */
    public void disconnect() {
        if (isInactive()) {
            //$NON-NLS-1$
            trace("leaveGroup() CONTEXT INACTIVE");
            return;
        }
        //$NON-NLS-1$
        trace("leaveGroup()");
        container.disconnect();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#getConnectedID()
	 */
    public ID getConnectedID() {
        if (isInactive()) {
            return null;
        }
        return container.getConnectedID();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#isGroupManager()
	 */
    public boolean isGroupManager() {
        if (isInactive()) {
            return false;
        }
        return container.isGroupManager();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#isGroupServer()
	 */
    public boolean isGroupServer() {
        if (isInactive()) {
            return false;
        }
        return container.isGroupManager();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#getGroupMembership()
	 */
    public ID[] getGroupMemberIDs() {
        if (isInactive()) {
            return new ID[0];
        }
        return container.getGroupMemberIDs();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#sendCreate(org.eclipse.ecf.core.identity.ID,
	 *      org.eclipse.ecf.core.ReplicaSharedObjectDescription)
	 */
    public void sendCreate(ID toContainerID, ReplicaSharedObjectDescription sd) throws IOException {
        if (isInactive()) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            trace("sendCreate(" + toContainerID + "," + sd + ") CONTEXT INACTIVE");
            return;
        }
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        trace("sendCreate(" + toContainerID + "," + sd + ")");
        container.sendCreate(sharedObjectID, toContainerID, sd);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#sendCreateResponse(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Throwable, long)
	 */
    public void sendCreateResponse(ID toContainerID, Throwable throwable, long identifier) throws IOException {
        if (isInactive()) {
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            trace("sendCreateResponse(" + toContainerID + "," + throwable + "," + identifier + ") CONTEXT INACTIVE");
            return;
        }
        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        trace("sendCreateResponse(" + toContainerID + "," + throwable + "," + identifier + ")");
        container.sendCreateResponse(toContainerID, sharedObjectID, throwable, identifier);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#sendDispose(org.eclipse.ecf.core.identity.ID)
	 */
    public void sendDispose(ID toContainerID) throws IOException {
        if (isInactive()) {
            //$NON-NLS-1$ //$NON-NLS-2$
            trace("sendDispose(" + toContainerID + ") CONTEXT INACTIVE");
            return;
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("sendDispose(" + toContainerID + ")");
        container.sendDispose(toContainerID, sharedObjectID);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#sendMessage(org.eclipse.ecf.core.identity.ID,
	 *      java.lang.Object)
	 */
    public void sendMessage(ID toContainerID, Object data) throws IOException {
        if (isInactive())
            return;
        container.sendMessage(toContainerID, sharedObjectID, data);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObjectContext#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class clazz) {
        return container.getAdapter(clazz);
    }

    public Namespace getConnectNamespace() {
        if (isInactive()) {
            return null;
        }
        return container.getConnectNamespace();
    }

    public Map getLocalContainerProperties() {
        if (isInactive())
            return new HashMap();
        return container.createContainerPropertiesForSharedObject(sharedObjectID);
    }
}
