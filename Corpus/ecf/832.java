/*******************************************************************************
* Copyright (c) 2013 Markus Alexander Kuppe and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Markus Alexander Kuppe - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

import org.eclipse.ecf.remoteservice.IRemoteCall;

/**
 * @since 8.0
 */
public abstract class AbstractParameterSerializer implements IRemoteCallParameterSerializer {

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.remoteservice.client.IRemoteCallParameterSerializer#serializeParameter(java.lang.String, org.eclipse.ecf.remoteservice.IRemoteCall, org.eclipse.ecf.remoteservice.client.IRemoteCallable, org.eclipse.ecf.remoteservice.client.IRemoteCallParameter[], java.lang.Object[])
	 */
    public IRemoteCallParameter[] serializeParameter(String endpoint, IRemoteCall call, IRemoteCallable callable, IRemoteCallParameter[] currentParameters, Object[] paramToSerialize) {
        return currentParameters;
    }
}
