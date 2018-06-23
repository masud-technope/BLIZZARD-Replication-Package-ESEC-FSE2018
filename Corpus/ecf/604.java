/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Wim Jongman - initial API and implementation 
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

public interface IService extends IServiceInfo {

    /**
	 * Holds the service location ( {@link IServiceInfo#getLocation()} ) in the
	 * zooKeeper node
	 **/
    //$NON-NLS-1$ 
    String LOCATION = "discovery.service.location";

    /**
	 * Holds the service weight ( {@link IServiceInfo#getWeight()} ) in the
	 * zooKeeper node
	 **/
    //$NON-NLS-1$
    String WEIGHT = "discovery.service.weight";

    /**
	 * Holds the service priority ({@link IServiceInfo#getPriority()()} ) in the
	 * zooKeeper node
	 **/
    //$NON-NLS-1$
    String PRIORITY = "discovery.service.priority";

    /**
	 * Holds the service-type protocols ({@link IServiceTypeID#getProtocols()} )
	 * in the zooKeeper node
	 **/
    //$NON-NLS-1$
    String PROTOCOLS = "discovery.service.protocol";

    /**
	 * Holds the service name ({@link IServiceInfo#getServiceName()}) in the
	 * zooKeeper node
	 **/
    //$NON-NLS-1$
    String SERVICE_NAME = "discovery.service.name";

    /**
	 * The byte representation of the service properties, appropriate to be
	 * stored in the zooKeeper node
	 * 
	 * @return byte representation of the properties
	 */
    byte[] getPropertiesAsBytes();
}
