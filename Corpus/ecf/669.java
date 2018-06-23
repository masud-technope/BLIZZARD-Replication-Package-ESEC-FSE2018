/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis and Jeff McAffer - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.ecf.server;

/**
 * Service Client Manager
 * @since 2.0
 */
public interface IServiceClientManager {

    /**
	 * Get an IServiceClient by it's String id.
	 * @param id the String id to use to lookup the IServiceClient.  If <code>null</code>, then <code>null</code> 
	 * will be returned.
	 * @return IServiceClient corresponding to given id.  If not found then <code>null</code> will be returned.
	 */
    public IServiceClient lookupClient(String id);
}
