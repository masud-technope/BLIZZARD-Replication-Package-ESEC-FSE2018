/*******************************************************************************
 * Copyright (c) 2010 Naumen. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Pavel Samolisov - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservices.rest.rss;

import java.util.Dictionary;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteServiceClientContainerAdapter;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;
import org.eclipse.ecf.remoteservice.events.IRemoteCallEvent;
import org.eclipse.ecf.remoteservice.rest.IRestCall;
import org.eclipse.ecf.remoteservice.rest.RestCallFactory;
import org.eclipse.ecf.remoteservice.rest.RestCallableFactory;
import org.eclipse.ecf.remoteservice.rest.client.HttpGetRequestType;
import org.eclipse.ecf.remoteservice.rest.identity.RestNamespace;
import org.eclipse.ecf.remoteservice.rest.synd.SyndFeedResponseDeserializer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;

public class Activator implements BundleActivator {

    private static final String REST_CONTAINER_TYPE = "ecf.rest.client";

    private static final String RSS_URL = "http://planeteclipse.org";

    private static final String RSS_PATH = "/planet/rss20.xml";

    private static final RestNamespace REST_NAMESPACE = new RestNamespace(RestNamespace.NAME, null);

    private static BundleContext context;

    private IContainer container;

    private ServiceTracker containerManagerServiceTracker;

    private IRemoteServiceRegistration registration;

    private IRemoteServiceClientContainerAdapter adapter;

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        IContainerManager containerManager = getContainerManagerService();
        container = containerManager.getContainerFactory().createContainer(REST_CONTAINER_TYPE, getRestID(RSS_URL));
        adapter = getRestClientContainerAdapter();
        adapter.setResponseDeserializer(new SyndFeedResponseDeserializer());
        IRemoteService restClientService = adapter.getRemoteService(registerCall().getReference());
        asyncCall(restClientService);
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
        if (registration != null)
            registration.unregister();
        if (container != null)
            container.disconnect();
        if (containerManagerServiceTracker != null)
            containerManagerServiceTracker.close();
    }

    private void asyncCall(IRemoteService restClientService) {
        System.out.println("async calling...");
        restClientService.callAsync(getRestCall(), createRemoteCallListener());
        System.out.println("async called...");
    }

    private IRemoteServiceRegistration registerCall() {
        IRemoteCallable callable = RestCallableFactory.createCallable(RSS_PATH, RSS_PATH, null, new HttpGetRequestType());
        registration = registerCallable(callable, null);
        return registration;
    }

    private void printFeedContent(SyndFeed feed) {
        System.out.println("Author: " + feed.getAuthor());
        System.out.println("Authors:");
        if (feed.getAuthors() != null) {
            for (Object author : feed.getAuthors()) {
                System.out.println(((SyndPerson) author).getName());
                System.out.println(((SyndPerson) author).getEmail());
                System.out.println(((SyndPerson) author).getUri());
                System.out.println();
            }
        }
        System.out.println("Title: " + feed.getTitle());
        System.out.println("Title Ex: " + feed.getTitleEx());
        System.out.println("Description: " + feed.getDescription());
        System.out.println("Description Ex: " + feed.getDescriptionEx());
        System.out.println("Date" + feed.getPublishedDate());
        System.out.println("Type: " + feed.getFeedType());
        System.out.println("Encoding: " + feed.getEncoding());
        System.out.println("(C) " + feed.getCopyright());
        System.out.println();
        for (Object object : feed.getEntries()) {
            SyndEntry entry = (SyndEntry) object;
            System.out.println(entry.getTitle() + " - " + entry.getAuthor());
            System.out.println(entry.getLink());
            for (Object contobj : entry.getContents()) {
                SyndContent content = (SyndContent) contobj;
                System.out.println(content.getType());
                System.out.println(content.getValue());
            }
            SyndContent content = entry.getDescription();
            if (content != null)
                System.out.println(content.getValue());
            System.out.println(entry.getPublishedDate());
            System.out.println();
        }
    }

    private IRemoteCallListener createRemoteCallListener() {
        return new IRemoteCallListener() {

            public void handleEvent(IRemoteCallEvent event) {
                if (event instanceof IRemoteCallCompleteEvent) {
                    IRemoteCallCompleteEvent cce = (IRemoteCallCompleteEvent) event;
                    if (!cce.hadException()) {
                        System.out.println("Remote call completed successfully!");
                        SyndFeed feed = (SyndFeed) cce.getResponse();
                        printFeedContent(feed);
                    } else {
                        System.out.println("Remote call completed with exception: " + cce.getException());
                    }
                }
            }
        };
    }

    private ID getRestID(String uri) {
        return IDFactory.getDefault().createID(REST_NAMESPACE, uri);
    }

    private IRemoteServiceClientContainerAdapter getRestClientContainerAdapter() {
        return (IRemoteServiceClientContainerAdapter) container.getAdapter(IRemoteServiceClientContainerAdapter.class);
    }

    private IRemoteServiceRegistration registerCallable(IRemoteCallable callable, Dictionary<String, String> properties) {
        return adapter.registerCallables(new IRemoteCallable[] { callable }, properties);
    }

    private IRestCall getRestCall() {
        return RestCallFactory.createRestCall(RSS_PATH);
    }

    private IContainerManager getContainerManagerService() {
        if (containerManagerServiceTracker == null) {
            containerManagerServiceTracker = new ServiceTracker(context, IContainerManager.class.getName(), null);
            containerManagerServiceTracker.open();
        }
        return (IContainerManager) containerManagerServiceTracker.getService();
    }
}
