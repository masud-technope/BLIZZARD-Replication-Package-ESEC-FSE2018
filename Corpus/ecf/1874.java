/*******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Remy Chi Jian Suen <remy.suen@gmail.com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.sync.resources.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.AbstractShare;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.sync.IModelChange;
import org.eclipse.ecf.sync.IModelChangeMessage;

public class ResourcesShare extends AbstractShare {

    private Boolean response;

    private Set sharedProjects = new HashSet();

    private ID receiverID;

    private ID containerID;

    private ID localID;

    public  ResourcesShare(ID containerID, IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
        this.containerID = containerID;
    }

    private void attachListener() {
        if (sharedProjects.size() == 1) {
            SyncResourcesCore.getDefault().attachListener();
        }
    }

    private void detachListener() {
        if (sharedProjects.isEmpty()) {
            SyncResourcesCore.getDefault().detachListener();
        }
    }

    public void sendResponse(boolean accept, String projectName) {
        try {
            if (accept) {
                send(receiverID, new AcceptMessage(projectName));
            } else {
                send(receiverID, new DenyMessage(projectName));
            }
        } catch (ECFException e) {
            e.printStackTrace();
        }
    }

    public Boolean getResponse() {
        if (response == null) {
            return null;
        }
        Boolean temp = response;
        response = null;
        return temp;
    }

    public ID getContainerID() {
        return containerID;
    }

    public ID getReceiverID() {
        return receiverID;
    }

    public ID getLocalID() {
        return localID;
    }

    public boolean isSharing(String projectName) {
        synchronized (sharedProjects) {
            return sharedProjects.contains(projectName);
        }
    }

    public void startShare(ID fromId, ID toID, String projectName) throws ECFException {
        if (sharedProjects.add(projectName)) {
            // reset in case we have a stale one
            response = null;
            try {
                send(toID, new StartMessage(projectName, fromId, toID));
                localID = fromId;
                receiverID = toID;
                attachListener();
            } catch (ECFException e) {
                receiverID = null;
                sharedProjects.remove(projectName);
                detachListener();
                throw e;
            }
        }
    }

    public void stopSharing(String projectName) {
        if (sharedProjects.remove(projectName)) {
            try {
                send(receiverID, new StopMessage(projectName));
            } catch (ECFException e) {
                e.printStackTrace();
            } finally {
                receiverID = null;
            }
            detachListener();
        }
    }

    private void send(ID toID, Message message) throws ECFException {
        sendMessage(toID, message.serialize());
    }

    void send(byte[] bytes) throws ECFException {
        sendMessage(receiverID, bytes);
    }

    void sendResourceChangeMessage(IResource resource, int kind) {
        try {
            IModelChange change = ResourceChangeMessage.createResourceChange(resource, kind);
            IModelChangeMessage[] messages = ResourcesSynchronizationStrategy.getInstance().registerLocalChange(change);
            for (int i = 0; i < messages.length; i++) {
                send(messages[i].serialize());
            }
        } catch (ECFException e) {
            e.printStackTrace();
        }
    }

    protected void handleStartMessage(StartMessage msg) {
        receiverID = msg.getFromId();
        localID = msg.getLocalId();
        sharedProjects.add(msg.getProjectName());
        attachListener();
    }

    private void handleStopMessage(StopMessage msg) {
        sharedProjects.remove(msg.getProjectName());
        detachListener();
    }

    private void handleResourceChangeMessage(byte[] data) throws Exception {
        IModelChange remoteChange = ResourcesSynchronizationStrategy.getInstance().deserializeRemoteChange(data);
        final IModelChange[] remoteChanges = ResourcesSynchronizationStrategy.getInstance().transformRemoteChange(remoteChange);
        // create a scheduling rule to lock the projects
        ISchedulingRule[] rules = new ISchedulingRule[sharedProjects.size()];
        int index = 0;
        for (Iterator it = sharedProjects.iterator(); it.hasNext(); ) {
            String projectName = (String) it.next();
            rules[index] = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            index++;
        }
        try {
            // lock to prevent resource changes from being propagated
            lock(remoteChanges);
            applyRemoteChanges(remoteChanges, new MultiRule(rules));
        } finally {
            // unlock now that we've applied the remote changes to our
            // own workspace
            unlock(remoteChanges);
        }
        if (remoteChange instanceof BatchModelChange) {
            BatchModelChange batchChange = (BatchModelChange) remoteChange;
            batchChange.setOutgoing(false);
            batchChange.setTime(System.currentTimeMillis());
            SyncResourcesCore.add(batchChange);
        }
    }

    protected void lock(IModelChange[] remoteChanges) {
        SyncResourcesCore.lock();
    }

    protected void unlock(IModelChange[] remoteChanges) {
        SyncResourcesCore.unlock();
    }

    private void applyRemoteChanges(final IModelChange[] remoteChanges, ISchedulingRule rule) throws CoreException {
        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

            public void run(IProgressMonitor monitor) throws CoreException {
                monitor.beginTask("Processing remote changes...", remoteChanges.length);
                for (int i = 0; i < remoteChanges.length; i++) {
                    if (monitor.isCanceled()) {
                        return;
                    }
                    // applies the resource changes
                    remoteChanges[i].applyToModel(containerID);
                    monitor.worked(1);
                }
                monitor.done();
            }
        }, rule, IWorkspace.AVOID_UPDATE, null);
    }

    protected void handleMessage(ID fromContainerID, byte[] data) {
        try {
            Object message = Message.deserialize(data);
            if (message instanceof StartMessage) {
                handleStartMessage((StartMessage) message);
            } else if (message instanceof StopMessage) {
                handleStopMessage((StopMessage) message);
            } else if (message instanceof AcceptMessage) {
                response = Boolean.TRUE;
            } else if (message instanceof DenyMessage) {
                sharedProjects.remove(((DenyMessage) message).getProjectName());
                receiverID = null;
                detachListener();
                response = Boolean.FALSE;
            } else {
                handleResourceChangeMessage(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
