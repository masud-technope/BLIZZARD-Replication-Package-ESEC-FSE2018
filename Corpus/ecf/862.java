/****************************************************************************
 * Copyright (c) 2009 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ****************************************************************************/
package org.eclipse.ecf.internal.examples.loadbalancing.consumer;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.examples.loadbalancing.IDataProcessor;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.util.tracker.IRemoteServiceTrackerCustomizer;
import org.eclipse.ecf.remoteservice.util.tracker.RemoteServiceTracker;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class DataProcessorConsumerApplication implements IApplication {

    private static final String LB_SVCCONSUMER_CONTAINER_TYPE = "ecf.jms.activemq.tcp.client";

    private static final String DEFAULT_TOPIC_ID = "tcp://localhost:61616/exampleTopic";

    private static final String DEFAULT_INPUT_DATA = "hello there";

    private BundleContext bundleContext;

    private ServiceTracker containerManagerServiceTracker;

    // JMS topic URI that we will connect to in order to lookup/get/use the
    // data processor remote service. Note that this topicId can be
    // changed by using the -topicId launch parameter...e.g.
    // -topicId tcp://myjmdnsbrokerdnsname:61616/myTopicName
    private String topicId = DEFAULT_TOPIC_ID;

    // Container type is the load balancing service consumer container type,
    // which is normal client
    private String containerType = LB_SVCCONSUMER_CONTAINER_TYPE;

    // Container instance that connects us with the ActiveMQ queue as a message
    // producer and publishes the service on the topicId
    private IContainer container;

    private IRemoteServiceContainerAdapter remoteServiceAdapter;

    // Input data that is passed to the data processor
    private String inputData = DEFAULT_INPUT_DATA;

    // Lock and flag for synchronization
    private final Object remoteServiceReceivedLock = new Object();

    private boolean remoteServiceReceived = false;

    // Remote service. The RemoteServiceTrackerCustomizer sets this
    IRemoteService remoteService;

    class RemoteServiceTrackerCustomizer implements IRemoteServiceTrackerCustomizer {

        public IRemoteService addingService(IRemoteServiceReference reference) {
            remoteService = remoteServiceAdapter.getRemoteService(reference);
            try {
                IDataProcessor dataProcessorProxy = (IDataProcessor) remoteService.getProxy();
                System.out.println("Calling remote service with input data=" + inputData);
                // And then call it
                String result = dataProcessorProxy.processData(inputData);
                // And print out results
                System.out.println("\tremote service result=" + result);
            } catch (ECFException e) {
                e.printStackTrace();
            }
            synchronized (remoteServiceReceivedLock) {
                remoteServiceReceived = true;
                remoteServiceReceivedLock.notify();
            }
            return remoteService;
        }

        public void modifiedService(IRemoteServiceReference reference, IRemoteService remoteService) {
        }

        public void removedService(IRemoteServiceReference reference, IRemoteService remoteService) {
        }
    }

    public Object start(IApplicationContext appContext) throws Exception {
        bundleContext = Activator.getContext();
        // Process Arguments...i.e. set queueId and topicId if specified
        processArgs(appContext);
        // Create container of appropriate type
        container = getContainerManagerService().getContainerFactory().createContainer(containerType);
        // Get appropriate adapter
        remoteServiceAdapter = (IRemoteServiceContainerAdapter) container.getAdapter(IRemoteServiceContainerAdapter.class);
        // Create remote service tracker, and then open it
        RemoteServiceTracker tracker = new RemoteServiceTracker(remoteServiceAdapter, null, IDataProcessor.class.getName(), new RemoteServiceTrackerCustomizer());
        // Open it
        tracker.open();
        // Connect to topic.  This should trigger remote service registration to be asynchronously
        // added, and in turn call RemoteServiceTrackerCustomizer.addingService (see impl of that
        // method above
        container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), topicId), null);
        // Wait for remote service tracker to receive proxy and execute.   See 
        // RemoteServiceTrackerCustomizer.addingService above
        waitForRemoteService();
        return IApplication.EXIT_OK;
    }

    public void stop() {
        if (container != null) {
            container.dispose();
            container = null;
            getContainerManagerService().removeAllContainers();
        }
        if (containerManagerServiceTracker != null) {
            containerManagerServiceTracker.close();
            containerManagerServiceTracker = null;
        }
        synchronized (remoteServiceReceivedLock) {
            remoteServiceReceived = true;
            remoteServiceReceivedLock.notifyAll();
        }
        bundleContext = null;
    }

    private void processArgs(IApplicationContext appContext) {
        String[] originalArgs = (String[]) appContext.getArguments().get("application.args");
        if (originalArgs == null)
            return;
        for (int i = 0; i < originalArgs.length; i++) {
            if (originalArgs[i].equals("-topicId")) {
                topicId = originalArgs[i + 1];
                i++;
            } else if (originalArgs[i].equals("-inputData")) {
                StringBuffer buf = new StringBuffer();
                for (int j = i + 1; j < originalArgs.length; j++) {
                    buf.append(originalArgs[j]).append(" ");
                }
                inputData = buf.toString();
                return;
            } else if (originalArgs[i].equals("-containerType")) {
                containerType = originalArgs[i + 1];
                i++;
            }
        }
    }

    private IContainerManager getContainerManagerService() {
        if (containerManagerServiceTracker == null) {
            containerManagerServiceTracker = new ServiceTracker(bundleContext, IContainerManager.class.getName(), null);
            containerManagerServiceTracker.open();
        }
        return (IContainerManager) containerManagerServiceTracker.getService();
    }

    private void waitForRemoteService() {
        // then just wait here
        synchronized (remoteServiceReceivedLock) {
            while (!remoteServiceReceived) {
                try {
                    remoteServiceReceivedLock.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
