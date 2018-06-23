/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.discovery;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.*;
import org.osgi.framework.*;

public class DiscoveryServiceListener implements ServiceListener {

    private final AbstractDiscoveryContainerAdapter discoveryContainer;

    private final Class listenerClass;

    private final BundleContext context;

    private final IServiceIDFactory idFactory;

    private final Namespace discoveryNamespace;

    public  DiscoveryServiceListener(AbstractDiscoveryContainerAdapter anAbstractDiscoveryContainerAdapter, Class clazz) {
        discoveryContainer = anAbstractDiscoveryContainerAdapter;
        listenerClass = clazz;
        discoveryNamespace = IDFactory.getDefault().getNamespaceByName(DiscoveryNamespace.NAME);
        idFactory = ServiceIDFactory.getDefault();
        context = DiscoveryPlugin.getDefault().getBundleContext();
        try {
            // get existing listener
            final ServiceReference[] references = context.getServiceReferences(listenerClass.getName(), null);
            addServiceListener(references);
            // listen for more listeners
            context.addServiceListener(this, getFilter());
        } catch (InvalidSyntaxException e) {
            DiscoveryPlugin.getDefault().log(new Status(IStatus.ERROR, DiscoveryPlugin.PLUGIN_ID, IStatus.ERROR, "Cannot create filter", e));
        }
    }

    public void dispose() {
        if (!DiscoveryPlugin.isStopped()) {
            context.removeServiceListener(this);
        }
    }

    private void addServiceListener(ServiceReference[] references) {
        if (references == null) {
            return;
        }
        for (int i = 0; i < references.length; i++) {
            final ServiceReference serviceReference = references[i];
            if (listenerClass.getName().equals(IServiceListener.class.getName())) {
                if (isAllWildcards(serviceReference)) {
                    final IServiceListener aListener = (IServiceListener) context.getService(serviceReference);
                    discoveryContainer.addServiceListener(aListener);
                } else {
                    final IServiceTypeID aType = getIServiceTypeID(serviceReference);
                    if (aType == null) {
                        continue;
                    }
                    final IServiceListener aListener = (IServiceListener) context.getService(serviceReference);
                    discoveryContainer.addServiceListener(aType, aListener);
                }
            } else {
                final IServiceTypeListener aListener = (IServiceTypeListener) context.getService(serviceReference);
                discoveryContainer.addServiceTypeListener(aListener);
            }
        }
    }

    private void addServiceListener(ServiceReference reference) {
        addServiceListener(new ServiceReference[] { reference });
    }

    private void removeServiceListener(ServiceReference[] references) {
        if (references == null) {
            return;
        }
        for (int i = 0; i < references.length; i++) {
            final ServiceReference serviceReference = references[i];
            if (listenerClass.getName().equals(IServiceListener.class.getName())) {
                if (isAllWildcards(serviceReference)) {
                    final IServiceListener aListener = (IServiceListener) context.getService(serviceReference);
                    discoveryContainer.removeServiceListener(aListener);
                } else {
                    final IServiceTypeID aType = getIServiceTypeID(serviceReference);
                    if (aType == null) {
                        continue;
                    }
                    final IServiceListener aListener = (IServiceListener) context.getService(serviceReference);
                    discoveryContainer.removeServiceListener(aType, aListener);
                }
            } else {
                final IServiceTypeListener aListener = (IServiceTypeListener) context.getService(serviceReference);
                discoveryContainer.removeServiceTypeListener(aListener);
            }
        }
    }

    private void removeServiceListener(ServiceReference reference) {
        removeServiceListener(new ServiceReference[] { reference });
    }

    private boolean isAllWildcards(ServiceReference serviceReference) {
        return serviceReference.getProperty("org.eclipse.ecf.discovery.namingauthority") == null && serviceReference.getProperty("org.eclipse.ecf.discovery.services") == null && serviceReference.getProperty("org.eclipse.ecf.discovery.scopes") == null && serviceReference.getProperty("org.eclipse.ecf.discovery.protocols") == null;
    }

    private IServiceTypeID getIServiceTypeID(ServiceReference serviceReference) {
        String namingAuthority = (String) serviceReference.getProperty(//$NON-NLS-1$
        "org.eclipse.ecf.discovery.namingauthority");
        if (namingAuthority == null) {
            //$NON-NLS-1$
            namingAuthority = "*";
        }
        try {
            final IServiceTypeID createServiceTypeID = idFactory.createServiceTypeID(discoveryNamespace, convert(serviceReference, "org.eclipse.ecf.discovery.services"), convert(serviceReference, "org.eclipse.ecf.discovery.scopes"), convert(serviceReference, "org.eclipse.ecf.discovery.protocols"), namingAuthority);
            return createServiceTypeID;
        } catch (final IDCreateException e) {
            return null;
        }
    }

    private String[] convert(ServiceReference serviceReference, String key) {
        final Object value = serviceReference.getProperty(key);
        // default to wildcard for non-set values
        if (value == null) {
            //$NON-NLS-1$
            return new String[] { "*" };
        } else if (value instanceof String[]) {
            return (String[]) value;
        }
        //$NON-NLS-1$
        return StringUtils.split((String) value, "._");
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.
	 * ServiceEvent)
	 */
    public void serviceChanged(ServiceEvent event) {
        // ignore events that are targeted at different discovery containers
        final Object containerName = event.getServiceReference().getProperty(IDiscoveryLocator.CONTAINER_NAME);
        if (!discoveryContainer.getContainerName().equals(containerName)) {
            return;
        }
        switch(event.getType()) {
            case ServiceEvent.REGISTERED:
                addServiceListener(event.getServiceReference());
                break;
            case ServiceEvent.UNREGISTERING:
                removeServiceListener(event.getServiceReference());
                break;
            default:
                break;
        }
    }

    private String getFilter() {
        return //$NON-NLS-1$ //$NON-NLS-2$
        "(" + Constants.OBJECTCLASS + "=" + listenerClass.getName() + //$NON-NLS-1$
        ")";
    }
}
