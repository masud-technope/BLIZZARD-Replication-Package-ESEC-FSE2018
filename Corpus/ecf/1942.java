/****************************************************************************
 * Copyright (c) 2004, 2010 Composent, Inc., Franky Brandelance and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. and Franky Brandelance - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.remoteservice;

import org.eclipse.ecf.core.identity.ID;

/**
 * Interface providing the ability to add authorization on a remote service method call.
 * @since 6.0
 */
public interface IRemoteServiceCallPolicy {

    /**
	 * The following method is to be called before the remote service method call to check 
	 * if the remote service method call is authorized.  Providers supporting this API 
	 * should call any available implementing instance prior to actually
	 * invoking the given IRemoteCall.
	 * 
	 * @param fromID container ID of the remote caller.  
	 *        May be <code>null</code>.
	 * @param registration Remote service registration associated 
	 *        with the given remoteCall.  Will not be <code>null</code>.
	 * @param remoteCall Remote method call to invoke locally (assuming this 
	 *        check passes).  Will not be <code>null</code>.
	 * @throws SecurityException if remoteCall is not authorized for the 
	 *         given caller fromID
	 */
    public void checkRemoteCall(ID fromID, IRemoteServiceRegistration registration, IRemoteCall remoteCall) throws SecurityException;
}
