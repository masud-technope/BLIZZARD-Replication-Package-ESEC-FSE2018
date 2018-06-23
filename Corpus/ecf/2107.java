/**
 * Copyright (c) 2006 Ecliptical Software Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ecliptical Software Inc. - initial API and implementation
 */
package org.eclipse.ecf.pubsub.impl;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.sharedobject.ISharedObjectManager;
import org.eclipse.ecf.core.sharedobject.SharedObjectCreateException;
import org.eclipse.ecf.core.sharedobject.SharedObjectDescription;
import org.eclipse.ecf.pubsub.IPublishedServiceDirectory;
import org.eclipse.ecf.pubsub.IPublishedServiceRequestor;

public class PubSubAdapterFactory implements IAdapterFactory {

    private static final Class[] ADAPTERS = { IPublishedServiceDirectory.class, IPublishedServiceRequestor.class };

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (!(adaptableObject instanceof ISharedObjectContainer))
            return null;
        if (IPublishedServiceDirectory.class.isAssignableFrom(adapterType))
            return getDirectory((ISharedObjectContainer) adaptableObject);
        if (IPublishedServiceRequestor.class.isAssignableFrom(adapterType))
            return getRequestor((ISharedObjectContainer) adaptableObject);
        return null;
    }

    protected IPublishedServiceDirectory getDirectory(ISharedObjectContainer container) {
        ID directoryID;
        try {
            directoryID = IDFactory.getDefault().createStringID(PublishedServiceDirectory.SHARED_OBJECT_ID);
        } catch (IDCreateException e) {
            throw new RuntimeException(e);
        }
        final ISharedObjectManager mgr = container.getSharedObjectManager();
        IPublishedServiceDirectory directory = (IPublishedServiceDirectory) mgr.getSharedObject(directoryID);
        if (directory != null)
            return directory;
        try {
            SharedObjectDescription desc = createDirectoryDescription(directoryID);
            mgr.createSharedObject(desc);
            return (IPublishedServiceDirectory) mgr.getSharedObject(directoryID);
        } catch (SharedObjectCreateException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected SharedObjectDescription createDirectoryDescription(ID directoryID) {
        return new SharedObjectDescription(PublishedServiceDirectory.class, directoryID, null);
    }

    protected IPublishedServiceRequestor getRequestor(ISharedObjectContainer container) {
        return new ServiceRequestor(container.getSharedObjectManager());
    }

    public Class[] getAdapterList() {
        return ADAPTERS;
    }
}
