/******************************************************************************* 
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.remoteservice.rest;

import java.util.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;
import org.eclipse.ecf.remoteservice.rest.client.RestClientContainer;
import org.eclipse.ecf.remoteservice.rest.identity.RestID;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;

/**
 * This class is omnly used for creating instances of {@link RestClientContainer}.
 */
public class RestClientContainerInstantiator extends BaseContainerInstantiator implements IRemoteServiceContainerInstantiator {

    //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    protected static final String[] restIntents = { "passByValue", "exactlyOnce", "ordered" };

    //$NON-NLS-1$
    private static final String REST_CLIENT_CONFIG = "ecf.rest.client";

    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        try {
            RestID restID = null;
            if (parameters != null && parameters[0] instanceof RestID)
                restID = (RestID) parameters[0];
            else
                restID = (RestID) IDFactory.getDefault().createID(RestNamespace.NAME, parameters);
            return new RestClientContainer(restID);
        } catch (Exception e) {
            throw new ContainerCreateException("Could not create RestClientContainer", e);
        }
    }

    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        return getInterfacesAndAdaptersForClass(RestClientContainer.class);
    }

    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return restIntents;
    }

    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
        RestNamespace restNamespace = (RestNamespace) IDFactory.getDefault().getNamespaceByName(RestNamespace.NAME);
        return restNamespace.getSupportedParameterTypes();
    }

    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
        if (REST_CLIENT_CONFIG.equals(description.getName())) {
            List supportedConfigs = Arrays.asList(exporterSupportedConfigs);
            if (supportedConfigs.contains(REST_CLIENT_CONFIG))
                return new String[] { REST_CLIENT_CONFIG };
        }
        return null;
    }

    public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs, Dictionary exportedProperties) {
        return null;
    }

    public String[] getSupportedConfigs(ContainerTypeDescription description) {
        return new String[] { REST_CLIENT_CONFIG };
    }
}
