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

import java.util.Arrays;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.core.variants.IResourceVariantComparator;
import org.eclipse.team.internal.ecf.core.TeamSynchronization;

class RemoteResourceVariantComparator implements IResourceVariantComparator {

    public boolean compare(IResource local, IResourceVariant remote) {
        RemoteResourceVariant remoteVariant = (RemoteResourceVariant) remote;
        int localType = local.getType();
        int remoteType = remoteVariant.getType();
        if (localType != remoteType) {
            return false;
        }
        if (localType != IResource.FILE && remoteType != IResource.FILE) {
            return local.getFullPath().toString().equals(remoteVariant.getPath());
        }
        if (localType == IResource.FILE && remote.isContainer()) {
            return false;
        }
        if (local instanceof IFile) {
            IFile file = (IFile) local;
            byte[] localBytes = TeamSynchronization.readFile(file);
            byte[] remoteBytes = remote.asBytes();
            return Arrays.equals(localBytes, remoteBytes);
        }
        return false;
    }

    public boolean compare(IResourceVariant base, IResourceVariant remote) {
        return true;
    }

    public boolean isThreeWay() {
        return false;
    }
}
