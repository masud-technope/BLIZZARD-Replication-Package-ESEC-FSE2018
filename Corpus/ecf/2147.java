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

import java.io.Serializable;
import java.util.Date;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.team.core.variants.IResourceVariant;
import org.eclipse.team.internal.ecf.core.TeamSynchronization;

public class RemoteResourceVariant implements IResourceVariant, Serializable {

    private static final long serialVersionUID = 2103652156613434181L;

    private final String name;

    private final String path;

    private final int type;

    private final long timeStamp;

    private final boolean hasMembers;

    private byte[] bytes;

    private IStorage storage;

    public  RemoteResourceVariant(IResource resource) {
        name = resource.getName();
        path = resource.getFullPath().toString();
        type = resource.getType();
        timeStamp = resource.getLocalTimeStamp();
        //$NON-NLS-1$
        Assert.isLegal(type == IResource.FILE || type == IResource.FOLDER || type == IResource.PROJECT, "Invalid resource type specified: " + type);
        if (type == IResource.FILE) {
            hasMembers = false;
            IFile file = (IFile) resource;
            bytes = TeamSynchronization.readFile(file);
            if (bytes != null) {
                try {
                    storage = new RemoteStorage(path, file.getCharset(), bytes);
                } catch (CoreException e) {
                    TeamSynchronization.log("Could not retrieve file charset", e);
                }
            }
        } else {
            boolean temp = false;
            try {
                if (resource.exists()) {
                    IResource[] members = ((IContainer) resource).members();
                    // we are only interested in members that are not derived
                    for (int i = 0; i < members.length; i++) {
                        if (!members[i].isDerived()) {
                            temp = true;
                            break;
                        }
                    }
                }
            } catch (CoreException e) {
            }
            hasMembers = temp;
        }
        if (bytes == null) {
            bytes = getContentIdentifier().getBytes();
        }
    }

    public byte[] asBytes() {
        return bytes;
    }

    public String getContentIdentifier() {
        return new Date(timeStamp).toString();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public IStorage getStorage(IProgressMonitor monitor) {
        return storage;
    }

    public boolean isContainer() {
        return type != IResource.FILE;
    }

    public boolean hasMembers() {
        return hasMembers;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        return path;
    }
}
