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

import java.io.IOException;
import java.util.List;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.identity.IIdentifiable;
import org.eclipse.ecf.core.sharedobject.ISharedObject;
import org.eclipse.ecf.core.sharedobject.ISharedObjectConfig;
import org.eclipse.ecf.core.sharedobject.SharedObjectInitException;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectDeactivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectMessageEvent;
import org.eclipse.ecf.core.util.Event;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelConnectEvent;
import org.eclipse.ecf.datashare.events.IChannelDisconnectEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.ecf.datashare.mergeable.IChannelHeader;
import org.eclipse.ecf.datashare.mergeable.IItem;
import org.eclipse.ecf.datashare.mergeable.IItemFactory;
import org.eclipse.ecf.datashare.mergeable.IMergeableChannel;
import org.eclipse.ecf.datashare.mergeable.IMergeableChannelContainerAdapter;
import org.eclipse.ecf.datashare.mergeable.MergeException;
import org.eclipse.ecf.datashare.mergeable.PublishException;
import org.eclipse.ecf.internal.provider.rss.RssDebugOptions;
import org.eclipse.ecf.internal.provider.rss.RssPlugin;
import org.eclipse.higgins.rsse.RssFeed;
import org.eclipse.higgins.rsse.RssItem;

/**
 * 
 */
public class FeedSharedObject implements ISharedObject, IIdentifiable, IMergeableChannel {

    public static String PUBLISH_PROPERTY_NAME = FeedSharedObject.class.getPackage().getName() + ".publishPathName";

    protected ISharedObjectConfig config;

    protected RssFeed feed;

    protected IChannelListener listener;

    protected String publishPathName;

    public  FeedSharedObject(IChannelListener listener) {
        setListener(listener);
    }

    protected void trace(String msg) {
        Trace.trace(RssPlugin.PLUGIN_ID, RssDebugOptions.DEBUG, msg);
    }

    protected void dumpStack(String msg, Throwable e) {
        Trace.catching(RssPlugin.PLUGIN_ID, RssDebugOptions.EXCEPTIONS_CATCHING, this.getClass(), "", e);
    }

    public RssFeed getFeed() {
        return feed;
    }

