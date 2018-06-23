/******************************************************************************
 * Copyright (c) 2009 Remy Chi Jian Suen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Remy Chi Jian Suen - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.tests.provider.datashare.nio;

import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelConfig;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.provider.datashare.nio.NIOChannel;
import org.eclipse.ecf.provider.datashare.nio.NIODatashareContainer;

public class ConcreteNIODatashareContainer extends NIODatashareContainer {

    private IContainer container;

    public  ConcreteNIODatashareContainer(IContainer container) {
        super(container);
        this.container = container;
    }

    protected void log(IStatus status) {
        System.err.println(status.getMessage());
        Throwable t = status.getException();
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }

    protected NIOChannel createNIOChannel(ID channelId, IChannelListener listener, Map properties) throws ECFException {
        return new ConcreteNIOChannel(this, container.getConnectedID(), channelId, listener);
    }

    protected NIOChannel createNIOChannel(IChannelConfig newChannelConfig) throws ECFException {
        return null;
    }

    public Namespace getChannelNamespace() {
        return null;
    }
}
