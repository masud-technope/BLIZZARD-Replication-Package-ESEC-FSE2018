/*******************************************************************************
* Copyright (c) 2013 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public abstract class HttpServiceComponent {

    private static final String SLASH = "/";

    private Collection<HttpService> httpServices;

    private BundleContext context;

    private static HttpServiceComponent instance;

    public static HttpServiceComponent getDefault() {
        return instance;
    }

    public  HttpServiceComponent() {
        this.httpServices = new ArrayList<HttpService>();
    }

    public Collection<HttpService> getHttpServices() {
        return httpServices;
    }

    protected void bindHttpService(HttpService httpService) {
        if (httpService != null)
            synchronized (httpServices) {
                httpServices.add(httpService);
            }
    }

    protected void unbindHttpService(HttpService httpService) {
        if (httpService != null)
            synchronized (httpServices) {
                httpServices.remove(httpService);
            }
    }

    public BundleContext getContext() {
        return context;
    }

    protected void activate(BundleContext ctxt) throws Exception {
        this.context = ctxt;
        instance = this;
    }

    protected void deactivate() throws Exception {
        httpServices.clear();
        this.context = null;
        instance = null;
    }

    @SuppressWarnings("rawtypes")
    public void registerServlet(Class service, Servlet servlet, Dictionary dictionary, HttpContext httpContext) throws ServletException, NamespaceException {
        registerServlet(SLASH + service.getName(), servlet, dictionary, httpContext);
    }

    public void registerServlet(String path, Servlet servlet, @SuppressWarnings("rawtypes") Dictionary dictionary, HttpContext httpContext) throws ServletException, NamespaceException {
        synchronized (httpServices) {
            for (HttpService httpService : httpServices) httpService.registerServlet(path, servlet, dictionary, httpContext);
        }
    }

    public void unregisterServlet(String path) {
        synchronized (httpServices) {
            for (HttpService httpService : httpServices) {
                httpService.unregister(path);
            }
        }
    }

    public void unregisterServlet(@SuppressWarnings("rawtypes") Class service) {
        unregisterServlet(SLASH + service.getName());
    }

    public <T> T getService(Class<T> service) {
        if (context == null)
            return null;
        ServiceReference<T> ref = context.getServiceReference(service);
        if (ref == null)
            return null;
        return context.getService(ref);
    }
}
