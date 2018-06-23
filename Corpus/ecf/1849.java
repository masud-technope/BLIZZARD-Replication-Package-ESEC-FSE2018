/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc., Peter Nehrer, Boris Bokowski.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.datashare;

import java.io.Serializable;
import java.util.Map;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectMessageEvent;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.datashare.*;
import org.eclipse.ecf.datashare.events.*;
import org.eclipse.ecf.internal.provider.datashare.Activator;

public class BaseChannel extends TransactionSharedObject implements IChannel {

    public static final String RECEIVER_ID_PROPERTY = BaseChannel.class.getName();

    static class ChannelMsg implements Serializable {

        private static final long serialVersionUID = 9065358269778864152L;

        byte[] channelData = null;

         ChannelMsg() {
        //
        }

         ChannelMsg(byte[] data) {
            this.channelData = data;
        }

        byte[] getData() {
            return channelData;
        }

        public String toString() {
            //$NON-NLS-1$
            StringBuffer buf = new StringBuffer("BaseChannel.ChannelMsg[");
            //$NON-NLS-1$ //$NON-NLS-2$
            buf.append("data=").append(getData()).append("]");
            return buf.toString();
        }
    }

    protected IChannelListener listener;

    /**
	 * Primary copy implementation of channel class constructor
	 * 
	 * @param config
	 *            the ISharedObjectTransactionConfig associated with this new
	 *            host instance
	 * @param listener
	 *            the listener associated with this channel instance
	 */
    public  BaseChannel(ISharedObjectTransactionConfig config, IChannelListener listener) {
        super(config);
        setChannelListener(listener);
    }

    /**
	 * Replica implementation of channel class constructor
	 * 
	 */
    public  BaseChannel() {
        super();
    }

    protected void setChannelListener(IChannelListener l) {
        this.listener = l;
    }

    protected void trace(String msg) {
        Trace.trace(Activator.PLUGIN_ID, msg);
    }

    /**
	 * Override of TransasctionSharedObject.initialize(). This method is called
	 * on both the host and the replicas during initialization. <b>Subclasses
	 * that override this method should be certain to call super.initialize() as
	 * the first thing in their own initialization so they get the
	 * initialization defined by TransactionSharedObject and BaseSharedObject.</b>
	 * 
	 * @throws SharedObjectInitException
	 *             if initialization should fail
	 */
    protected void initialize() throws SharedObjectInitException {
        super.initialize();
        if (!isPrimary())
            initializeReplicaChannel();
        addEventProcessor(new IEventProcessor() {

            public boolean processEvent(Event event) {
                //$NON-NLS-1$ //$NON-NLS-2$
                trace("processEvent(" + event + ")");
                IChannelListener l = getListener();
                if (event instanceof IContainerConnectedEvent) {
                    if (l != null)
                        l.handleChannelEvent(createChannelGroupJoinEvent(true, ((IContainerConnectedEvent) event).getTargetID()));
                } else if (event instanceof IContainerDisconnectedEvent) {
                    if (l != null)
                        l.handleChannelEvent(createChannelGroupDepartEvent(true, ((IContainerDisconnectedEvent) event).getTargetID()));
                } else if (event instanceof ISharedObjectMessageEvent) {
                    BaseChannel.this.handleMessageEvent((ISharedObjectMessageEvent) event);
                }
                return false;
            }
        });
        //$NON-NLS-1$
        trace("initialize()");
    }

    /**
	 * Override of TransactionSharedObject.getAdapter()
	 */
    public Object getAdapter(Class clazz) {
        if (clazz.equals(IChannel.class)) {
            return this;
        }
        return super.getAdapter(clazz);
    }

    IChannelConnectEvent createChannelGroupJoinEvent(final boolean hasJoined, final ID targetID) {
        return new IChannelConnectEvent() {

            public ID getTargetID() {
                return targetID;
            }

            public ID getChannelID() {
                return getID();
            }

            public String toString() {
                StringBuffer buf = new //$NON-NLS-1$
                StringBuffer(//$NON-NLS-1$
                "ChannelConnectEvent[");
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                buf.append("channelid=").append(getChannelID()).append(";targetid=").append(getTargetID()).append("]");
                return buf.toString();
            }
        };
    }

    IChannelDisconnectEvent createChannelGroupDepartEvent(final boolean hasJoined, final ID targetID) {
        return new IChannelDisconnectEvent() {

            public ID getTargetID() {
                return targetID;
            }

            public ID getChannelID() {
                return getID();
            }

            public String toString() {
                StringBuffer buf = new //$NON-NLS-1$
                StringBuffer(//$NON-NLS-1$
                "ChannelDisconnectEvent[");
                //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                buf.append("channelid=").append(getChannelID()).append(";targetid=").append(getTargetID()).append("]");
                return buf.toString();
            }
        };
    }

