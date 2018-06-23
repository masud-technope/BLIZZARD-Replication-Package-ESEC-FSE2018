/******************************************************************************* 
 * Copyright (c) 2010-2011 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Pavel Samolisov - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.internal.tests.remoteservice.rpc;

import javax.servlet.http.HttpServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

@SuppressWarnings("rawtypes")
public class HttpServiceConnector extends ServiceTracker implements ServiceTrackerCustomizer {

    private String path;

    private HttpServlet servlet;

    @SuppressWarnings("unchecked")
    public  HttpServiceConnector(BundleContext context, String path, HttpServlet servlet) {
        super(context, HttpService.class.getName(), null);
        this.path = path;
        this.servlet = servlet;
        open();
    }

    public Object addingService(ServiceReference reference) {
        @SuppressWarnings("unchecked") HttpService httpService = (HttpService) super.addingService(reference);
        if (httpService == null)
            return null;
        try {
            httpService.registerServlet(path, servlet, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpService;
    }

    @SuppressWarnings("unchecked")
    public void removedService(ServiceReference reference, Object service) {
        HttpService httpService = (HttpService) service;
        httpService.unregister(path);
        super.removedService(reference, service);
    }
}
