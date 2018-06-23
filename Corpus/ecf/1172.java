/*******************************************************************************
 * Copyright (c) 2013 Markus Alexander Kuppe and others. All rights reserved. 
 * This program and the accompanying materials are made available under the terms 
 * of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.discovery;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.*;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * An IServiceInfoServiceListener is a whiteboard pattern listener responsible
 * to handle IServiceInfos registered in the OSGi service registry.
 */
public class IServiceInfoServiceListener {

    private final ServiceTracker serviceTracker;

    void logException(String message, Throwable t) {
        DiscoveryPlugin.getDefault().log(new Status(IStatus.ERROR, DiscoveryPlugin.PLUGIN_ID, message, t));
    }

    public  IServiceInfoServiceListener(final IDiscoveryAdvertiser advertiser) {
        final BundleContext bundleContext = DiscoveryPlugin.getDefault().getBundleContext();
        serviceTracker = new ServiceTracker(bundleContext, IServiceInfo.class, new ServiceTrackerCustomizer() {

            public Object addingService(ServiceReference reference) {
                final IServiceInfo serviceInfo = (IServiceInfo) bundleContext.getService(reference);
                try {
                    advertiser.registerService(convertToProviderSpecific(advertiser, serviceInfo));
                } catch (Exception e) {
                    logException("Advertiser.registerService failed", e);
                }
                return serviceInfo;
            }

            public void modifiedService(ServiceReference reference, Object service) {
                // unregisterService first
                try {
                    advertiser.registerService(convertToProviderSpecific(advertiser, (IServiceInfo) service));
                } catch (Exception e) {
                    logException("Advertiser.modifiedService failed", e);
                }
            }

            public void removedService(ServiceReference reference, Object service) {
                try {
                    advertiser.unregisterService(convertToProviderSpecific(advertiser, (IServiceInfo) service));
                } catch (Exception e) {
                    logException("Advertiser.removedService failed", e);
                }
            }
        });
        serviceTracker.open();
    }

    /**
	 * Converts the generic (not discovery provider specific WRT
	 * IServiceID/IServiceTypeID) IServiceInfo into a discovery provider
	 * specific one. This is required so that discovery providers can correctly
	 * advertise services.
	 */
    private IServiceInfo convertToProviderSpecific(final IDiscoveryAdvertiser advertiser, final IServiceInfo genericInfo) {
        // Convert similar to
        // org.eclipse.ecf.provider.discovery.CompositeDiscoveryContainer.getServiceIDForDiscoveryContainer(IServiceID,
        // IDiscoveryLocator)
        final Namespace servicesNamespace = advertiser.getServicesNamespace();
        final IServiceID genericServiceID = genericInfo.getServiceID();
        final ServiceID specificServiceID = (ServiceID) servicesNamespace.createInstance(new Object[] { genericServiceID.getServiceTypeID().getName(), genericServiceID.getLocation() });
        final IServiceTypeID serviceTypeID = specificServiceID.getServiceTypeID();
        return new ServiceInfo(genericServiceID.getLocation(), genericInfo.getServiceName(), serviceTypeID, genericInfo.getPriority(), genericInfo.getWeight(), genericInfo.getServiceProperties(), genericInfo.getTTL());
    }

    public void dispose() {
        serviceTracker.close();
    }
}
