/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.security;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;

/**
 * Policy handler for connect initiator (clients).
 *
 */
public interface IConnectInitiatorPolicy extends IContainerPolicy {

    /**
	 * Create connect data for given IContainer, given targetID and given context
	 * 
	 * @param container the container that is doing the connecting
	 * @param targetID the target ID from {@link IContainer#connect(ID, IConnectContext)}
	 * @param context from {@link IContainer#connect(ID, IConnectContext)}
	 * @return Object that will be used as data for the connect call
	 */
    public Object createConnectData(IContainer container, ID targetID, IConnectContext context);

    /**
	 * Get connect timeout (in ms)
	 * @return int connect timeout in ms
	 */
    public int getConnectTimeout();
}
