/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.filetransfer;

/**
 * Outgoing file transfer.
 * 
 */
public interface IOutgoingFileTransfer extends IFileTransfer {

    /**
	 * Get the number of bytes sent for this outgoing file transfer. Returns 0
	 * if transfer has not be started, and -1 if underlying provider does not
	 * support reporting number of bytes sent during transfer.
	 * 
	 * @return number of bytes sent. Returns 0 if the outgoing file transfer has
	 *         not been started, and -1 if provider does not support reporting
	 *         of number of bytes received during transfer
	 */
    public long getBytesSent();
}
