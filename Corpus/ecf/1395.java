/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis (slewis@composent.com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.examples.raspberrypi.management.host;

import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.ecf.examples.raspberrypi.management.IRaspberryPi;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private static final String OSGI_SERVICE_EXPORTED_INTERFACES = "service.exported.interfaces";

    private ServiceRegistration<IRaspberryPi> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        // Add OSGi required remote service properties
        props.put(OSGI_SERVICE_EXPORTED_INTERFACES, System.getProperty(OSGI_SERVICE_EXPORTED_INTERFACES, "*"));
        // Use ECF generic server config.
        props.put("service.exported.configs", "ecf.generic.server");
        // Setup hostname config (default:localhost)
        String hostname = System.getProperty("ecf.generic.server.hostname");
        if (hostname != null)
            props.put("ecf.generic.server.hostname", hostname);
        // Setup port config (default:-1)
        props.put("ecf.generic.server.port", new Integer(System.getProperty("ecf.generic.server.port", "-1")));
        // Setup IRaspberryPiAsync as async remote service
        props.put("ecf.exported.async.interfaces", "*");
        // This remote service registration will trigger export, and publishing via zeroconf
        registration = context.registerService(IRaspberryPi.class, new RaspberryPi(), props);
        System.out.println("IRaspberryPi remote service registered=" + registration);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        System.out.println("IRaspberryPi remote service unregistered");
    }
}
