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
package org.eclipse.ecf.provider.datashare;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;

public class DatashareContainerAdapterFactory extends AbstractSharedObjectContainerAdapterFactory {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.AbstractSharedObjectContainerAdapterFactory#createAdapter(org.eclipse.ecf.core.sharedobject.ISharedObjectContainer, java.lang.Class, org.eclipse.ecf.core.identity.ID)
	 */
    protected ISharedObject createAdapter(ISharedObjectContainer container, Class adapterType, ID adapterID) {
        if (adapterType.equals(IChannelContainerAdapter.class)) {
            return new SharedObjectDatashareContainerAdapter();
        }
        return null;
    }

    public Class[] getAdapterList() {
        return new Class[] { IChannelContainerAdapter.class };
    }
}
