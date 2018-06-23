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
package org.eclipse.team.internal.ecf.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.core.synchronize.SyncInfo;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.core.variants.IResourceVariantComparator;

public final class RemoteSyncInfo extends SyncInfo {

    private final IResource local;

    private final IResourceVariant remote;

    public  RemoteSyncInfo(IResource local, IResourceVariant remote, IResourceVariantComparator comparator) {
        super(local, null, remote, comparator);
        this.local = local;
        this.remote = remote;
    }

    protected int calculateKind() throws TeamException {
        if (remote == null && !local.exists()) {
            // isSupervised(IResource) implementation
            return IN_SYNC;
        }
        int kind = super.calculateKind();
        switch(kind) {
            case ADDITION:
                kind |= (kind & ~DIRECTION_MASK) | INCOMING;
                break;
            case DELETION:
                kind |= (kind & ~DIRECTION_MASK) | OUTGOING;
                break;
        }
        return kind;
    }
}
