/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.soap.client;

import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.*;
import org.eclipse.ecf.remoteservice.soap.identity.SoapID;
import org.eclipse.ecf.remoteservice.soap.identity.SoapNamespace;

public abstract class AbstractSoapClientContainer extends AbstractClientContainer {

    public  AbstractSoapClientContainer(SoapID containerID) {
        super(containerID);
    }

    protected abstract IRemoteService createRemoteService(RemoteServiceClientRegistration registration);

    protected String prepareEndpointAddress(IRemoteCall call, IRemoteCallable callable) {
        String resourcePath = callable.getResourcePath();
        if (//$NON-NLS-1$
        resourcePath == null || "".equals(resourcePath))
            return null;
        // if resourcePath startswith http then we use it unmodified
        if (//$NON-NLS-1$
        resourcePath.startsWith("http://"))
            return resourcePath;
        SoapID targetContainerID = (SoapID) getRemoteCallTargetID();
        String baseUriString = targetContainerID.toURI().toString();
        int length = baseUriString.length();
        char[] lastChar = new char[1];
        baseUriString.getChars(length - 1, length, lastChar, 0);
        char[] firstMethodChar = new char[1];
        resourcePath.getChars(0, 1, firstMethodChar, 0);
        if ((lastChar[0] == '/' && firstMethodChar[0] != '/') || (lastChar[0] != '/' && firstMethodChar[0] == '/'))
            return baseUriString + resourcePath;
        else if (lastChar[0] == '/' && firstMethodChar[0] == '/') {
            String tempurl = baseUriString.substring(0, length - 1);
            return tempurl + resourcePath;
        } else if (lastChar[0] != '/' && firstMethodChar[0] != '/')
            //$NON-NLS-1$
            return baseUriString + "/" + resourcePath;
        return null;
    }

    public Namespace getConnectNamespace() {
        return IDFactory.getDefault().getNamespaceByName(SoapNamespace.NAME);
    }
}
