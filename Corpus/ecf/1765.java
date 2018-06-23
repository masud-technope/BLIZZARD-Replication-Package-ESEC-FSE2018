/******************************************************************************
 * Copyright (c) 2008 Versant Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen (Versant Corporation) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.example.collab.presence;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.example.collab.share.EclipseCollabSharedObject;
import org.eclipse.ecf.presence.IPresenceContainerAdapter;
import org.eclipse.ecf.provider.generic.TCPClientSOContainer;

public class PresenceContainerAdapterFactory implements IAdapterFactory {

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType.equals(IPresenceContainerAdapter.class)) {
            TCPClientSOContainer container = (TCPClientSOContainer) adaptableObject;
            ISharedObjectManager manager = container.getSharedObjectManager();
            ID[] ids = manager.getSharedObjectIDs();
            for (int i = 0; i < ids.length; i++) {
                ISharedObject object = manager.getSharedObject(ids[i]);
                if (object instanceof EclipseCollabSharedObject) {
                    EclipseCollabSharedObject ecso = (EclipseCollabSharedObject) object;
                    return ecso.getPresenceContainer();
                }
            }
        }
        return null;
    }

    public Class[] getAdapterList() {
        return new Class[] { IPresenceContainerAdapter.class };
    }
}