    Event handleMessageEvent(final ISharedObjectMessageEvent event) {
        Object eventData = event.getData();
        ChannelMsg channelMsg = null;
        IChannelListener l = getListener();
        if (eventData instanceof ChannelMsg) {
            channelMsg = (ChannelMsg) eventData;
            final byte[] channelData = channelMsg.getData();
            if (channelData != null) {
                if (l == null)
                    return event;
                listener.handleChannelEvent(new IChannelMessageEvent() {

                    public ID getFromContainerID() {
                        return event.getRemoteContainerID();
                    }

                    public byte[] getData() {
                        return channelData;
                    }

                    public ID getChannelID() {
                        return getID();
                    }

                    public String toString() {
                        StringBuffer buf = new StringBuffer("ChannelMessageEvent[");
                        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        buf.append("channelid=").append(getChannelID()).append(";fromid=").append(getFromContainerID()).append(";data=").append(getData()).append("]");
                        return buf.toString();
                    }
                });
                // Discontinue processing of this event...we are it
                return null;
            }
        }
        return event;
    }

    // Implementation of org.eclipse.ecf.datashare.IChannel
    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannel#sendMessage(byte[])
	 */
    public void sendMessage(byte[] message) throws ECFException {
        sendMessage(null, message);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannel#sendMessage(org.eclipse.ecf.core.identity.ID,
	 *      byte[])
	 */
    public void sendMessage(ID receiver, byte[] message) throws ECFException {
        try {
            getContext().sendMessage(receiver, new ChannelMsg(message));
        } catch (Exception e) {
            throw new ECFException("send message exception", e);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IAbstractChannel#getListener()
	 */
    public synchronized IChannelListener getListener() {
        return listener;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IAbstractChannel#setListener(org.eclipse.ecf.datashare.IChannelListener)
	 */
    public IChannelListener setListener(IChannelListener listener) {
        IChannelListener oldListener = getListener();
        setChannelListener(listener);
        return oldListener;
    }

    // Subclass API
    /**
	 * Receive and process channel events. This method can be overridden by
	 * subclasses to process channel events in a sub-class specific manner.
	 * 
	 * @param channelEvent
	 *            the IChannelEvent to receive and process
	 */
    protected void receiveUndeliveredChannelEvent(IChannelEvent channelEvent) {
        if (isPrimary())
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            trace("host.receiveUndeliveredChannelEvent(" + channelEvent + ";containerid=" + getContext().getLocalContainerID() + ")");
        else
            //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            trace("replica.receiveUndeliveredChannelEvent(" + channelEvent + ";containerid=" + getContext().getLocalContainerID() + ")");
    }

    /**
	 * Override of BaseSharedObject.getReplicaDescription. Note this method
	 * should be overridden by subclasses that wish to specify the type of the
	 * replica created.
	 * 
	 * @param targetContainerID
	 *            the ID of the target container for subsequentreplica creation.
	 *            If null, the target is <b>all</b> current group members
	 * @return ReplicaSharedObjectDescripton to be used for creating remote
	 *         replica of this host shared object. If null, no create message
	 *         will be sent to the target container.
	 */
    protected ReplicaSharedObjectDescription getReplicaDescription(ID targetContainerID) {
        return new ReplicaSharedObjectDescription(getClass(), getID(), getConfig().getHomeContainerID(), getConfig().getProperties());
    }

    /**
	 * Initialize replicas of this channel. This method is only called if
	 * isPrimary() returns false. It is called from within the initialize
	 * method, immediately after super.initialize but before the listener for
	 * this channel is notified of initialization. If this method throws a
	 * SharedObjectInitException, then initialization of the replica is halted
	 * and the remote transaction creating the replica will be aborted.
	 * <p>
	 * Note that this implementation checks for the existence of the
	 * RECEIVER_ID_PROPERTY on the replica's properties, and if the property
	 * contains a valid ID will
	 * <ul>
	 * <li>lookup the IChannel on the given container via
	 * IChannelContainerAdapter.getID(ID)</li>
	 * <li>call IChannel.getListener() to retrieve the listener for the channel
	 * returned</li>
	 * <li>set the listener for this object to the value returned from
	 * IChannel.getListener()</li>
	 * </ul>
	 * 
	 * @throws SharedObjectInitException
	 *             if the replica initialization should fail
	 */
    protected void initializeReplicaChannel() throws SharedObjectInitException {
        Map properties = getConfig().getProperties();
        ID rcvr = null;
        try {
            rcvr = (ID) properties.get(RECEIVER_ID_PROPERTY);
        } catch (ClassCastException e) {
            throw new SharedObjectInitException("Receiver ID property value must be of type ID", e);
        }
        if (rcvr != null) {
            // Now...get local channel container first...throw if we can't get
            // it
            IChannelContainerAdapter container = (IChannelContainerAdapter) getContext().getAdapter(IChannelContainerAdapter.class);
            if (container == null)
                throw new //$NON-NLS-1$
                SharedObjectInitException(//$NON-NLS-1$
                "channel container adapter must not be null");
            // Now get receiver IChannel...throw if we can't get it
            final IChannel receiver = container.getChannel(rcvr);
            if (receiver == null)
                throw new //$NON-NLS-1$
                SharedObjectInitException(//$NON-NLS-1$
                "channel receiver must not be null");
            setChannelListener(receiver.getListener());
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IAbstractChannel#dispose()
	 */
    public void dispose() {
        destroySelfLocal();
    }
}
