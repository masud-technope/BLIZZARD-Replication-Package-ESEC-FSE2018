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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.variants.AbstractResourceVariantTree;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ecf.core.RemoteShare;

final class RemoteResourceVariantTree extends AbstractResourceVariantTree {

    private final RemoteShare share;

    private final ID ownId;

    private final ID remoteId;

    private IResource[] roots;

    public  RemoteResourceVariantTree(RemoteShare share, ID ownId, ID remoteId) {
        this.share = share;
        this.ownId = ownId;
        this.remoteId = remoteId;
    }

    public void setResources(IResource[] roots) {
        this.roots = roots;
    }

    protected IResourceVariant[] fetchMembers(IResourceVariant variant, IProgressMonitor monitor) throws TeamException {
        return share.fetchMembers(ownId, remoteId, variant, monitor);
    }

    protected IResourceVariant fetchVariant(IResource resource, int depth, IProgressMonitor monitor) throws TeamException {
        return share.fetchVariant(ownId, remoteId, resource, monitor);
    }

    protected boolean setVariant(IResource local, IResourceVariant remote) {
        return true;
    }

    public void flushVariants(IResource resource, int depth) {
    // does not appear to be called by the Team APIs
    }

    public IResourceVariant getResourceVariant(IResource resource) throws TeamException {
        return share.getResourceVariant(ownId, remoteId, resource);
    }

    public boolean hasResourceVariant(IResource resource) {
        return true;
    }

    public IResource[] members(IResource resource) throws TeamException {
        if (resource.getType() == IResource.FILE || !resource.exists()) {
            return new IResource[0];
        }
        try {
            IResource[] members = ((IContainer) resource).members();
            List nonDerivedMembers = new ArrayList(members.length);
            for (int i = 0; i < members.length; i++) {
                if (!members[i].isDerived()) {
                    nonDerivedMembers.add(members[i]);
                }
            }
            return (IResource[]) nonDerivedMembers.toArray(new IResource[nonDerivedMembers.size()]);
        } catch (CoreException e) {
            throw new TeamException(e.getStatus());
        }
    }

    public IResource[] roots() {
        return roots == null ? new IResource[0] : roots;
    }
}
