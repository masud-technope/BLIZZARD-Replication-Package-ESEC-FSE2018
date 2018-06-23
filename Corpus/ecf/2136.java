/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.datashare;

import java.util.Map;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainerConfig;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelContainerListener;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.provider.generic.TCPClientSOContainer;

public class DatashareContainer extends TCPClientSOContainer implements IChannelContainerAdapter {

    protected static final int DEFAULT_CONTAINER_KEEP_ALIVE = 30000;

    protected static final int DEFAULT_TRANSACTION_WAIT = 30000;

    protected DatashareContainerAdapter adapter = null;

    public  DatashareContainer(ISharedObjectContainerConfig config) {
        super(config, DEFAULT_CONTAINER_KEEP_ALIVE);
        adapter = new DatashareContainerAdapter(this);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#createChannel(org.eclipse.ecf.datashare.IChannelConfig)
	 */
    public IChannel createChannel(final ID newID, final IChannelListener listener, final Map properties) throws ECFException {
        return adapter.createChannel(newID, listener, properties);
    }

    public IChannel createChannel(IChannelConfig newChannelConfig) throws ECFException {
        return adapter.createChannel(newChannelConfig);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#getChannel(org.eclipse.ecf.core.identity.ID)
	 */
    public IChannel getChannel(ID channelID) {
        return adapter.getChannel(channelID);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#disposeChannel(org.eclipse.ecf.core.identity.ID)
	 */
    public boolean removeChannel(ID channelID) {
        return adapter.removeChannel(channelID);
    }

    /*
	 * (non-Javadoc)
	 * @see org.eclipse.ecf.datashare.IChannelContainerAdapter#getChannelNamespace()
	 */
    public Namespace getChannelNamespace() {
        return adapter.getChannelNamespace();
    }

    public void addListener(IChannelContainerListener listener) {
        adapter.addListener(listener);
    }

    public void removeListener(IChannelContainerListener listener) {
        adapter.removeListener(listener);
    }
}
