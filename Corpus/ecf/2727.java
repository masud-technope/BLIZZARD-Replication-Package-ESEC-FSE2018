/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.datashare;

import java.lang.reflect.Constructor;
import java.util.*;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.sharedobject.*;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectActivatedEvent;
import org.eclipse.ecf.core.sharedobject.events.ISharedObjectDeactivatedEvent;
import org.eclipse.ecf.core.util.*;
import org.eclipse.ecf.datashare.*;
import org.eclipse.ecf.datashare.events.*;
import org.eclipse.ecf.internal.provider.datashare.Activator;

public class SharedObjectDatashareContainerAdapter extends BaseSharedObject implements IChannelContainerAdapter {

    protected static final int DEFAULT_TRANSACTION_WAIT = 30000;

    protected List channelContainerListeners = Collections.synchronizedList(new ArrayList());

    protected void initialize() throws SharedObjectInitException {
        super.initialize();
        addEventProcessor(new IEventProcessor() {

            public boolean processEvent(Event event) {
                if (event instanceof ISharedObjectActivatedEvent) {
                    final ISharedObjectActivatedEvent soae = (ISharedObjectActivatedEvent) event;
                    if (!soae.getActivatedID().equals(getID()))
                        fireChannelContainerListeners(new IChannelContainerChannelActivatedEvent() {

                            public ID getChannelID() {
                                return soae.getActivatedID();
                            }

                            public ID getChannelContainerID() {
                                return soae.getLocalContainerID();
                            }

                            public String toString() {
                                StringBuffer buf = new //$NON-NLS-1$
                                StringBuffer(//$NON-NLS-1$
                                "ChannelActivatedEvent[");
                                //$NON-NLS-1$ //$NON-NLS-2$
                                buf.append("channelid=").append(soae.getActivatedID()).append(//$NON-NLS-1$ //$NON-NLS-2$
                                ";");
                                //$NON-NLS-1$ //$NON-NLS-2$
                                buf.append("containerid=").append(soae.getLocalContainerID()).append(//$NON-NLS-1$ //$NON-NLS-2$
                                "]");
                                return buf.toString();
                            }
                        });
                } else if (event instanceof ISharedObjectDeactivatedEvent) {
                    final ISharedObjectDeactivatedEvent sode = (ISharedObjectDeactivatedEvent) event;
                    if (!sode.getDeactivatedID().equals(getID()))
                        fireChannelContainerListeners(new IChannelContainerChannelDeactivatedEvent() {

                            public ID getChannelID() {
                                return sode.getDeactivatedID();
                            }

                            public ID getChannelContainerID() {
                                return sode.getLocalContainerID();
                            }

                            public String toString() {
                                StringBuffer buf = new //$NON-NLS-1$
                                StringBuffer(//$NON-NLS-1$
                                "ChannelDeactivatedEvent[");
                                //$NON-NLS-1$ //$NON-NLS-2$
                                buf.append("channelid=").append(sode.getDeactivatedID()).append(//$NON-NLS-1$ //$NON-NLS-2$
                                ";");
                                //$NON-NLS-1$ //$NON-NLS-2$
                                buf.append("containerid=").append(sode.getLocalContainerID()).append(//$NON-NLS-1$ //$NON-NLS-2$
                                "]");
                                return buf.toString();
                            }
                        });
                }
                return false;
            }
        });
    }

