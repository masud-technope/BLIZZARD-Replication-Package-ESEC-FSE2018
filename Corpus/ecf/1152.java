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

import java.io.ByteArrayInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ecf.sync.ModelUpdateException;
import org.eclipse.ecf.sync.resources.core.preferences.PreferenceConstants;

public final class FileChangeMessage extends ResourceChangeMessage {

    private static final long serialVersionUID = 1482601202361201103L;

     FileChangeMessage(String path, int kind, byte[] contents) {
        super(path, IResource.FILE, kind, contents);
    }

    public void applyToModel(Object model) throws ModelUpdateException {
        try {
            apply();
        } catch (CoreException e) {
            throw new ModelUpdateException(e, this, model);
        }
    }

    private void apply() throws CoreException {
        IPath resourcePath = new Path(path);
        final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(resourcePath);
        switch(kind) {
            case IResourceDelta.ADDED:
                switch(SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION)) {
                    case PreferenceConstants.COMMIT_VALUE:
                        if (file.exists()) {
                            // file was added by a remote peer when it exists locally,
                            // this is a conflict, flag it as such
                            setConflicted(true);
                            // change the contents
                            file.setContents(new ByteArrayInputStream(contents), true, true, null);
                        } else {
                            // file doesn't exist, create it
                            file.create(new ByteArrayInputStream(contents), true, null);
                        }
                        break;
                    case PreferenceConstants.IGNORE_CONFLICTS_VALUE:
                        if (file.exists()) {
                            // the file exists, this is a conflict, flag it
                            setConflicted(true);
                            // the user has set the system to ignore conflicts, flag it
                            setIgnored(true);
                        } else {
                            // file doesn't exist, create it
                            file.create(new ByteArrayInputStream(contents), true, new NullProgressMonitor());
                        }
                        break;
                    case PreferenceConstants.IGNORE_VALUE:
                        setIgnored(true);
                        break;
                }
                break;
            case IResourceDelta.CHANGED:
                switch(SyncResourcesCore.getInt(PreferenceConstants.REMOTE_RESOURCE_ADDITION)) {
                    case PreferenceConstants.COMMIT_VALUE:
                        if (file.exists()) {
                            file.setContents(new ByteArrayInputStream(contents), true, true, null);
                        } else {
                            setConflicted(true);
                            file.create(new ByteArrayInputStream(contents), true, null);
                        }
                        break;
                    case PreferenceConstants.IGNORE_CONFLICTS_VALUE:
                        if (file.exists()) {
                            file.setContents(new ByteArrayInputStream(contents), true, true, null);
                        } else {
                            setConflicted(true);
                            setIgnored(true);
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
                        if (file.exists()) {
                            // the file exists, commit the change by deleting it
                            file.delete(false, true, new NullProgressMonitor());
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
        }
    }
}
