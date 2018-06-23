/*******************************************************************************
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.filetransfer.jreprovider;

import org.eclipse.ecf.internal.tests.filetransfer.jreprovider.Activator;
import org.eclipse.ecf.provider.filetransfer.IFileTransferProtocolToFactoryMapper;

public class HttpFactoryRemover {

    public static IFileTransferProtocolToFactoryMapper getProtocolToFactoryMapper() {
        return Activator.getDefault().getProtocolToFactoryMapper();
    }

    public static boolean removeRetrieveProvider() {
        IFileTransferProtocolToFactoryMapper mapper = getProtocolToFactoryMapper();
        String existingProviderId = mapper.getRetrieveFileTransferFactoryId("http");
        if (existingProviderId == null) {
            // Just warn
            System.out.println("WARNING: No non-null retrieve provider found for protocol http...no remove will occur");
            return true;
        }
        return mapper.removeRetrieveFileTransferFactory(existingProviderId);
    }

    public static boolean removeBrowseProvider() {
        IFileTransferProtocolToFactoryMapper mapper = getProtocolToFactoryMapper();
        String existingProviderId = mapper.getBrowseFileTransferFactoryId("http");
        if (existingProviderId == null) {
            // Just warn
            System.out.println("WARNING: No non-null browse provider found for protocol http...no remove will occur");
            return true;
        }
        return mapper.removeBrowseFileTransferFactory(existingProviderId);
    }

    public static boolean removeSendProvider() {
        IFileTransferProtocolToFactoryMapper mapper = getProtocolToFactoryMapper();
        String existingProviderId = mapper.getSendFileTransferFactoryId("http");
        if (existingProviderId == null) {
            // Just warn
            System.out.println("WARNING: No non-null send provider found for protocol http...no remove will occur");
            return true;
        }
        return mapper.removeSendFileTransferFactory(existingProviderId);
    }
}
