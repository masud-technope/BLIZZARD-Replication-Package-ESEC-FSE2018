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

import org.eclipse.ecf.core.BaseContainer;
import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.events.ContainerConnectedEvent;
import org.eclipse.ecf.core.events.ContainerDisconnectedEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.security.IConnectContext;

public class ContainerImpl extends BaseContainer {

    private static int counter = 0;

    private ID connectedId;

    public  ContainerImpl() {
        //$NON-NLS-1$
        super(IDFactory.getDefault().createStringID("A" + counter));
        //$NON-NLS-1$
        connectedId = IDFactory.getDefault().createStringID("B" + counter);
        counter++;
    }

    public void connect(ID targetId, IConnectContext connectContext) throws ContainerConnectException {
        fireContainerEvent(new ContainerConnectedEvent(getID(), getConnectedID()));
    }

    public void disconnect() {
        fireContainerEvent(new ContainerDisconnectedEvent(getID(), getConnectedID()));
    }

    public ID getConnectedID() {
        return connectedId;
    }
}
