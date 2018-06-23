/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ecf.core.variants;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.team.core.variants.*;
import org.eclipse.team.internal.ecf.core.RemoteShare;
import org.eclipse.team.internal.ecf.core.RemoteSyncInfo;

public class RemoteResourceVariantTreeSubscriber extends ResourceVariantTreeSubscriber {

    private RemoteResourceVariantTree remoteTree;

    private IResource[] resources;

    private RemoteResourceVariantComparator comparator = new RemoteResourceVariantComparator();

    public  RemoteResourceVariantTreeSubscriber(RemoteShare share, ID ownId, ID remoteId) {
        remoteTree = new RemoteResourceVariantTree(share, ownId, remoteId);
    }

    public void setResources(IResource[] resources) {
        this.resources = resources;
        remoteTree.setResources(resources);
    }

    protected IResourceVariantTree getBaseTree() {
        // no base
        return null;
    }

    protected IResourceVariantTree getRemoteTree() {
        return remoteTree;
    }

    public String getName() {
        //$NON-NLS-1$
        return "getName();";
    }

    public IResourceVariantComparator getResourceComparator() {
        return comparator;
    }

    public SyncInfo getSyncInfo(IResource local, IProgressMonitor monitor) throws TeamException {
        IResourceVariant remote = remoteTree.fetchVariant(local, IResource.DEPTH_ZERO, monitor);
        return getSyncInfo(local, null, remote);
    }

    protected SyncInfo getSyncInfo(IResource local, IResourceVariant base, IResourceVariant remote) throws TeamException {
        SyncInfo info = new RemoteSyncInfo(local, remote, comparator);
        info.init();
        return info;
    }

    public boolean isSupervised(IResource resource) {
        return true;
    }

    public IResource[] roots() {
        return resources == null ? new IResource[0] : resources;
    }
}
