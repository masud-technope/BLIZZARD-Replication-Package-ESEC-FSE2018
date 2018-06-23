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
package org.eclipse.ecf.core.provider;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerTypeDescription;

/**
 *  Default implementation of {@link IRemoteServiceContainerInstantiator}.  ECF provider implementers
 *  may subclass as desired.
 * @since 3.1
 */
public class BaseRemoteServiceContainerInstantiator extends BaseContainerInstantiator implements IRemoteServiceContainerInstantiator {

    public String[] getSupportedConfigs(ContainerTypeDescription description) {
        return new String[] { description.getName() };
    }

    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
        return new String[] { description.getName() };
    }

    public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigTypes, Dictionary exportedProperties) {
        return null;
    }
}
