/**
 * Copyright (c) 2006 Parity Communications, Inc. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sergey Yakovlev - initial API and implementation
 */
package org.eclipse.ecf.internal.provider.rss.container;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.SharedObjectCreateException;
import org.eclipse.ecf.core.sharedobject.SharedObjectDescription;
import org.eclipse.ecf.core.sharedobject.SharedObjectFactory;
import org.eclipse.ecf.core.sharedobject.SharedObjectTypeDescription;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectDeactivatedEvent;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.datashare.IChannelContainerListener;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelContainerChannelActivatedEvent;
import org.eclipse.ecf.datashare.events.IChannelContainerChannelDeactivatedEvent;
import org.eclipse.ecf.datashare.events.IChannelContainerEvent;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.mergeable.IMergeableChannel;
import org.eclipse.ecf.datashare.mergeable.IMergeableChannelContainerAdapter;
import org.eclipse.ecf.internal.provider.rss.RssDebugOptions;
import org.eclipse.ecf.internal.provider.rss.RssPlugin;
import org.eclipse.ecf.internal.provider.rss.http.HttpClient;
import org.eclipse.ecf.provider.comm.ConnectionCreateException;
import org.eclipse.ecf.provider.comm.ISynchAsynchConnection;
import org.eclipse.ecf.provider.generic.ClientSOContainer;
import org.eclipse.ecf.provider.generic.SOContainerConfig;
import org.eclipse.higgins.rsse.RssFeed;
import org.eclipse.higgins.rsse.RssItem;
import org.eclipse.higgins.rsse.parser.FeedParser;
import org.eclipse.higgins.rsse.parser.ParseException;
import org.eclipse.higgins.rsse.util.RssVersion;

/**
 * The RssClientSOContainer implements the basic RSS client functionality.
 * 
 */
public class RssClientSOContainer extends ClientSOContainer implements IMergeableChannelContainerAdapter {

    public static final String DEFAULT_COMM_NAME = org.eclipse.ecf.internal.provider.rss.http.HttpClient.class.getName();

    public static final int DEFAULT_KEEPALIVE = 30000;

    int keepAlive = 0;

    protected List channelContainerListener = Collections.synchronizedList(new ArrayList());

    protected void fireChannelContainerListeners(IChannelContainerEvent event) {
        synchronized (channelContainerListener) {
            for (final Iterator i = channelContainerListener.iterator(); i.hasNext(); ) {
                final IChannelContainerListener l = (IChannelContainerListener) i.next();
                if (l != null)
                    l.handleChannelContainerEvent(event);
            }
        }
    }

    /**
	 * The constructors
	 * 
	 * @throws IDCreateException
	 */
    public  RssClientSOContainer() throws IDCreateException {
        this(DEFAULT_KEEPALIVE);
    }

    public  RssClientSOContainer(int keepAlive) throws IDCreateException {
        this(IDFactory.getDefault().createGUID(), keepAlive);
    }

    public  RssClientSOContainer(String userhost) throws IDCreateException {
        this(userhost, DEFAULT_KEEPALIVE);
    }

    public  RssClientSOContainer(String userhost, int keepAlive) throws IDCreateException {
        this(IDFactory.getDefault().createStringID(userhost), keepAlive);
    }

    public  RssClientSOContainer(ID containerId, int keepAlive) throws IDCreateException {
        super(new SOContainerConfig(containerId));
        this.keepAlive = keepAlive;
        this.addListener(new ContainerListener());
    }

    protected class ContainerListener implements IContainerListener {

