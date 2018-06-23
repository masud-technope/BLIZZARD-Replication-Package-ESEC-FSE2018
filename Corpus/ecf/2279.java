/*******************************************************************************
 * Copyright (c) 2010-2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.IDUtil;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.RemoteServiceContainer;
import org.osgi.framework.ServiceReference;

/**
 * Abstract superclass for host container selectors...i.e. implementers of
 * {@link IHostContainerSelector}.
 * 
 */
public abstract class AbstractHostContainerSelector extends AbstractContainerSelector {

    protected String[] defaultConfigTypes;

    public  AbstractHostContainerSelector(String[] defaultConfigTypes) {
        this.defaultConfigTypes = defaultConfigTypes;
    }

    /**
	 * @param serviceReference service reference
	 * @param overridingProperties overriding properties
	 * @param serviceExportedInterfaces service exported interfaces to select for
	 * @param serviceExportedConfigs service exported configs to select for
	 * @param serviceIntents service exported intents to select for 
	 * @return Collection of existing host containers
	 * @since 2.0
	 */
    protected Collection selectExistingHostContainers(ServiceReference serviceReference, Map<String, Object> overridingProperties, String[] serviceExportedInterfaces, String[] serviceExportedConfigs, String[] serviceIntents) {
        List results = new ArrayList();
        // Get all existing containers
        IContainer[] containers = getContainers();
        // If nothing there, then return empty array
        if (containers == null || containers.length == 0)
            return results;
        for (int i = 0; i < containers.length; i++) {
            // Check to make sure it's a rs container adapter. If it's not go
            // onto next one
            IRemoteServiceContainerAdapter adapter = hasRemoteServiceContainerAdapter(containers[i]);
            if (adapter == null)
                continue;
            // Get container type description and intents
            ContainerTypeDescription description = getContainerTypeDescription(containers[i]);
            // If it has no description go onto next
            if (description == null)
                continue;
            // http://bugs.eclipse.org/331532
            if (!description.isServer()) {
                continue;
            }
            if (matchExistingHostContainer(serviceReference, overridingProperties, containers[i], adapter, description, serviceExportedConfigs, serviceIntents)) {
                trace("selectExistingHostContainers", //$NON-NLS-1$ //$NON-NLS-2$
                "INCLUDING containerID=" + containers[i].getID() + //$NON-NLS-1$
                " configs=" + (//$NON-NLS-1$
                (serviceExportedConfigs == null) ? //$NON-NLS-1$
                "null" : Arrays.asList(serviceExportedConfigs).toString()) + //$NON-NLS-1$
                " intents=" + (//$NON-NLS-1$
                (serviceIntents == null) ? //$NON-NLS-1$
                "null" : Arrays.asList(serviceIntents).toString()));
                results.add(new RemoteServiceContainer(containers[i], adapter));
            } else {
                trace("selectExistingHostContainers", //$NON-NLS-1$ //$NON-NLS-2$
                "EXCLUDING containerID=" + containers[i].getID() + //$NON-NLS-1$
                " configs=" + (//$NON-NLS-1$
                (serviceExportedConfigs == null) ? //$NON-NLS-1$
                "null" : Arrays.asList(serviceExportedConfigs).toString()) + //$NON-NLS-1$
                " intents=" + (//$NON-NLS-1$
                (serviceIntents == null) ? //$NON-NLS-1$
                "null" : Arrays.asList(serviceIntents).toString()));
            }
        }
        return results;
    }

    /**
	 * @param serviceReference serviceReference
	 * @param properties properties
	 * @param container container to match
	 * @return boolean true if match false otherwise
	 * @since 2.0
	 */
    protected boolean matchHostContainerToConnectTarget(ServiceReference serviceReference, Map<String, Object> properties, IContainer container) {
        String target = (String) properties.get(RemoteConstants.ENDPOINT_CONNECTTARGET_ID);
        if (target == null)
            return true;
        // If a targetID is specified, make sure it either matches what the
        // container
        // is already connected to, or that we connect an unconnected container
        ID connectedID = container.getConnectedID();
        // then we connect it to the given target
        if (connectedID == null) {
            // connect to the target and we have a match
            try {
                connectHostContainer(serviceReference, properties, container, target);
            } catch (Exception e) {
                logException("doConnectContainer containerID=" + container.getID() + " target=" + target, e);
                return false;
            }
            return true;
        } else {
            ID targetID = createTargetID(container, target);
            // If it does we have a match
            if (connectedID.equals(targetID))
                return true;
        }
        return false;
    }

