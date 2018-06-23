/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.distribution;

import java.util.Map;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.hooks.service.EventListenerHook;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class BasicTopologyManagerComponent implements EventListenerHook, RemoteServiceAdminListener {

    private boolean exportRegisteredSvcs = new Boolean(//$NON-NLS-1$ //$NON-NLS-2$
    System.getProperty("org.eclipse.ecf.osgi.services.basictopologymanager.exportRegisteredSvcs", "true")).booleanValue();

    private String exportRegisteredSvcsFilter = System.getProperty(//$NON-NLS-1$
    "org.eclipse.ecf.osgi.services.basictopologymanager.exportRegisteredSvcsFilter", //$NON-NLS-1$
    "(service.exported.interfaces=*)");

    private String exportRegisteredSvcsClassname = System.getProperty(//$NON-NLS-1$
    "org.eclipse.ecf.osgi.services.basictopologymanager.exportRegisteredSvcsClassname");

    private BasicTopologyManagerImpl basicTopologyManagerImpl;

    void bindEndpointEventListener(EndpointEventListener el) {
        if (el instanceof Activator.ProxyEndpointEventListener)
            basicTopologyManagerImpl = ((Activator.ProxyEndpointEventListener) el).getBasicTopologyManagerImpl();
        else if (el instanceof BasicTopologyManagerImpl)
            basicTopologyManagerImpl = (BasicTopologyManagerImpl) el;
    }

    void unbindEndpointEventListener(EndpointEventListener el) {
        basicTopologyManagerImpl = null;
    }

    void activate() {
        if (basicTopologyManagerImpl == null)
            return;
        if (exportRegisteredSvcs)
            basicTopologyManagerImpl.exportRegisteredServices(exportRegisteredSvcsClassname, exportRegisteredSvcsFilter);
    }

    // RemoteServiceAdminListener impl
    public void remoteAdminEvent(RemoteServiceAdminEvent event) {
        if (basicTopologyManagerImpl == null)
            return;
        basicTopologyManagerImpl.handleRemoteAdminEvent(event);
    }

    // EventListenerHook impl
    public void event(ServiceEvent event, Map listeners) {
        if (basicTopologyManagerImpl == null)
            return;
        basicTopologyManagerImpl.event(event, listeners);
    }
}
