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
 * Remote file representation.
 */
public interface IRemoteFile extends IAdaptable {

    /**
	 * Get remote file info associated with this remote file. 
	 * @return file info.  Will not be <code>null</code>.
	 */
    public IRemoteFileInfo getInfo();

    /**
	 * Get the remote file id associated with this file.
	 * @return the file id associated with this file.  Will not be
	 * <code>null</code>.
	 */
    public IFileID getID();
}
