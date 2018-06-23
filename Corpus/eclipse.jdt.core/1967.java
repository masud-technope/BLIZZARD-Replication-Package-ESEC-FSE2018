/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search.indexing;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.compiler.SourceElementParser;
import org.eclipse.jdt.internal.core.index.Index;
import org.eclipse.jdt.internal.core.search.processing.JobManager;
import org.eclipse.jdt.internal.core.util.Util;

class AddFolderToIndex extends IndexRequest {

    IPath folderPath;

    IProject project;

    char[][] inclusionPatterns;

    char[][] exclusionPatterns;

    public  AddFolderToIndex(IPath folderPath, IProject project, char[][] inclusionPatterns, char[][] exclusionPatterns, IndexManager manager) {
        super(project.getFullPath(), manager);
        this.folderPath = folderPath;
        this.project = project;
        this.inclusionPatterns = inclusionPatterns;
        this.exclusionPatterns = exclusionPatterns;
    }

    public boolean execute(IProgressMonitor progressMonitor) {
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled())
            return true;
        // nothing to do
        if (!this.project.isAccessible())
            return true;
        IResource folder = this.project.getParent().findMember(this.folderPath);
        // nothing to do, source folder was removed
        if (folder == null || folder.getType() == IResource.FILE)
            return true;
        /* ensure no concurrent write access to index */
        Index index = this.manager.getIndex(this.containerPath, /*reuse index file*/
        true, /*create if none*/
        true);
        if (index == null)
            return true;
        ReadWriteMonitor monitor = index.monitor;
        // index got deleted since acquired
        if (monitor == null)
            return true;
        try {
            // ask permission to read
            monitor.enterRead();
            final IPath container = this.containerPath;
            final IndexManager indexManager = this.manager;
            final SourceElementParser parser = indexManager.getSourceElementParser(JavaCore.create(this.project), /*requestor will be set by indexer*/
            null);
            if (this.exclusionPatterns == null && this.inclusionPatterns == null) {
                folder.accept(new IResourceProxyVisitor() {

                    public boolean visit(/* throws CoreException */
                    IResourceProxy proxy) {
                        if (proxy.getType() == IResource.FILE) {
                            if (org.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(proxy.getName()))
                                indexManager.addSource((IFile) proxy.requestResource(), container, parser);
                            return false;
                        }
                        return true;
                    }
                }, IResource.NONE);
            } else {
                folder.accept(new IResourceProxyVisitor() {

                    public boolean visit(/* throws CoreException */
                    IResourceProxy proxy) {
                        switch(proxy.getType()) {
                            case IResource.FILE:
                                if (org.eclipse.jdt.internal.core.util.Util.isJavaLikeFileName(proxy.getName())) {
                                    IResource resource = proxy.requestResource();
                                    if (!Util.isExcluded(resource, AddFolderToIndex.this.inclusionPatterns, AddFolderToIndex.this.exclusionPatterns))
                                        indexManager.addSource((IFile) resource, container, parser);
                                }
                                return false;
                            case IResource.FOLDER:
                                if (AddFolderToIndex.this.exclusionPatterns != null && AddFolderToIndex.this.inclusionPatterns == null) {
                                    // if there are inclusion patterns then we must walk the children
                                    if (Util.isExcluded(proxy.requestFullPath(), AddFolderToIndex.this.inclusionPatterns, AddFolderToIndex.this.exclusionPatterns, true))
                                        return false;
                                }
                        }
                        return true;
                    }
                }, IResource.NONE);
            }
        } catch (CoreException e) {
            if (JobManager.VERBOSE) {
                Util.verbose("-> failed to add " + this.folderPath + " to index because of the following exception:", System.err);
                e.printStackTrace();
            }
            return false;
        } finally {
            // free read lock
            monitor.exitRead();
        }
        return true;
    }

    public String toString() {
        //$NON-NLS-1$ //$NON-NLS-2$
        return "adding " + this.folderPath + " to index " + this.containerPath;
    }
}
