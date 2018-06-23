/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *    Wim Jongman - initial API and implementation 
 *    Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core.internal;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceInfo;
import org.eclipse.ecf.discovery.IServiceTypeEvent;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.provider.zookeeper.core.ZooDiscoveryContainer;

public class Notification implements IServiceEvent, IServiceTypeEvent {

    /**
	 * Notification indicating that a service has been discovered.
	 * <p>
	 * The value of <code>AVAILABLE</code> is 0x00000001.
	 */
    public static final int AVAILABLE = 0x00000001;

    /**
	 * Notification indicating that a previously discovered service is no longer
	 * known to ZooDiscovery.
	 * <p>
	 * The value of <code>UNAVAILABLE</code> is 0x00000002.
	 */
    public static final int UNAVAILABLE = 0x00000002;

    private int type;

    private IServiceInfo discoverdService;

    public  Notification(IServiceInfo discoverdService, int type) {
        this.discoverdService = discoverdService;
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public IServiceInfo getAdvertisedService() {
        return discoverdService;
    }

    public IServiceInfo getServiceInfo() {
        return discoverdService;
    }

    public ID getLocalContainerID() {
        return ZooDiscoveryContainer.getSingleton().getID();
    }

    public IServiceTypeID getServiceTypeID() {
        return discoverdService.getServiceID().getServiceTypeID();
    }
}
