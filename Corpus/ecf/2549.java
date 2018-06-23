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
 * Retrieve file transfer factory. This service interface is used by clients to
 * create a new IRetrieveFileTransfer instance.
 */
public interface IRetrieveFileTransferFactory {

    /**
	 * Get new instance of IRetrieveFileTransfer.
	 * 
	 * @return IRetrieveFileTransfer for initiating a retrieval of a remote file.
	 */
    public IRetrieveFileTransfer newInstance();
}
