/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.provider;

import java.util.*;
import org.eclipse.ecf.core.*;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;

/**
 * @since 8.7
 */
public abstract class RemoteServiceContainerInstantiator extends BaseContainerInstantiator implements IRemoteServiceContainerInstantiator {

    protected static final String[] defaultSupportedAdapterTypes = new String[] { IContainer.class.getName(), IRemoteServiceContainerAdapter.class.getName() };

    protected static final Class[][] defaultSupportedParameterTypes = new Class[][] { { Map.class } };

    //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    protected static final String[] defaultSupportedIntents = new String[] { "passByValue", "exactlyOnce", "ordered" };

    public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
        return defaultSupportedAdapterTypes;
    }

    public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
        return defaultSupportedParameterTypes;
    }

    public String[] getSupportedIntents(ContainerTypeDescription description) {
        return defaultSupportedIntents;
    }

    protected List<String> exporterConfigs;

    protected Map<String, List<String>> exporterConfigToImporterConfigs;

    /**
	 * @param exportingProvider exporting provider (e.g. server or service host)
	 * @param importingProvider importing provider (e.g. client or service client)
	 * @since 8.9
	 */
    protected  RemoteServiceContainerInstantiator(String exportingProvider, String importingProvider) {
        this();
        this.exporterConfigs.add(exportingProvider);
        this.exporterConfigToImporterConfigs.put(exportingProvider, Arrays.asList(new String[] { importingProvider }));
    }

    protected  RemoteServiceContainerInstantiator(List<String> exporterConfigs, Map<String, List<String>> exporterConfigToImporterConfig) {
        this();
        this.exporterConfigs.addAll(exporterConfigs);
        this.exporterConfigToImporterConfigs.putAll(exporterConfigToImporterConfig);
    }

    protected  RemoteServiceContainerInstantiator() {
        this.exporterConfigs = new ArrayList<String>();
        this.exporterConfigToImporterConfigs = new HashMap<String, List<String>>();
    }

    public String[] getSupportedConfigs(ContainerTypeDescription description) {
        List<String> results = new ArrayList<String>();
        String descriptionName = description.getName();
        if (this.exporterConfigs.contains(descriptionName))
            results.add(descriptionName);
        return results.toArray(new String[results.size()]);
    }

    public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
        if (exporterSupportedConfigs == null)
            return null;
        List<String> results = new ArrayList<String>();
        for (String exporterConfig : exporterSupportedConfigs) {
            List<String> importerConfigs = exporterConfigToImporterConfigs.get(exporterConfig);
            if (importerConfigs != null)
                for (String importerConfig : importerConfigs) if (description.getName().equals(importerConfig))
                    results.add(importerConfig);
        }
        return results.toArray(new String[results.size()]);
    }

    public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs, Dictionary exportedProperties) {
        return null;
    }

    public abstract IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters) throws ContainerCreateException;

    public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) throws ContainerCreateException {
        return createInstance(description, getMap(parameters));
    }

    protected IContainer throwCreateException(String message, Throwable cause) throws ContainerCreateException {
        ContainerCreateException cce = new ContainerCreateException(message, cause);
        cce.setStackTrace(cause.getStackTrace());
        throw cce;
    }
}
