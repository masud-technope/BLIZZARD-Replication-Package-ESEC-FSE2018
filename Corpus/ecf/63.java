/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.remoteservice;

import java.util.Dictionary;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.RemoteServiceContainer;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public abstract class AbstractConcatHostApplication implements IApplication {

    protected IRemoteServiceContainer rsContainer;

    protected boolean done = false;

    /**
	 * This is the only method that must be overridden in order to define the
	 * container type used for running a concat server as an application.
	 * 
	 * @return String that will be used to create a container instance (e.g.
	 *         "ecf.generic.server" or "ecf.r_osgi.peer")
	 */
    protected abstract String getContainerType();

    public Object start(IApplicationContext context) throws Exception {
        // First, create container of appropriate type
        IContainer container = createContainer();
        // Then, from container create remote service container
        rsContainer = createRemoteServiceContainer(container);
        // Now register remote service
        registerRemoteService(getRemoteServiceClass(), createRemoteService(), createRemoteServiceProperties());
        printStarted();
        // And wait until we're explicitly stopped.
        synchronized (this) {
            while (!done) wait();
        }
        return new Integer(0);
    }

    public void stop() {
        rsContainer.getContainer().disconnect();
        rsContainer.getContainer().dispose();
        ((IContainerManager) ContainerFactory.getDefault()).removeAllContainers();
        done = true;
        notifyAll();
    }

    protected void printStarted() {
        System.out.println("STARTED: Test Concat Server\n\tcontainerType=" + getContainerType() + "\n\tID=" + rsContainer.getContainer().getID());
    }

    protected Class getRemoteServiceClass() {
        return IConcatService.class;
    }

    protected Object createRemoteService() {
        return new IConcatService() {

            public String concat(String string1, String string2) {
                final String result = string1.concat(string2);
                System.out.println("SERVICE.concat(" + string1 + "," + string2 + ") returning " + result);
                return string1.concat(string2);
            }
        };
    }

    protected Dictionary createRemoteServiceProperties() {
        return null;
    }

    protected IContainer createContainer() throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(getContainerType());
    }

    protected IContainer createContainer(String containerID) throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(getContainerType(), containerID);
    }

    protected IContainer createContainer(String containerType, String containerId) throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(containerType, containerId);
    }

    protected IContainer createContainer(ID containerID) throws ContainerCreateException {
        return Activator.getDefault().getContainerManager().getContainerFactory().createContainer(getContainerType(), containerID);
    }

    protected IRemoteServiceContainer createRemoteServiceContainer(IContainer container) {
        return new RemoteServiceContainer(container);
    }

    protected IRemoteServiceRegistration registerRemoteService(Class clazz, Object service, Dictionary properties) {
        return rsContainer.getContainerAdapter().registerRemoteService(new String[] { clazz.getName() }, service, properties);
    }
}
