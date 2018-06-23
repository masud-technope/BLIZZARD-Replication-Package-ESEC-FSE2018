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
package org.eclipse.ecf.examples.provider.trivial;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;

/**
 * 
 */
public class TrivialClient implements Runnable {

    public void run() {
        try {
            // Create instance of trivial container
            IContainer container = ContainerFactory.getDefault().createContainer("ecf.container.trivial");
            // Get appropriate container adapter...e.g. IChannelContainerAdapter
            // IChannelContainerAdapter containerAdapter =
            // (IChannelContainerAdapter)
            // container.getAdapter(IChannelContainerAdapter.class);
            // Connect
            ID targetID = IDFactory.getDefault().createID(container.getConnectNamespace(), "myid");
            container.connect(targetID, null);
        } catch (ECFException e) {
            e.printStackTrace();
        }
    }
}
