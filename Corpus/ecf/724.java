/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice;

import java.util.Dictionary;
import org.osgi.framework.ServiceReference;

/**
 * @since 5.0
 */
public interface IOSGiRemoteServiceContainerAdapter extends IRemoteServiceContainerAdapter {

    /**
	 * Register a new remote service. This method is to be called by the service
	 * server...i.e. the client that wishes to make available a service to other
	 * client within this container.
	 * 
	 * @param clazzes
	 *            The class names under which the service will be remoted. 
	 *            The array must match or be a subset of the service's 
	 *            properties under the key Constants.OBJECTCLASS.
	 *            Must not be <code>null</code> and must not be an
	 *            empty array.
	 * @param aServiceReference
	 *            a <code>ServiceRefenrence</code>. This object must
	 *            <ul>
	 *            <li>not be <code>null</code></li>
	 *            </ul>
	 * @param properties
	 *            to be associated with the service reference (replaces 
	 *            servicereference's properties)
	 * @return IRemoteServiceRegistration the service registration. Will not
	 *         return <code>null</code> .
	 */
    public IRemoteServiceRegistration registerRemoteService(String[] clazzes, ServiceReference aServiceReference, Dictionary properties);
}