    /**
	 * @param serviceReference service reference
	 * @param properties properties
	 * @param container container
	 * @param adapter remote service container adapter
	 * @param description container type description
	 * @param requiredConfigTypes required config types
	 * @param requiredServiceIntents required service intents
	 * @return boolean true if match, false otherwise
	 * @since 2.0
	 */
    protected boolean matchExistingHostContainer(ServiceReference serviceReference, Map<String, Object> properties, IContainer container, IRemoteServiceContainerAdapter adapter, ContainerTypeDescription description, String[] requiredConfigTypes, String[] requiredServiceIntents) {
        return matchHostSupportedConfigTypes(requiredConfigTypes, description) && matchHostSupportedIntents(requiredServiceIntents, description) && matchHostContainerID(serviceReference, properties, container) && matchHostContainerToConnectTarget(serviceReference, properties, container);
    }

    /**
	 * @param serviceReference serviceReference
	 * @param properties properties
	 * @param container container
	 * @return boolean true if match, false otherwise
	 * @since 2.0
	 */
    protected boolean matchHostContainerID(ServiceReference serviceReference, Map<String, Object> properties, IContainer container) {
        ID containerID = container.getID();
        // No match if the container has no ID
        if (containerID == null)
            return false;
        // Then get containerid if specified directly by user in properties
        ID requiredContainerID = (ID) properties.get(RemoteConstants.SERVICE_EXPORTED_CONTAINER_ID);
        // If the CONTAINER_I
        if (requiredContainerID != null) {
            return requiredContainerID.equals(containerID);
        }
        // Else get the container factory arguments, create an ID from the
        // arguments
        // and check if the ID matches that
        Namespace ns = containerID.getNamespace();
        Object cid = properties.get(RemoteConstants.SERVICE_EXPORTED_CONTAINER_FACTORY_ARGS);
        // If no arguments are present, then any container ID should match
        if (cid == null)
            return true;
        ID cID = null;
        if (cid instanceof ID) {
            cID = (ID) cid;
        } else if (cid instanceof String) {
            cID = IDUtil.createID(ns, (String) cid);
        } else if (cid instanceof Object[]) {
            Object cido = ((Object[]) cid)[0];
            cID = IDUtil.createID(ns, new Object[] { cido });
        }
        if (cID == null)
            return true;
        return containerID.equals(cID);
    }

    /**
	 * @param requiredConfigTypes request config types
	 * @param containerTypeDescription container type description
	 * @return boolean true if match, false otherwise
	 */
    protected boolean matchHostSupportedConfigTypes(String[] requiredConfigTypes, ContainerTypeDescription containerTypeDescription) {
        // endpoint (see section 122.5.1)
        if (requiredConfigTypes == null)
            return true;
        // Get supported config types for this description
        String[] supportedConfigTypes = getSupportedConfigTypes(containerTypeDescription);
        // If it doesn't support anything, return false
        if (supportedConfigTypes == null || supportedConfigTypes.length == 0)
            return false;
        // Turn supported config types for this description into list
        List supportedConfigTypesList = Arrays.asList(supportedConfigTypes);
        List requiredConfigTypesList = Arrays.asList(requiredConfigTypes);
        // We check all of the required config types and make sure
        // that they are present in the supportedConfigTypes
        boolean result = true;
        for (Iterator i = requiredConfigTypesList.iterator(); i.hasNext(); ) result &= supportedConfigTypesList.contains(i.next());
        return result;
    }

    /**
	 * @param serviceReference service reference
	 * @param properties overriding properties
	 * @param serviceExportedInterfaces service exported interfaces to select for
	 * @param requiredConfigs service exported configs to select for
	 * @param requiredIntents intents to select for 
	 * @return Collection of host containers
	 * @throws SelectContainerException if container cannot be created or configured
	 * @since 2.0
	 */
    protected Collection createAndConfigureHostContainers(ServiceReference serviceReference, Map<String, Object> properties, String[] serviceExportedInterfaces, String[] requiredConfigs, String[] requiredIntents) throws SelectContainerException {
        List results = new ArrayList();
        ContainerTypeDescription[] descriptions = getContainerTypeDescriptions();
        if (descriptions == null)
            return Collections.EMPTY_LIST;
        // If there are no required configs specified, then create any defaults
        if (requiredConfigs == null || requiredConfigs.length == 0) {
            createAndAddDefaultContainers(serviceReference, properties, serviceExportedInterfaces, requiredIntents, descriptions, results);
        } else {
            // See if we have a match
            for (int i = 0; i < descriptions.length; i++) {
                IRemoteServiceContainer matchingContainer = createMatchingContainer(descriptions[i], serviceReference, properties, serviceExportedInterfaces, requiredConfigs, requiredIntents);
                if (matchingContainer != null)
                    results.add(matchingContainer);
            }
        }
        return results;
    }

    private void createAndAddDefaultContainers(ServiceReference serviceReference, Map<String, Object> properties, String[] serviceExportedInterfaces, String[] requiredIntents, ContainerTypeDescription[] descriptions, Collection results) throws SelectContainerException {
        ContainerTypeDescription[] ctds = getContainerTypeDescriptionsForDefaultConfigTypes(descriptions);
        if (ctds != null) {
            for (int i = 0; i < ctds.length; i++) {
                IRemoteServiceContainer matchingContainer = createMatchingContainer(ctds[i], serviceReference, properties, serviceExportedInterfaces, null, requiredIntents);
                if (matchingContainer != null)
                    results.add(matchingContainer);
            }
        }
    }

