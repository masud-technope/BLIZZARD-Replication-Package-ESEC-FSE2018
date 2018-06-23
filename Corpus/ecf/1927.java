/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.core.provider;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerTypeDescription;

/**
 * Interface that must be implemented by ECF remote service provider implementations.
 * @since 3.1
 * 
 */
public interface IRemoteServiceContainerInstantiator {

    // Remote Service Exporter
    /**
	 * Get supported configs for the given ContainerTypeDescription.  This method
	 * will be called to determine what the OSGi remote service supported config types are for the given description during
	 * the search for the service exporter provider/containers upon remote service registration.
	 * 
	 * @param description the ContainerTypeDescription to return the supported configs for.  
	 * Will not be <code>null</code>.
	 * @return String[] the supported config types.  <code>null</code> may be returned if the 
	 * given description does not support any config types.
	 */
    public String[] getSupportedConfigs(ContainerTypeDescription description);

    /**
	 * Get supported intents for the given ContainerTypeDescription.  This method
	 * will be called to determine what the OSGi remote service supported intents are for the given description during
	 * the search for the service exporter provider/containers upon remote service registration.
	 * 
	 * @param description the ContainerTypeDescription to return the supported intents for.  
	 * Will not be <code>null</code>.
	 * @return String[] the supported intents.  <code>null</code> may be returned if the 
	 * given description does not support any intents.
	 */
    public String[] getSupportedIntents(ContainerTypeDescription description);

    // Remote Service Importer
    /**
	 * <p>Get the imported config types for a given ContainerTypeDescription for the given exporter supported config types.  This
	 * method will be called to determine what the local container imported configs are for the given description and
	 * exporterSupportedConfigTypes.  The local provider can decide which (if any) imported config types should be 
	 * returned and return them. 
	 * <p>
	 * As an example, consider the config types for the ECF generic provider.  A generic server has a config type
	 * of 'ecf.generic.server', and the client has 'ecf.generic.server'.  If the generic server exports a given
	 * service, the exportersSupportedConfigTypes will be '[ecf.generic.server]'.  When this method is called
	 * with the ecf.generic.client description (i.e. the container type description named 'ecf.generic.client'), it
	 * should respond with a non-null, non-empty array...e.g.:  [ecf.generic.client].  This indicates that the
	 * ecf.generic.client can serve as an importer for the given exporter config type.  All, other descriptions
	 * should return <code>null</code>, to indicate that they cannot import a remote service exported by the given
	 * exporterSupportedConfigTypes. 
	 * 
	 * @param description the container type description under consideration.
	 * @param exporterSupportedConfigs the exporter supported config types under consideration.
	 * @return String[] indicating the importer's supported config types.  Should be <code>null</code>, unless
	 * one or more of the exporterSupportedConfigTypes is recognized for the given description.
	 */
    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs);

    /**
	 * Get the properties associated with the given description, with the given importedConfigTypes, via the given exportedProperties.
	 * 
	 * @param description the container type description under consideration.
	 * @param importedConfigs the imported config types for the given properties.  Will not be <code>null</code>, and
	 * should be the same values as returned from {@link #getImportedConfigs(ContainerTypeDescription, String[])}.
	 * @param exportedProperties the properties from the exported service.  Will not be <code>null</code>.
	 * @return Dictionary that has all of the properties for the importedConfigTypes.  May be <code>null</code> if 
	 * no properties are associated with the given description, importedConfigTypes, exportedProperties.
	 */
    public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs, Dictionary exportedProperties);
}
