/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.core.sharedobject;

import org.eclipse.ecf.core.security.IConnectInitiatorPolicy;

/**
 * Interface for shared object containers that are clients rather than group
 * manager
 * 
 * @see ISharedObjectContainerGroupManager
 */
public interface ISharedObjectContainerClient {

    /**
	 * Set the connect initiator policy handler for authentication policy
	 * 
	 * @param policy
	 *            the policy to use
	 */
    public void setConnectInitiatorPolicy(IConnectInitiatorPolicy policy);
}