    protected ContainerTypeDescription[] getContainerTypeDescriptionsForDefaultConfigTypes(ContainerTypeDescription[] descriptions) {
        String[] defaultConfigTypes = getDefaultConfigTypes();
        if (defaultConfigTypes == null || defaultConfigTypes.length == 0)
            return null;
        List results = new ArrayList();
        for (int i = 0; i < descriptions.length; i++) {
            // For each description, get supported config types
            String[] supportedConfigTypes = descriptions[i].getSupportedConfigs();
            if (supportedConfigTypes != null && matchDefaultConfigTypes(defaultConfigTypes, supportedConfigTypes))
                results.add(descriptions[i]);
        }
        return (ContainerTypeDescription[]) results.toArray(new ContainerTypeDescription[] {});
    }

    protected boolean matchDefaultConfigTypes(String[] defaultConfigTypes, String[] supportedConfigTypes) {
        List supportedConfigTypesList = Arrays.asList(supportedConfigTypes);
        for (int i = 0; i < defaultConfigTypes.length; i++) {
            if (supportedConfigTypesList.contains(defaultConfigTypes[i]))
                return true;
        }
        return false;
    }

    protected String[] getDefaultConfigTypes() {
        return defaultConfigTypes;
    }

    /**
	 * @param containerTypeDescription containerTypeDescription
	 * @param serviceReference reference
	 * @param properties properties
	 * @param serviceExportedInterfaces exported interfaces
	 * @param requiredConfigs configs
	 * @param requiredIntents intents
	 * @return IRemoteServiceContainer matching container created
	 * @throws SelectContainerException container cannot be created or selected
	 * @since 2.0
	 */
    protected IRemoteServiceContainer createMatchingContainer(ContainerTypeDescription containerTypeDescription, ServiceReference serviceReference, Map<String, Object> properties, String[] serviceExportedInterfaces, String[] requiredConfigs, String[] requiredIntents) throws SelectContainerException {
        if (matchHostSupportedConfigTypes(requiredConfigs, containerTypeDescription) && matchHostSupportedIntents(requiredIntents, containerTypeDescription)) {
            return createRSContainer(serviceReference, properties, containerTypeDescription);
        }
        return null;
    }

    /**
	 * @param serviceReference serviceReference
	 * @param properties properties
	 * @param containerTypeDescription container type description
	 * @return IRemoteServiceContainer created remote service container
	 * @throws SelectContainerException if could not be created
	 * @since 2.0
	 */
    protected IRemoteServiceContainer createRSContainer(ServiceReference serviceReference, Map<String, Object> properties, ContainerTypeDescription containerTypeDescription) throws SelectContainerException {
        IContainer container = createContainer(serviceReference, properties, containerTypeDescription);
        IRemoteServiceContainerAdapter adapter = (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class);
        if (adapter == null)
            throw new SelectContainerException("Container does not implement IRemoteServiceContainerAdapter", null, containerTypeDescription//$NON-NLS-1$
            );
        return new RemoteServiceContainer(container, adapter);
    }

    /**
	 * @param serviceReference service reference
	 * @param properties properties
	 * @param container container
	 * @param target target
	 * @throws ContainerConnectException if container cannot be connected
	 * @throws IDCreateException thrown if ID cannot be created
	 * @since 2.0
	 */
    protected void connectHostContainer(ServiceReference serviceReference, Map<String, Object> properties, IContainer container, Object target) throws ContainerConnectException, IDCreateException {
        ID targetID = (target instanceof String) ? IDUtil.createID(container.getConnectNamespace(), (String) target) : IDUtil.createID(container.getConnectNamespace(), new Object[] { target });
        Object context = properties.get(RemoteConstants.SERVICE_EXPORTED_CONTAINER_CONNECT_CONTEXT);
        IConnectContext connectContext = null;
        if (context != null) {
            connectContext = createConnectContext(serviceReference, properties, container, context);
        }
        // connect the container
        container.connect(targetID, connectContext);
    }

    protected boolean matchHostSupportedIntents(String[] serviceRequiredIntents, ContainerTypeDescription containerTypeDescription) {
        // If there are no required intents then we have a match
        if (serviceRequiredIntents == null)
            return true;
        String[] supportedIntents = getSupportedIntents(containerTypeDescription);
        if (supportedIntents == null)
            return false;
        List supportedIntentsList = Arrays.asList(supportedIntents);
        boolean result = true;
        for (int i = 0; i < serviceRequiredIntents.length; i++) result = result && supportedIntentsList.contains(serviceRequiredIntents[i]);
        return result;
    }
}
