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
 * Send file transfer factory. This service interface is used by clients to
 * create a new ISendFileTransfer instance, used to send file to remote
 * clients.
 */
public interface ISendFileTransferFactory {

    /**
	 * Get new instance of ISendFileTransfer.
	 * 
	 * @return ISendFileTransfer for initiating send of a local file.
	 */
    public ISendFileTransfer newInstance();
}