    protected void fireChannelContainerListeners(IChannelContainerEvent event) {
        synchronized (channelContainerListeners) {
            for (Iterator i = channelContainerListeners.iterator(); i.hasNext(); ) {
                IChannelContainerListener l = (IChannelContainerListener) i.next();
                if (l != null)
                    l.handleChannelContainerEvent(event);
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#createChannel(org.eclipse.ecf.datashare.IChannelConfig)
	 */
    public IChannel createChannel(final ID newID, final IChannelListener listener, final Map properties) throws ECFException {
        return createChannel(new IChannelConfig() {

            public IChannelListener getListener() {
                return listener;
            }

            public ID getID() {
                return newID;
            }

            public Object getAdapter(Class adapter) {
                return null;
            }

            public Map getProperties() {
                return properties;
            }
        });
    }

    /**
	 * @param channelConfig
	 * @return SharedObjectDescription a non-<code>null</code> instance.
	 * @throws ECFException not thrown by this implementation.
	 */
    protected SharedObjectDescription createChannelSharedObjectDescription(final IChannelConfig channelConfig) throws ECFException {
        return new SharedObjectDescription(BaseChannel.class, channelConfig.getID(), channelConfig.getProperties());
    }

    protected ISharedObjectTransactionConfig createChannelSharedObjectTransactionConfig() {
        return null;
    }

    protected ISharedObject createSharedObject(SharedObjectTypeDescription typeDescription, ISharedObjectTransactionConfig transactionConfig, IChannelListener listener) throws SharedObjectCreateException {
        try {
            Class clazz = Class.forName(typeDescription.getClassName());
            Constructor cons = clazz.getDeclaredConstructor(new Class[] { ISharedObjectTransactionConfig.class, IChannelListener.class });
            return (ISharedObject) cons.newInstance(new Object[] { transactionConfig, listener });
        } catch (Exception e) {
            throw new SharedObjectCreateException("Cannot create shared object of class=" + typeDescription.getClassName(), e);
        }
    }

    public IChannel createChannel(IChannelConfig newChannelConfig) throws ECFException {
        SharedObjectDescription sodesc = createChannelSharedObjectDescription(newChannelConfig);
        SharedObjectTypeDescription sotypedesc = sodesc.getTypeDescription();
        IChannelListener listener = newChannelConfig.getListener();
        ISharedObjectTransactionConfig transactionConfig = createChannelSharedObjectTransactionConfig();
        ISharedObject so = null;
        if (sotypedesc.getName() != null) {
            so = SharedObjectFactory.getDefault().createSharedObject(sotypedesc, new Object[] { transactionConfig, listener });
        } else {
            so = createSharedObject(sotypedesc, transactionConfig, listener);
        }
        IChannel channel = (IChannel) so.getAdapter(IChannel.class);
        if (channel == null)
            //$NON-NLS-1$
            throw new SharedObjectCreateException("channel must not be null");
        ID newID = sodesc.getID();
        if (newID == null)
            newID = IDFactory.getDefault().createGUID();
        // Now add channel to container...this will block
        getContext().getSharedObjectManager().addSharedObject(newID, so, sodesc.getProperties());
        return channel;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#getChannel(org.eclipse.ecf.core.identity.ID)
	 */
    public IChannel getChannel(ID channelID) {
        if (channelID == null || channelID.equals(getID()))
            return null;
        return (IChannel) getContext().getSharedObjectManager().getSharedObject(channelID);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#removeChannel(org.eclipse.ecf.core.identity.ID)
	 */
    public boolean removeChannel(ID channelID) {
        if (channelID == null || channelID.equals(getID()))
            return false;
        ISharedObject o = getContext().getSharedObjectManager().removeSharedObject(channelID);
        if (o != null && o instanceof IChannel) {
            ((IChannel) o).dispose();
            return true;
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#getChannelNamespace()
	 */
    public Namespace getChannelNamespace() {
        return IDFactory.getDefault().getNamespaceByName(StringID.class.getName());
    }

    public void addListener(IChannelContainerListener listener) {
        channelContainerListeners.add(listener);
    }

    public void removeListener(IChannelContainerListener listener) {
        channelContainerListeners.add(listener);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.sharedobject.BaseSharedObject#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter != null && adapter.isAssignableFrom(IContainer.class)) {
            IContainerManager containerManager = Activator.getDefault().getContainerManager();
            return containerManager.getContainer(getContext().getLocalContainerID());
        }
        return super.getAdapter(adapter);
    }
}
