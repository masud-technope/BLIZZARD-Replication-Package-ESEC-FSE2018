/*******************************************************************************
* Copyright (c) 2009 IBM, and others. 
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   IBM Corporation - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.filetransfer.events.socket;

import org.eclipse.core.runtime.IAdaptable;

// IFileTransfer or IRemoteFileSystemRequest, other?
public interface ISocketEventSource extends IAdaptable {

    void addListener(ISocketListener listener);

    void removeListener(ISocketListener listener);

    void fireEvent(ISocketEvent event);
}
