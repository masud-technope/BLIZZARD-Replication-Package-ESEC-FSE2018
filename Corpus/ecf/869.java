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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.sync.ModelUpdateException;
import org.eclipse.ecf.sync.resources.core.preferences.PreferenceConstants;

final class FolderChangeMessage extends ResourceChangeMessage {

    private static final long serialVersionUID = -9163249277826579621L;

     FolderChangeMessage(String path, int kind) {
        super(path, IResource.FOLDER, kind, null);
    }

    public void applyToModel(Object model) throws ModelUpdateException {
        try {
            apply();
        } catch (CoreException e) {
            throw new ModelUpdateException(e, this, model);
        }
    }

    private void apply() throws CoreException {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(path));
        switch(kind) {
            case IResourceDelta.ADDED:
                switch(SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION)) {
                    case PreferenceConstants.COMMIT_VALUE:
                        if (folder.exists()) {
                            // the folder already exists, no need to create it, mark it
                            // as conflicting and ignored
                            setConflicted(true);
                            setIgnored(true);
                        } else {
                            folder.create(false, true, new NullProgressMonitor());
                        }
                        break;
                    case PreferenceConstants.IGNORE_VALUE:
                        setIgnored(true);
                        break;
                }
                break;
            case IResourceDelta.REMOVED:
                switch(SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_DELETION)) {
                    case PreferenceConstants.COMMIT_VALUE:
                        if (folder.exists()) {
                            folder.delete(false, true, new NullProgressMonitor());
                        } else {
                            // if it doesn't exist, then there's nothing to delete, so
                            // we flag this change as having been ignored
                            setIgnored(true);
                        }
                        break;
                    case PreferenceConstants.IGNORE_VALUE:
                        setIgnored(true);
                        break;
                }
                break;
        }
    }
}
