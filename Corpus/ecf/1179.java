/****************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.filetransfer.browse;

import org.eclipse.ecf.core.AbstractContainerAdapterFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.filetransfer.IRemoteFileSystemBrowserContainerAdapter;

/**
 * Adapter factory for handling multiple protocols.
 */
public class MultiProtocolFileSystemBrowserAdapterFactory extends AbstractContainerAdapterFactory {

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.sharedobject.AbstractSharedObjectContainerAdapterFactory#getAdapterList()
	 */
    public Class[] getAdapterList() {
        return new Class[] { IRemoteFileSystemBrowserContainerAdapter.class };
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.AbstractContainerAdapterFactory#getContainerAdapter(org.eclipse.ecf.core.IContainer,
	 *      java.lang.Class)
	 */
    protected Object getContainerAdapter(IContainer container, Class adapterType) {
        if (adapterType.equals(IRemoteFileSystemBrowserContainerAdapter.class)) {
            return new MultiProtocolFileSystemBrowserAdapter();
        }
        return null;
    }
}
