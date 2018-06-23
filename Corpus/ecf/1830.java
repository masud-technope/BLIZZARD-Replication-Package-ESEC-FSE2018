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
package org.eclipse.ecf.provider.zookeeper.core;

import java.util.UUID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceTypeID;

public class ZooDiscoveryServiceTypeID extends ServiceTypeID {

    private static final long serialVersionUID = 9063908479280524897L;

    private String id;

    public  ZooDiscoveryServiceTypeID(ZooDiscoveryNamespace discoveryNamespace, IServiceTypeID typeId) {
        super(discoveryNamespace, typeId);
        this.id = UUID.randomUUID().toString();
    }

    public  ZooDiscoveryServiceTypeID(ZooDiscoveryNamespace discoveryNamespace, IServiceTypeID typeId, String internal) {
        super(discoveryNamespace, typeId);
        this.id = internal;
    }

    public String getInternal() {
        return this.id;
    }
}
