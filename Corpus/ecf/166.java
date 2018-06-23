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

import java.util.Map;
import org.eclipse.core.runtime.*;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.datashare.*;
import org.eclipse.ecf.internal.provider.datashare.Activator;
import org.eclipse.ecf.internal.provider.datashare.DatashareProviderDebugOptions;
import org.eclipse.ecf.provider.generic.SOContainer;

public class DatashareContainerAdapter implements IChannelContainerAdapter {

    protected SOContainer container = null;

    protected static final int DEFAULT_TRANSACTION_WAIT = 30000;

    protected static final int SO_CREATION_ERROR = 1001;

    protected SharedObjectDatashareContainerAdapter delegate = null;

    protected ID delegateID = null;

    public  DatashareContainerAdapter(SOContainer container) {
        this.container = container;
        initialize();
    }

    protected void initialize() {
        try {
            this.delegateID = IDFactory.getDefault().createStringID(SharedObjectDatashareContainerAdapter.class.getName());
            this.delegate = new SharedObjectDatashareContainerAdapter();
            this.container.getSharedObjectManager().addSharedObject(delegateID, delegate, null);
        } catch (Exception e) {
            Trace.catching(Activator.PLUGIN_ID, DatashareProviderDebugOptions.EXCEPTIONS_CATCHING, DatashareContainerAdapter.class, "DatashareContainerAdapter.initialize", e);
            Activator.getDefault().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, SO_CREATION_ERROR, "Exception initializing DatashareContainerAdapter instance", e));
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#createChannel(org.eclipse.ecf.datashare.IChannelConfig)
	 */
    public IChannel createChannel(final ID newID, final IChannelListener listener, final Map properties) throws ECFException {
        return delegate.createChannel(newID, listener, properties);
    }

    public IChannel createChannel(IChannelConfig newChannelConfig) throws ECFException {
        return delegate.createChannel(newChannelConfig);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#getChannel(org.eclipse.ecf.core.identity.ID)
	 */
    public IChannel getChannel(ID channelID) {
        return delegate.getChannel(channelID);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#disposeChannel(org.eclipse.ecf.core.identity.ID)
	 */
    public boolean removeChannel(ID channelID) {
        return delegate.removeChannel(channelID);
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
        delegate.addListener(listener);
    }

    public void removeListener(IChannelContainerListener listener) {
        delegate.removeListener(listener);
    }

    public Object getAdapter(Class adapter) {
        if (adapter != null && adapter.equals(IContainer.class))
            return container;
        final IAdapterManager adapterManager = Activator.getDefault().getAdapterManager();
        return (adapterManager == null) ? null : adapterManager.loadAdapter(this, adapter.getName());
    }
}