        public void handleEvent(final IContainerEvent evt) {
            if (evt instanceof ISharedObjectActivatedEvent) {
                final ISharedObjectActivatedEvent soae = (ISharedObjectActivatedEvent) evt;
                fireChannelContainerListeners(new IChannelContainerChannelActivatedEvent() {

                    public ID getChannelID() {
                        return soae.getActivatedID();
                    }

                    public ID getChannelContainerID() {
                        return soae.getLocalContainerID();
                    }

                    public String toString() {
                        final StringBuffer buf = new StringBuffer("ChannelActivatedEvent[");
                        buf.append("channelid=").append(soae.getActivatedID()).append(";");
                        buf.append("containerid=").append(soae.getLocalContainerID()).append("]");
                        return buf.toString();
                    }
                });
            } else if (evt instanceof ISharedObjectDeactivatedEvent) {
                final ISharedObjectDeactivatedEvent sode = (ISharedObjectDeactivatedEvent) evt;
                fireChannelContainerListeners(new IChannelContainerChannelDeactivatedEvent() {

                    public ID getChannelID() {
                        return sode.getDeactivatedID();
                    }

                    public ID getChannelContainerID() {
                        return sode.getLocalContainerID();
                    }

                    public String toString() {
                        final StringBuffer buf = new StringBuffer("ChannelDeactivatedEvent[");
                        buf.append("channelid=").append(sode.getDeactivatedID()).append(";");
                        buf.append("containerid=").append(sode.getLocalContainerID()).append("]");
                        return buf.toString();
                    }
                });
            }
        }
    }

    protected void trace(String msg) {
        Trace.trace(RssPlugin.PLUGIN_ID, RssDebugOptions.DEBUG, msg);
    }

    protected void dumpStack(String msg, Throwable e) {
        Trace.catching(RssPlugin.PLUGIN_ID, RssDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "", e);
    }

    protected ISynchAsynchConnection createConnection(ID remoteSpace, Object data) throws ConnectionCreateException {
        trace("createConnection:" + remoteSpace + ":" + data);
        // Object[] args = { new Integer(keepAlive) };
        final ISynchAsynchConnection conn = new HttpClient(receiver);
        return conn;
    }

    protected ID handleConnectResponse(ID originalTarget, Object serverData) throws Exception {
        trace("handleConnectResponse:" + originalTarget + ":" + serverData);
        if (originalTarget != null && !originalTarget.equals(getID())) {
            addNewRemoteMember(originalTarget, null);
            // notify listeners
            fireContainerEvent(new ContainerConnectedEvent(this.getID(), originalTarget));
        }
        return originalTarget;
    }

    public RssFeed receiveFeed(String feedPath) throws IOException {
        RssFeed feed = null;
        final ISynchAsynchConnection connection = getConnection();
        synchronized (connection) {
            if (connection.isConnected()) {
                try {
                    feed = FeedParser.parse((byte[]) connection.sendSynch(null, feedPath.getBytes()));
                } catch (final ParseException e) {
                    throw new IOException(e.getMessage());
                }
            }
        }
        return feed;
    }

    public Object getAdapter(Class clazz) {
        if (clazz.equals(IMergeableChannelContainerAdapter.class)) {
            return this;
        } else {
            return super.getAdapter(clazz);
        }
    }

    public Namespace getChannelNamespace() {
        return IDFactory.getDefault().getNamespaceByName(StringID.class.getName());
    }

    public IMergeableChannel createMergeableChannel(ID channelID, IChannelListener listener, Map properties) throws ECFException {
        return createChannel(channelID, listener, properties);
    }

    public IMergeableChannel createChannel(final ID channelID, final IChannelListener listener, final Map properties) throws ECFException {
        return createChannel(new IChannelConfig() {

            public ID getID() {
                return channelID;
            }

            public IChannelListener getListener() {
                return listener;
            }

            public Object getAdapter(Class adapter) {
                return null;
            }

            public Map getProperties() {
                return properties;
            }
        });
    }

    public IMergeableChannel createChannel(IChannelConfig newChannelConfig) throws ECFException {
        final IChannelListener listener = newChannelConfig.getListener();
        final SharedObjectDescription sodesc = new SharedObjectDescription(FeedSharedObject.class, IDFactory.getDefault().createGUID(), new HashMap());
        final SharedObjectTypeDescription sotypedesc = sodesc.getTypeDescription();
        ISharedObject sharedObject = null;
        if (sotypedesc.getName() != null) {
            sharedObject = SharedObjectFactory.getDefault().createSharedObject(sotypedesc, new Object[] { listener });
        } else {
            sharedObject = createSharedObject(sotypedesc, listener);
        }
        final IMergeableChannel channel = (IMergeableChannel) sharedObject.getAdapter(IMergeableChannel.class);
        if (channel == null) {
            throw new SharedObjectCreateException("Cannot coerce object " + channel + " to be of type IChannel");
        }
        ID newID = sodesc.getID();
        if (newID == null) {
            newID = IDFactory.getDefault().createGUID();
        }
        Map properties = sodesc.getProperties();
        if (properties == null) {
            properties = new HashMap();
        }
        // Now add channel to container...this will block
        getSharedObjectManager().addSharedObject(newID, sharedObject, properties);
        return channel;
    }

