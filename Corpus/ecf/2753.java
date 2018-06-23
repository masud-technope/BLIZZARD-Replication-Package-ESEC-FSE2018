/*******************************************************************************
* Copyright (c) 2009 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.remoteservice.client;

/**
 * Callables represent a remotely callable method.  Clients can register
 * callables (via {@link IRemoteServiceClientContainerAdapter#registerCallables(String[], IRemoteCallable[][], java.util.Dictionary)} or
 * {@link IRemoteServiceClientContainerAdapter#registerCallables(IRemoteCallable[], java.util.Dictionary)} and then at runtime
 * when actual remote calls are attempted, the associated callable is looked up in the 
 * {@link RemoteServiceClientRegistry}.  If present, the remote call can be completed, if not present in the
 * registry, the call is not completed.
 * 
 * @since 4.0
 */
public interface IRemoteCallable {

    /**
	 * Get the method name associated with this remote callable.
	 * @return String method name.  Must not be <code>null</code>.
	 */
    public String getMethod();

    /**
	 * Get the resource path associated with this remote callable.
	 * @return String the resource path for this remote callable.  Must not be <code>null</code>.
	 */
    public String getResourcePath();

    /**
	 * Get request type associated with this remote callable. 
	 * @return IRemoteCallableRequestType for this callable.  May be <code>null</code>.
	 */
    public IRemoteCallableRequestType getRequestType();

    /**
	 * Get default remote call parameters for this remote callable.
	 * @return IRemoteCallParameter[] array of default parameters for this remote callable. 
	 * May be <code>null</code>.
	 */
    public IRemoteCallParameter[] getDefaultParameters();

    /**
	 * Get default timeout for this remote callable.
	 * @return long default timeout value.  
	 */
    public long getDefaultTimeout();
}
