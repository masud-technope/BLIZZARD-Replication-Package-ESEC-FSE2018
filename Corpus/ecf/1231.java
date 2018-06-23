/******************************************************************************
 * Copyright (c) 2008, 2009 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.core;

import java.io.*;
import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.AbstractShare;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ecf.core.messages.*;
import org.eclipse.team.internal.ecf.core.variants.RemoteResourceVariant;

public class RemoteShare extends AbstractShare {

    private Map participants = new HashMap();

    private Object returnValue;

    public  RemoteShare(IChannelContainerAdapter adapter) throws ECFException {
        super(adapter);
    }

    public synchronized boolean sendShareRequest(ID localId, ID remoteId, IResource[] resources, IProgressMonitor monitor) throws ECFException, InterruptedException {
        Assert.isNotNull(localId);
        Assert.isNotNull(remoteId);
        Assert.isNotNull(resources);
        sendMessage(remoteId, serialize(new ShareRequest(localId, resources)));
        while (returnValue == null) {
            Thread.sleep(100);
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
        }
        Object tmpValue = returnValue;
        returnValue = null;
        return ((Boolean) tmpValue).booleanValue();
    }

    public synchronized IResourceVariant[] fetchMembers(ID ownId, ID remoteId, IResourceVariant variant, IProgressMonitor monitor) throws TeamException {
        RemoteResourceVariant remoteVariant = (RemoteResourceVariant) variant;
        if (!remoteVariant.hasMembers()) {
            // members, just return an empty array
            return new IResourceVariant[0];
        }
        monitor.subTask(NLS.bind(Messages.RemoteShare_FetchingVariant, variant.getName()));
        sendMessage(remoteId, new FetchVariantsRequest(ownId, remoteVariant.getPath(), remoteVariant.getType()));
        while (returnValue == null) {
            try {
                Thread.sleep(100);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new TeamException("Interrupted whilst fetching members");
            }
        }
        monitor.done();
        Object tmpValue = returnValue;
        returnValue = null;
        return (IResourceVariant[]) tmpValue;
    }

    public synchronized IResourceVariant fetchVariant(ID ownId, ID remoteId, IResource resource, IProgressMonitor monitor) throws TeamException {
        monitor.subTask(NLS.bind(Messages.RemoteShare_FetchingVariant, resource.getFullPath().toString().substring(1)));
        sendMessage(remoteId, new FetchVariantRequest(ownId, resource.getFullPath().toString(), resource.getType()));
        while (returnValue == null) {
            try {
                Thread.sleep(100);
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new TeamException("Interrupted whilst fetching variant");
            }
        }
        monitor.done();
        Object tmpValue = returnValue;
        returnValue = null;
        return ((IResourceVariant[]) tmpValue)[0];
    }

    public synchronized IResourceVariant getResourceVariant(ID ownId, ID remoteId, IResource resource) throws TeamException {
        sendMessage(remoteId, new FetchVariantRequest(ownId, resource.getFullPath().toString(), resource.getType()));
        // needs to be implemented at the preferences layer
        while (returnValue == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.interrupted();
                throw new TeamException("Interrupted whilst getting variant");
            }
        }
        Object tmpValue = returnValue;
        returnValue = null;
        return ((IResourceVariant[]) tmpValue)[0];
    }

    private void sendMessage(ID id, Serializable serializable) {
        try {
            sendMessage(id, serialize(serializable));
        } catch (ECFException e) {
            TeamSynchronization.log("Could not send message to peer " + id, e);
        }
    }

    protected void handleMessage(ID fromContainerId, byte[] data) {
        Object message = deserialize(data);
        if (message instanceof FetchVariantsRequest) {
            FetchVariantRequest msg = (FetchVariantRequest) message;
            String path = msg.getPath();
            IContainer container = null;
            switch(msg.getType()) {
                case IResource.FILE:
                    // this shouldn't happen as the fetch request should not have
                    // been sent for files in the first place
                    TeamSynchronization.log("Files should not have any variants to request for");
                    break;
                case IResource.PROJECT:
                    container = ResourcesPlugin.getWorkspace().getRoot().getProject(new Path(path).lastSegment());
                    break;
                case IResource.FOLDER:
                    container = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(path));
                    break;
                default:
                    TeamSynchronization.log("Unsupported resource type specified: " + msg.getType());
                    break;
            }
            sendFetchVariantsResponse(msg.getFromId(), container);
        } else if (message instanceof FetchVariantRequest) {
            FetchVariantRequest msg = (FetchVariantRequest) message;
            IResource resource = null;
            IPath path = new Path(msg.getPath());
            switch(msg.getType()) {
                case IResource.PROJECT:
                    resource = ResourcesPlugin.getWorkspace().getRoot().getProject(path.lastSegment());
                    break;
                case IResource.FOLDER:
                    resource = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
                    break;
                case IResource.FILE:
                    resource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                    break;
                default:
                    TeamSynchronization.log("Unsupported resource type specified: " + msg.getType());
                    break;
            }
            sendFetchVariantResponse(msg.getFromId(), resource);
        } else if (message instanceof ShareRequest) {
            ShareRequest request = (ShareRequest) message;
            boolean response = prompt(request.getFromId(), request.getPaths());
            if (response) {
                response = false;
                String[] paths = request.getPaths();
                int[] types = request.getTypes();
                IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
                resourceLoop: for (int i = 0; i < paths.length; i++) {
                    switch(types[i]) {
                        case IResource.PROJECT:
                            if (workspaceRoot.getProject(new Path(paths[i]).lastSegment()).exists()) {
                                response = true;
                                break resourceLoop;
                            }
                            break;
                        case IResource.FOLDER:
                            if (workspaceRoot.getFolder(new Path(paths[i])).exists()) {
                                response = true;
                                break resourceLoop;
                            }
                            break;
                        case IResource.FILE:
                            if (workspaceRoot.getFile(new Path(paths[i])).exists()) {
                                response = true;
                                break resourceLoop;
                            }
                            break;
                    }
                }
            }
            sendMessage(request.getFromId(), new ShareResponse(response));
        } else if (message instanceof IResponse) {
            returnValue = ((IResponse) message).getResponse();
        }
    }

    protected boolean prompt(ID fromId, String[] paths) {
        return false;
    }

    private void sendFetchVariantsResponse(ID fromId, IContainer container) {
        // the container will be null if an invalid resource type was provided
        if (container == null) {
            sendMessage(fromId, new FetchResponse());
            return;
        }
        try {
            IResource[] members = container.members();
            List variants = new ArrayList();
            for (int i = 0; i < members.length; i++) {
                if (!members[i].isDerived()) {
                    variants.add(new RemoteResourceVariant(members[i]));
                }
            }
            IResourceVariant[] variantsArray = (IResourceVariant[]) variants.toArray(new IResourceVariant[variants.size()]);
            sendMessage(fromId, new FetchResponse(variantsArray));
        } catch (CoreException e) {
            TeamSynchronization.log("Could not retrieve container members: " + container.getFullPath(), e);
        }
    }

    private void sendFetchVariantResponse(ID fromId, IResource resource) {
        // resource will be null if an invalid resource type was provided
        if (resource != null && resource.exists()) {
            sendMessage(fromId, new FetchResponse(new RemoteResourceVariant(resource)));
        } else {
            sendMessage(fromId, new FetchResponse((IResourceVariant) null));
        }
    }

    public synchronized void dispose() {
        participants.clear();
        super.dispose();
    }

    private static Object deserialize(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream inputStream = new ObjectInputStream(bais);
            return inputStream.readObject();
        } catch (IOException e) {
            TeamSynchronization.log("Could not read deserialize data", e);
            return null;
        } catch (ClassNotFoundException e) {
            TeamSynchronization.log("Could not find class for deserialization", e);
            return null;
        }
    }

    private static byte[] serialize(Serializable serializable) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(serializable);
            return baos.toByteArray();
        } catch (IOException e) {
            TeamSynchronization.log("Could not read serialize object", e);
            return null;
        }
    }
}
