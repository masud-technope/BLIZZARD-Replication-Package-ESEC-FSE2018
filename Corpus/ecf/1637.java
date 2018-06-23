/*******************************************************************************
 * Copyright (c) 2014 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package com.mycorp.examples.timeservice.host.generic.auth;

import java.security.PermissionCollection;
import java.util.Dictionary;
import java.util.Hashtable;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.security.IConnectHandlerPolicy;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerGroupManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import com.mycorp.examples.timeservice.ITimeService;

public class Activator implements BundleActivator {

    private IContainer hostContainer;

    public void start(BundleContext context) throws Exception {
        // One way to setup authentication is to create/configure the ECF generic provider container
        createAndConfigureHostContainer(context);
        // Create remote service properties...see createRemoteServiceProperties()
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put("service.exported.interfaces", "*");
        props.put("service.exported.configs", "ecf.generic.server");
        //props.put("ecf.endpoint.connecttarget.id", hostContainer.getID().getName());
        // Create MyTimeService impl and register/export as a remote service
        ServiceRegistration<ITimeService> timeServiceRegistration = context.registerService(ITimeService.class, new TimeServiceImpl(), props);
        // Print out that ITimeService remote service registration
        System.out.println("MyTimeService host registered with registration=" + timeServiceRegistration);
    }

    private void createAndConfigureHostContainer(BundleContext context) throws Exception {
        // Get IContainerManager singleton
        ServiceTracker<IContainerManager, IContainerManager> containerManagerTracker = new ServiceTracker<IContainerManager, IContainerManager>(context, IContainerManager.class.getName(), null);
        containerManagerTracker.open();
        IContainerManager containerManager = containerManagerTracker.getService();
        if (containerManager == null)
            throw new NullPointerException("Cannot get IContainerManager service");
        containerManagerTracker.close();
        // Now create a hostContainer instance
        hostContainer = containerManager.getContainerFactory().createContainer("ecf.generic.server");
        // Get the ISharedObjectContainerGroupManager adapter interface
        ISharedObjectContainerGroupManager hostManager = (ISharedObjectContainerGroupManager) hostContainer.getAdapter(ISharedObjectContainerGroupManager.class);
        // Set connect policy
        hostManager.setConnectPolicy(new IConnectHandlerPolicy() {

            public void refresh() {
            }

            public PermissionCollection checkConnect(Object address, ID fromID, ID targetID, String targetGroup, Object connectData) throws Exception {
                // What we will do when we receive a check connect call is to call
                // verifyClientConnect
                verifyClientConnect(fromID, connectData);
                return null;
            }
        });
    }

    void verifyClientConnect(ID clientID, Object connectData) throws Exception {
        // Simply print out the data.  This would/should check the values in the
        // connectData (a map:  "username"->username, "password"->password
        // And check these values against appropriate values from db, or some other source
        // and throw some appropriate exception if things do not match
        System.out.println("clientID=" + clientID + ",connectData=" + connectData);
    }

    public void stop(BundleContext context) throws Exception {
        if (hostContainer != null) {
            hostContainer.disconnect();
            hostContainer.dispose();
            hostContainer = null;
        }
    }
}
