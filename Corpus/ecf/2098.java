/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.filetransfer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ecf.filetransfer.identity.IFileID;

/**
 * Remote file request.
 */
public interface IRemoteFileSystemRequest extends IAdaptable {

    /**
	 * Cancel this request.
	 */
    public void cancel();

    /**
	 * Get the listener associated with this request
	 * @return IRemoteFileSystemListener associated with this request.
	 */
    public IRemoteFileSystemListener getRemoteFileListener();

    /**
	 * Get directoryID that represents the directory accessed.
	 * @return IFileID for remote directory or file.  Will not return <code>null</code>.
	 */
    public IFileID getFileID();
}
