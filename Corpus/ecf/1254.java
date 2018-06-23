/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

/**
 * Entry point discovery container adapter. This interface exposes the ability
 * to add/remove listeners for newly discovered services and service types,
 * register and unregister locally provided services, and get (synch) and
 * request (asynchronous) service info from a remote service provider.
 * <p>
 * This interface can be used by container provider implementations as an
 * adapter so that calls to
 * IContainer.getAdapter(IDiscoveryContainerAdapter.class) will return a
 * non-null instance of a class that implements this interface. Clients can then
 * proceed to use this interface to interact with the given discovery
 * implementation.
 * 
 * @deprecated Use IDisocveryLocator and IDisocveryAdvertiser instead
 */
public interface IDiscoveryContainerAdapter extends IDiscoveryLocator, IDiscoveryAdvertiser {
}
