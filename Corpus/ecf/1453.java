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
package org.eclipse.ecf.example.collab.share.io;

import java.io.File;
import java.io.Serializable;

public interface FileTransferListener extends Serializable {

    public void sendStart(FileTransferSharedObject obj, long length, float rate);

    public void sendData(FileTransferSharedObject obj, int dataLength);

    public void sendDone(FileTransferSharedObject obj, Exception e);

    public void receiveStart(FileTransferSharedObject obj, File aFile, long length, float rate);

    public void receiveData(FileTransferSharedObject obj, int dataLength);

    public void receiveDone(FileTransferSharedObject obj, Exception e);
}
