/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.servlet;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;

public abstract class ServletServerContainerInstantiator extends BaseContainerInstantiator implements IRemoteServiceContainerInstantiator {

    protected static final String[] intents = { "passByValue", "exactlyOnce", "ordered" };

    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
        return null;
    }

    @Override
    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return intents;
    }

    @SuppressWarnings("rawtypes")
    public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs, Dictionary exportedProperties) {
        return null;
    }
}