    private ISharedObject createSharedObject(SharedObjectTypeDescription sotypedesc, IChannelListener listener) throws SharedObjectCreateException {
        Class clazz;
        try {
            clazz = Class.forName(sotypedesc.getClassName());
        } catch (final ClassNotFoundException e) {
            throw new SharedObjectCreateException("No constructor for shared object of class " + sotypedesc.getClassName(), e);
        }
        Constructor cons = null;
        try {
            cons = clazz.getDeclaredConstructor(new Class[] { IChannelListener.class });
        } catch (final NoSuchMethodException e) {
            throw new SharedObjectCreateException("No constructor for shared object of class " + sotypedesc.getClassName(), e);
        }
        ISharedObject so = null;
        try {
            so = (ISharedObject) cons.newInstance(new Object[] { listener });
        } catch (final Exception e) {
            throw new SharedObjectCreateException("Cannot create instance of class " + sotypedesc.getClassName(), e);
        }
        return so;
    }

    public IChannel getChannel(ID channelID) {
        return (IChannel) getSharedObjectManager().getSharedObject(channelID);
    }

    public boolean removeChannel(ID channelID) {
        return (getSharedObjectManager().removeSharedObject(channelID) != null);
    }

    public static final void main(String[] args) throws Exception {
        // Get server identity
        // String targetURL =
        // "http://"+java.net.InetAddress.getLocalHost().getHostName();
        String targetURL = "http://feeds.feedburner.com";
        if (args.length > 0) {
            targetURL = args[0];
        }
        final ContainerTypeDescription contd = new ContainerTypeDescription(RssContainerInstantiator.class.getName(), RssContainerInstantiator.class.getName(), null);
        ContainerFactory.getDefault().addDescription(contd);
        final RssClientSOContainer container = new RssClientSOContainer();
        // now connect to rss service
        final ID serverID = IDFactory.getDefault().createStringID(targetURL);
        container.connect(serverID, null);
        // get IMergeableChannelContainer adapter
        final IMergeableChannelContainerAdapter channelContainer = (IMergeableChannelContainerAdapter) container.getAdapter(IMergeableChannelContainerAdapter.class);
        // create channel listener
        final IChannelListener listener = new IChannelListener() {

            public void handleChannelEvent(IChannelEvent event) {
                System.out.println("listener.handleChannelEvent(" + event + ")");
            }
        };
        // create a new channel
        final ID channelID = IDFactory.getDefault().createStringID("/reuters/worldNews/");
        // ID channelID = IDFactory.getDefault().createStringID("/feed.xml");
        final IMergeableChannel channel = channelContainer.createMergeableChannel(channelID, listener, new HashMap());
        if (channel instanceof FeedSharedObject) {
            // get remote feed (subscribed)
            final RssFeed remoteFeed = ((FeedSharedObject) channel).getFeed();
            // get local feed (published)
            final File feedFile = new File("feed.xml");
            RssFeed localFeed = RssFeed.load(feedFile);
            if (localFeed == null) {
                localFeed = new RssFeed(remoteFeed.getTitle(), remoteFeed.getLink(), remoteFeed.getDescription());
                localFeed.setVersion(RssVersion.RSS_2_0);
            }
            // merge remote feed with local one
            localFeed.merge(remoteFeed);
            // add a new item to feed
            localFeed.addItem(new RssItem("New Google Item", "This is a new item", "http://www.google.com"));
            // publish updated feed
            localFeed.save(feedFile);
            // print item titles
            final java.util.List items = localFeed.getItems();
            for (int i = 0; i < items.size(); i++) {
                System.out.println(" " + i + " " + ((RssItem) items.get(i)).getTitle());
            }
        }
        // remove the channel
        channelContainer.removeChannel(channelID);
        // disconnect the service
        container.disconnect();
        container.dispose();
        System.out.println("Exiting.");
    }

    public void addListener(IChannelContainerListener listener) {
        channelContainerListener.add(listener);
    }

    public void removeListener(IChannelContainerListener listener) {
        channelContainerListener.add(listener);
    }
}
