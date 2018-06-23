/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.filetransfer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Runnable for doing file transfer.  Used by {@link FileTransferJob}s.
 * 
 * @since 2.0
 */
public interface IFileTransferRunnable {

    /**
	 * Synchronously perform the actual file transfer.
	 * 
	 * @param monitor a progress montior.  Will not be <code>null</code>.
	 * @return IStatus a status object indicating the ending status of the file transfer job.
	 */
    public IStatus performFileTransfer(IProgressMonitor monitor);
}
