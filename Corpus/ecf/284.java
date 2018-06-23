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
package org.eclipse.ecf.provider.zookeeper.core;

import java.net.URI;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceID;

public class ZooDiscoveryServiceID extends ServiceID {

    private static final long serialVersionUID = 1185925207835288995L;

    protected  ZooDiscoveryServiceID(Namespace namespace, IServiceTypeID type, URI anURI) {
        super(namespace, type, anURI);
    }

    public  ZooDiscoveryServiceID(Namespace namespace, DiscoverdService discoverdService, IServiceTypeID type, URI anURI) {
        this(namespace, type, anURI);
        setServiceInfo(discoverdService);
    }
}
