/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.filetransfer.events;

import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.IOutgoingFileTransfer;

/**
 * Event sent to {@link IFileTransferListener} associated with
 * {@link IOutgoingFileTransfer} instances when some data are received
 * 
 */
public interface IOutgoingFileTransferSendDataEvent extends IOutgoingFileTransferEvent {
    // no methods for interface
}
