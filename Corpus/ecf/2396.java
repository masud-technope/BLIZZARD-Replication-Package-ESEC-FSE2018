/*******************************************************************************
* Copyright (c) 2010 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.tests.httpservice.util;

import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class HttpServiceTracker extends ServiceTracker {

    public  HttpServiceTracker(BundleContext context, ServiceTrackerCustomizer customizer) {
        super(context, HttpService.class.getName(), customizer);
    }

    public  HttpServiceTracker(BundleContext context) {
        this(context, null);
    }

    public HttpService getHttpService() {
        return (HttpService) getService();
    }

    public HttpService[] getHttpServices() {
        return (HttpService[]) getServices();
    }
}
