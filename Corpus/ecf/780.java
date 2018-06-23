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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.osgi.framework.ServiceReference;

/**
 * Default implementation of {@link IHostContainerSelector} service.
 * 
 */
public class HostContainerSelector extends AbstractHostContainerSelector implements IHostContainerSelector {

    private static final boolean reuseExistingContainers = new Boolean(System.getProperty("org.eclipse.ecf.osgi.services.remoteserviceadmin.HostContainerSelector.reuseExistingContainers", //$NON-NLS-1$
    "true")).booleanValue();

    private boolean autoCreateContainer = false;

    public  HostContainerSelector(String[] defaultConfigTypes, boolean autoCreateContainer) {
        super(defaultConfigTypes);
        this.autoCreateContainer = autoCreateContainer;
    }

    // Adding synchronized to make the host container finding
    // thread safe to deal with bug
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=331836
    /**
	 * @see org.eclipse.ecf.osgi.services.remoteserviceadmin.IHostContainerSelector#selectHostContainers(org.osgi.framework.ServiceReference,
	 *      java.util.Map, java.lang.String[], java.lang.String[],
	 *      java.lang.String[])
	 * @since 2.0
	 */
    public synchronized IRemoteServiceContainer[] selectHostContainers(ServiceReference serviceReference, Map<String, Object> overridingProperties, String[] serviceExportedInterfaces, String[] serviceExportedConfigs, String[] serviceIntents) throws SelectContainerException {
        trace(//$NON-NLS-1$
        "selectHostContainers", //$NON-NLS-1$ //$NON-NLS-2$
        "serviceReference=" + serviceReference + ",overridingProperties=" + overridingProperties + //$NON-NLS-1$
        ",exportedInterfaces=" + ((serviceExportedInterfaces == null) ? Collections.EMPTY_LIST : Arrays.asList(serviceExportedInterfaces)) + ",serviceExportedConfigs=" + ((serviceExportedConfigs == null) ? Collections.EMPTY_LIST : Arrays.asList(serviceExportedConfigs)) + //$NON-NLS-1$
        ",serviceIntents=" + ((serviceIntents == null) ? Collections.EMPTY_LIST : Arrays.asList(serviceIntents)));
        // Find previously created containers that match the given
        // serviceExportedConfigs and serviceIntents
        Collection rsContainers = (reuseExistingContainers) ? selectExistingHostContainers(serviceReference, overridingProperties, serviceExportedInterfaces, serviceExportedConfigs, serviceIntents) : Collections.EMPTY_LIST;
        if (rsContainers.size() == 0 && autoCreateContainer) {
            // If no existing containers are found we'll go through
            // finding/creating/configuring/connecting
            rsContainers = createAndConfigureHostContainers(serviceReference, overridingProperties, serviceExportedInterfaces, serviceExportedConfigs, serviceIntents);
            // if SERVICE_EXPORTED_CONTAINER_CONNECT_TARGET service property is
            // specified, then
            // connect the host container(s)
            Object target = overridingProperties.get(RemoteConstants.ENDPOINT_CONNECTTARGET_ID);
            if (target != null) {
                for (Iterator i = rsContainers.iterator(); i.hasNext(); ) {
                    IContainer container = ((IRemoteServiceContainer) i.next()).getContainer();
                    try {
                        connectHostContainer(serviceReference, overridingProperties, container, target);
                    } catch (Exception e) {
                        logException("doConnectContainer failure containerID=" + container.getID() + " target=" + target, e);
                    }
                }
            }
        }
        //$NON-NLS-1$ //$NON-NLS-2$
        trace("selectHostContainers", "rsContainers selected=" + rsContainers);
        // return result
        return (IRemoteServiceContainer[]) rsContainers.toArray(new IRemoteServiceContainer[rsContainers.size()]);
    }

    public void close() {
    }
}
