/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search.indexing;

import java.io.IOException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.core.index.Index;
import org.eclipse.jdt.internal.core.search.processing.JobManager;
import org.eclipse.jdt.internal.core.util.Util;

/*
 * Save the index of a project.
 */
public class SaveIndex extends IndexRequest {

    public  SaveIndex(IPath containerPath, IndexManager manager) {
        super(containerPath, manager);
    }

    public boolean execute(IProgressMonitor progressMonitor) {
        if (this.isCancelled || progressMonitor != null && progressMonitor.isCanceled())
            return true;
        /* ensure no concurrent write access to index */
        Index index = this.manager.getIndex(this.containerPath, /*reuse index file*/
        true, /*don't create if none*/
        false);
        if (index == null)
            return true;
        ReadWriteMonitor monitor = index.monitor;
        // index got deleted since acquired
        if (monitor == null)
            return true;
        try {
            // ask permission to write
            monitor.enterWrite();
            this.manager.saveIndex(index);
        } catch (IOException e) {
            if (JobManager.VERBOSE) {
                Util.verbose("-> failed to save index " + this.containerPath + " because of the following exception:", System.err);
                e.printStackTrace();
            }
            return false;
        } finally {
            // free write lock
            monitor.exitWrite();
        }
        return true;
    }

    public String toString() {
        //$NON-NLS-1$
        return "saving index for " + this.containerPath;
    }
}
