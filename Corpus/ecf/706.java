/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.remoteservices.ui;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.IEndpointDescriptionLocator;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.EndpointDiscoveryView;
import org.eclipse.ecf.remoteserviceadmin.ui.rsa.RemoteServiceAdminView;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.remoteserviceadmin.EndpointEvent;
import org.osgi.service.remoteserviceadmin.EndpointEventListener;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminEvent;
import org.osgi.service.remoteserviceadmin.RemoteServiceAdminListener;

public class DiscoveryComponent implements EndpointEventListener, RemoteServiceAdminListener {

    //$NON-NLS-1$
    private static final String RSA_SYMBOLICNAME = "org.eclipse.ecf.osgi.services.remoteserviceadmin";

    private static DiscoveryComponent instance;

    private BundleContext context;

    private RemoteServiceAdmin rsa;

    void bindRemoteServiceAdmin(RemoteServiceAdmin r) {
        rsa = r;
    }

    void unbindRemoteServiceAdmin(RemoteServiceAdmin rsa) {
        this.rsa = null;
    }

    public BundleContext getContext() {
        return this.context;
    }

    private EndpointDiscoveryView discoveryView;

    private RemoteServiceAdminView rsaView;

    private IEndpointDescriptionLocator edLocator;

    void bindEndpointDescriptionLocator(IEndpointDescriptionLocator locator) {
        this.edLocator = locator;
    }

    void unbindEndpointDescriptionLocator(IEndpointDescriptionLocator locator) {
        this.edLocator = null;
    }

    public IEndpointDescriptionLocator getEndpointDescriptionLocator() {
        return this.edLocator;
    }

    public static DiscoveryComponent getDefault() {
        return instance;
    }

    public void setView(EndpointDiscoveryView edv) {
        synchronized (this) {
            discoveryView = edv;
        }
    }

    public RemoteServiceAdmin getRSA() {
        return rsa;
    }

    void activate(BundleContext context) throws Exception {
        history = new ArrayList<EndpointEvent>();
        synchronized (this) {
            instance = this;
            this.context = context;
        }
    }

    void deactivate() {
        synchronized (this) {
            instance = null;
            discoveryView = null;
            rsa = null;
            context = null;
            if (history != null) {
                history.clear();
                history = null;
            }
        }
    }

    public void startRSA() throws BundleException {
        Bundle rsaBundle = null;
        BundleContext ctxt = null;
        synchronized (this) {
            ctxt = this.context;
            if (ctxt == null)
                return;
        }
        for (Bundle b : ctxt.getBundles()) if (b.getSymbolicName().equals(RSA_SYMBOLICNAME))
            rsaBundle = b;
        if (rsaBundle == null)
            throw new BundleException(Messages.DiscoveryComponent_ERROR_MSG_CANNOT_FIND_RSA_BUNDLE);
        rsaBundle.start();
    }

    private List<EndpointEvent> history;

    List<EndpointEvent> getHistory() {
        synchronized (this) {
            return history;
        }
    }

    @Override
    public void endpointChanged(EndpointEvent event, String filter) {
        EndpointDiscoveryView view = null;
        List<EndpointEvent> h = null;
        synchronized (this) {
            h = history;
            view = discoveryView;
        }
        if (view != null)
            view.handleEndpointChanged(event);
        else if (h != null)
            h.add(event);
    }

    @Override
    public void remoteAdminEvent(RemoteServiceAdminEvent event) {
        if (rsaView != null)
            rsaView.handleRSAEvent(event);
        if (discoveryView != null)
            discoveryView.handleRSAEent(event);
    }

    public void setRSAView(RemoteServiceAdminView rsaView) {
        this.rsaView = rsaView;
    }
}
