/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.internal.examples.datashare.app;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerFactory;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public abstract class AbstractDatashareApplication implements IApplication {

    protected BundleContext bundleContext;

    protected ServiceTracker containerManagerTracker;

    private final Object appLock = new Object();

    private boolean done = false;

    protected IContainer container;

    // The following must be set in processArgs
    protected String containerType;

    protected String containerId;

    protected String targetId;

    // Datashare adapter
    protected IChannelContainerAdapter datashareAdapter;

    // Test channel
    protected IChannel testChannel;

    protected abstract IChannelListener createChannelListener();

    protected abstract void processArgs(String[] args);

    protected abstract String usageApplicationId();

    protected abstract String usageParameters();

    public Object start(IApplicationContext context) throws Exception {
        Object startupResult = startup(context);
        if (!startupResult.equals(IApplication.EXIT_OK))
            return startupResult;
        return run();
    }

    protected void createChannel() throws ECFException {
        // Get datashare adapter from new container
        datashareAdapter = (IChannelContainerAdapter) container.getAdapter(IChannelContainerAdapter.class);
        // Create channel listener for channel to be created
        IChannelListener channelListener = createChannelListener();
        // Create a channel with given channel listener
        testChannel = datashareAdapter.createChannel(IDFactory.getDefault().createStringID(Activator.class.getName()), channelListener, null);
    }

    protected void createContainer() throws ECFException {
        // get container factory and create container
        IContainerFactory containerFactory = getContainerManager().getContainerFactory();
        // If the containerId is null, the id is *not* passed to the container
        // factory
        // If it is non-null (i.e. the server), then it's passed to the factory
        container = (containerId == null) ? containerFactory.createContainer(containerType) : containerFactory.createContainer(containerType, new Object[] { containerId });
    }

    protected void connectContainer() throws ContainerConnectException {
        // then if targetId is non-null (client), connect to target Id
        if (targetId != null)
            container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), targetId), null);
    }

    protected Object startup(IApplicationContext context) throws Exception {
        // Get BundleContext
        bundleContext = Activator.getContext();
        // Process Arguments
        final String[] args = mungeArguments((String[]) context.getArguments().get(//$NON-NLS-1$
        "application.args"));
        processArgs(args);
        createContainer();
        createChannel();
        connectContainer();
        return IApplication.EXIT_OK;
    }

    private String[] mungeArguments(String originalArgs[]) {
        if (originalArgs == null)
            return new String[0];
        final List l = new ArrayList();
        for (int i = 0; i < originalArgs.length; i++) if (//$NON-NLS-1$
        !originalArgs[i].equals("-pdelaunch"))
            l.add(originalArgs[i]);
        return (String[]) l.toArray(new String[] {});
    }

    protected void usage() {
        System.out.println("Usage: eclipse.exe -application " + usageApplicationId() + " " + usageParameters());
    }

    protected void waitForDone() {
        // then just wait here
        synchronized (appLock) {
            while (!done) {
                try {
                    appLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void stop() {
        shutdown();
    }

    protected void shutdown() {
        if (datashareAdapter != null) {
            datashareAdapter.removeChannel(testChannel.getID());
            testChannel = null;
            datashareAdapter = null;
        }
        if (container != null) {
            container.dispose();
            getContainerManager().removeAllContainers();
            container = null;
        }
        if (containerManagerTracker != null) {
            containerManagerTracker.close();
            containerManagerTracker = null;
        }
        synchronized (appLock) {
            done = true;
            appLock.notifyAll();
        }
        bundleContext = null;
    }

    protected Object run() {
        waitForDone();
        return IApplication.EXIT_OK;
    }

    protected IContainerManager getContainerManager() {
        if (containerManagerTracker == null) {
            containerManagerTracker = new ServiceTracker(bundleContext, IContainerManager.class.getName(), null);
            containerManagerTracker.open();
        }
        return (IContainerManager) containerManagerTracker.getService();
    }
}
