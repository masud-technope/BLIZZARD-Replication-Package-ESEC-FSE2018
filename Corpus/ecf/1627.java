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
package org.eclipse.ecf.presence.bot;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;

/**
 * Advisor instance for receiving container initialization and pre-connect
 * notifications.
 * 
 */
public interface IContainerAdvisor {

    /**
	 * This method will be called prior to calling the container's
	 * {@link IContainer#connect(ID, org.eclipse.ecf.core.security.IConnectContext)}
	 * method.
	 * 
	 * @param container
	 *            the container instance created. Will not be <code>null</code>.
	 * @param targetID
	 *            the target id instance to connect to. Will not be
	 *            <code>null</code>.
	 */
    public void preContainerConnect(IContainer container, ID targetID);
}