    public void setListener(IChannelListener listener) {
        this.listener = listener;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObject#init(org.eclipse.ecf.core.ISharedObjectConfig)
	 */
    public void init(ISharedObjectConfig initData) throws SharedObjectInitException {
        if (config == null) {
            config = initData;
        } else {
            throw new SharedObjectInitException("Already initialized.");
        }
        trace("init(" + initData + ")");
        // get local publish path
        publishPathName = (String) initData.getProperties().get(PUBLISH_PROPERTY_NAME);
        publishPathName = publishPathName == null ? "feed.xml" : publishPathName;
        // get local channel container first...throw if we can't get it
        IMergeableChannelContainerAdapter container = (IMergeableChannelContainerAdapter) config.getContext().getAdapter(IMergeableChannelContainerAdapter.class);
        if (container == null) {
            throw new SharedObjectInitException("Channel container is null/not available");
        }
        if (container instanceof RssClientSOContainer) {
            // get rss feed
            try {
                feed = ((RssClientSOContainer) container).receiveFeed(getID().getName());
            } catch (IOException ioe) {
                throw new SharedObjectInitException(ioe);
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.ISharedObject#dispose(org.eclipse.ecf.core.identity.ID)
	 */
    public void dispose(ID containerID) {
        trace("dispose(" + containerID + ")");
        config = null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter.equals(IMergeableChannel.class)) {
            return this;
        } else {
            return null;
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IEventHandler#handleEvents(org.eclipse.ecf.core.util.Event[])
	 */
    public void handleEvents(Event[] events) {
        for (int i = 0; i < events.length; ++i) {
            handleEvent(events[i]);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.util.IEventHandler#handleEvent(org.eclipse.ecf.core.util.Event)
	 */
    public void handleEvent(Event event) {
        trace("handleEvent(" + event + ")");
        if (event instanceof ISharedObjectMessageEvent) {
            ISharedObjectMessageEvent e = (ISharedObjectMessageEvent) event;
            handleMessage(e);
        } else if (event instanceof IContainerConnectedEvent) {
            IContainerConnectedEvent e = (IContainerConnectedEvent) event;
            if (e.getTargetID().equals(config.getContext().getLocalContainerID())) {
            // this container joined
            // handleJoined();
            } else if (config.getContext().isGroupManager()) {
                // some other container joined and we're the server
                handleJoined(e.getTargetID());
            }
        } else if (event instanceof IContainerDisconnectedEvent) {
            IContainerDisconnectedEvent e = (IContainerDisconnectedEvent) event;
            // some other container departed -- same as peer deactivation
            if (!e.getTargetID().equals(config.getContext().getLocalContainerID())) {
                handleLeave(e.getTargetID());
            }
        } else if (event instanceof ISharedObjectActivatedEvent) /*
																	 * ISharedObjectActivatedEvent
																	 * e =
																	 * (ISharedObjectActivatedEvent)
																	 * event;
																	 * if(e.getActivatedID().equals(config.getSharedObjectID())) { //
																	 * we're
																	 * being
																	 * activated
																	 * handleActivated(); }
																	 */
        {
        } else if (event instanceof ISharedObjectDeactivatedEvent) /*
																	 * ISharedObjectDeactivatedEvent
																	 * e =
																	 * (ISharedObjectDeactivatedEvent)
																	 * event;
																	 * if(e.getDeactivatedID().equals(config.getSharedObjectID())) { //
																	 * we're
																	 * being
																	 * deactivated
																	 * handleDeactivated(); }
																	 * else
																	 * if(table.contains(e.getDeactivatedID())) { //
																	 * a local
																	 * graph we
																	 * track is
																	 * being
																	 * deactivated
																	 * handleRemoved(e.getDeactivatedID()); }
																	 */
        {
        }
    }

    private void handleJoined(final ID targetID) {
        listener.handleChannelEvent(new IChannelConnectEvent() {

            public ID getTargetID() {
                return targetID;
            }

            public ID getChannelID() {
                return getID();
            }

            public String toString() {
                StringBuffer buf = new StringBuffer("ChannelGroupJoinEvent[");
                buf.append("chid=").append(getChannelID()).append(";targetid=").append(getTargetID()).append("]");
                return buf.toString();
            }
        });
    }

    private void handleMessage(final ISharedObjectMessageEvent event) {
        listener.handleChannelEvent(new IChannelMessageEvent() {

            public ID getFromContainerID() {
                return event.getRemoteContainerID();
            }

            public byte[] getData() {
                return (byte[]) event.getData();
            }

            public ID getChannelID() {
                return getID();
            }

            public String toString() {
                StringBuffer buf = new StringBuffer("ChannelMessageEvent[");
                buf.append("chid=").append(getChannelID()).append(";fromid=").append(getFromContainerID()).append(";data=").append(getData()).append("]");
                return buf.toString();
            }
        });
    }

    private void handleLeave(final ID targetID) {
        listener.handleChannelEvent(new IChannelDisconnectEvent() {

            public ID getTargetID() {
                return targetID;
            }

            public ID getChannelID() {
                return getID();
            }

            public String toString() {
                StringBuffer buf = new StringBuffer("ChannelGroupDepartedEvent[");
                buf.append("chid=").append(getChannelID()).append(";targetid=").append(getTargetID()).append("]");
                return buf.toString();
            }
        });
    }

    public ID getID() {
        return config != null ? config.getSharedObjectID() : null;
    }

    /*
	 * public void sendMessage(byte[] message) throws ECFException {
	 * sendMessage(null, message); }
	 * 
	 * public void sendMessage(ID receiver, byte[] message) throws ECFException {
	 * throw new ECFException("Async message isn't allowed"); }
	 */
    public IChannelListener getListener() {
        return listener;
    }

    public List getItems() {
        if (feed != null) {
            return feed.getItems();
        }
        return null;
    }

    public void merge() throws MergeException {
    // TODO Auto-generated method stub
    }

    public boolean addItem(IItem item) {
        if (feed != null) {
            if (item instanceof RssItem) {
                return feed.addItem((RssItem) item);
            }
        }
        return false;
    }

    public boolean changeItem(ID itemID, String description) {
        if (feed != null) {
            List items = feed.getItems();
            for (int i = 0; i < items.size(); i++) {
                RssItem rssItem = (RssItem) items.get(i);
                ID id = createID(rssItem.getSync().getId());
                if (id != null && id.equals(itemID)) {
                    rssItem.setDescription(description);
                    return true;
                }
            }
        }
        return false;
    }

    private ID createID(String id) {
        try {
            return IDFactory.getDefault().createStringID(id);
        } catch (IDCreateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean removeItem(IItem item) {
        if (feed != null) {
            if (item instanceof RssItem) {
                List items = feed.getItems();
                for (int i = 0; i < items.size(); i++) {
                    RssItem rssItem = (RssItem) items.get(i);
                    if (rssItem.equals(item)) {
                        items.remove(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void publish() throws PublishException {
        if (feed != null) {
        // TODO Auto-generated method stub
        }
    }

    public IItemFactory getItemFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    public IChannelHeader getHeaderInfo() {
        // TODO Auto-generated method stub
        return null;
    }
}
