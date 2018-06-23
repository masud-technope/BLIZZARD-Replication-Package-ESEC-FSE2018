/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.filetransfer.service;

/**
 * Remote file browser factory. This service interface is used by clients to
 * create a new IRemoteFileSystemBrowser instance.
 */
public interface IRemoteFileSystemBrowserFactory {

    /**
	 * Get new instance of IRemoteFileSystemBrowser.
	 * 
	 * @return IRemoteFileSystemBrowser for initiating a retrieval of a remote file.
	 */
    public IRemoteFileSystemBrowser newInstance();
}
