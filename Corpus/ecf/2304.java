/****************************************************************************
 * Copyright (c) 2008 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.examples.updatesite;

import java.net.*;
import java.security.InvalidParameterException;
import java.util.Map;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.http.HttpService;

/**
 *
 */
public class DiscoverableServer implements IApplication {

    //$NON-NLS-1$
    private static final String PROTO = "http";

    //$NON-NLS-1$
    static final String DEFAULT_UPDATE_SITE_SERVICE_TYPE = "updatesite";

    private String username;

    protected String serviceType;

    private String serviceName;

    private String servicePath;

    private String updateSiteName;

    private URL updateSiteLocation;

    private IDiscoveryAdvertiser discovery;

    private IServiceInfo serviceInfo;

    private boolean done = false;

    public  DiscoverableServer() {
    // nothing to do
    }

    /* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
    public Object start(IApplicationContext ctxt) throws Exception {
        Map args = ctxt.getArguments();
        //$NON-NLS-1$
        initializeFromArguments((String[]) args.get("application.args"));
        // Load and start ECF core bundles (to execute ecfstart jobs like discovery providers)
        ContainerFactory.getDefault().getDescriptions();
        // get discovery service
        discovery = Activator.getDefault().waitForDiscoveryService(5000);
        // Create service id
        IServiceTypeID serviceTypeID = ServiceIDFactory.getDefault().createServiceTypeID(discovery.getServicesNamespace(), new String[] { PROTO }, IServiceTypeID.DEFAULT_PROTO);
        // create service info
        //$NON-NLS-1$ //$NON-NLS-2$
        URI uri = URI.create(PROTO + "://" + InetAddress.getLocalHost().getHostAddress() + ":" + getServicePort() + servicePath);
        serviceInfo = new ServiceInfo(uri, serviceName, serviceTypeID, 0, 0, new ServiceProperties(new UpdateSiteProperties(serviceName).toProperties()));
        // get http service
        final HttpService httpService = Activator.getDefault().waitForHttpService(2000);
        // start http service
        //$NON-NLS-1$
        httpService.registerResources(servicePath, "/", new UpdateSiteContext(httpService.createDefaultHttpContext(), updateSiteLocation));
        //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("http server\n\tupdateSiteLocation=" + updateSiteLocation + "\n\turl=" + serviceInfo.getServiceID().getLocation());
        // setup discovery
        discovery.registerService(serviceInfo);
        //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("discovery publish\n\tserviceName=" + serviceName + "\n\tserviceTypeID=" + serviceTypeID);
        // wait until done
        synchronized (this) {
            while (!done) {
                wait();
            }
        }
        return new Integer(0);
    }

    private int getServicePort() {
        //$NON-NLS-1$
        final String osgiPort = System.getProperty("org.osgi.service.http.port");
        Integer servicePort = new Integer(80);
        if (osgiPort != null) {
            servicePort = Integer.valueOf(osgiPort);
        }
        return servicePort.intValue();
    }

    private void initializeFromArguments(String[] args) throws Exception {
        if (args == null)
            return;
        for (int i = 0; i < args.length; i++) {
            if (//$NON-NLS-1$
            !args[i].startsWith("-")) {
                String arg = args[i++];
                if (!//$NON-NLS-1$
                arg.endsWith(//$NON-NLS-1$
                "/"))
                    //$NON-NLS-1$
                    arg = arg + "/";
                updateSiteLocation = new URL(arg);
            } else {
                if (//$NON-NLS-1$
                args[i - 1].equalsIgnoreCase(//$NON-NLS-1$
                "-username"))
                    username = args[++i];
                else if (//$NON-NLS-1$
                args[i - 1].equalsIgnoreCase(//$NON-NLS-1$
                "-serviceType"))
                    serviceType = args[++i];
                else if (//$NON-NLS-1$
                args[i - 1].equalsIgnoreCase(//$NON-NLS-1$
                "-serviceName"))
                    serviceName = args[++i];
                else if (//$NON-NLS-1$
                args[i - 1].equalsIgnoreCase(//$NON-NLS-1$
                "-servicePath"))
                    servicePath = args[++i];
                else if (//$NON-NLS-1$
                args[i - 1].equalsIgnoreCase(//$NON-NLS-1$
                "-updateSiteName"))
                    updateSiteName = args[++i];
            }
        }
        if (updateSiteLocation == null) {
            usage();
            //$NON-NLS-1$
            throw new InvalidParameterException("updateSiteDirectoryURL required");
        }
        //$NON-NLS-1$
        username = (username == null) ? System.getProperty("user.name") : username;
        serviceType = (serviceType == null) ? DEFAULT_UPDATE_SITE_SERVICE_TYPE : serviceType;
        //$NON-NLS-1$
        serviceName = (serviceName == null) ? username + " update site" : serviceName;
        //$NON-NLS-1$
        servicePath = (servicePath == null) ? "/update" : servicePath;
        //$NON-NLS-1$ //$NON-NLS-2$
        updateSiteName = (updateSiteName == null) ? System.getProperty("updateSiteName", username + " update site") : updateSiteName;
    }

    private void usage() {
        //$NON-NLS-1$
        System.out.println("usage: eclipse -console [options] -application org.eclipse.ecf.examples.updatesite.server.updateSiteServer <updateSiteDirectoryURL>");
        //$NON-NLS-1$
        System.out.println("   options: [-username <username>] default=<current user>");
        //$NON-NLS-1$
        System.out.println("            [-serviceType <servicetype>] default=updatesite");
        //$NON-NLS-1$
        System.out.println("            [-serviceName <name>] default=<current user> update site");
        //$NON-NLS-1$
        System.out.println("            [-servicePath <path>] default=/update");
        //$NON-NLS-1$
        System.out.println("            [-updateSiteName <name>] default=<current user> update site");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
    public void stop() {
        if (discovery != null && serviceInfo != null) {
            discovery.unregisterService(serviceInfo);
            discovery = null;
            serviceInfo = null;
        }
        synchronized (this) {
            done = true;
            notifyAll();
        }
    }
}
