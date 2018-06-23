/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.internal.remoteservice.java8;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.provider.remoteservice.generic.RegistrySharedObject;
import org.eclipse.ecf.provider.remoteservice.generic.RemoteServiceContainerAdapterFactory;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

public class J8RemoteServiceContainerAdapterFactory extends RemoteServiceContainerAdapterFactory {

    protected ISharedObject createAdapter(ISharedObjectContainer container, @SuppressWarnings("rawtypes") Class adapterType, ID adapterID) {
        if (adapterType.equals(IRemoteServiceContainerAdapter.class)) {
            return new RegistrySharedObject() {
            };
        }
        return null;
    }
}
