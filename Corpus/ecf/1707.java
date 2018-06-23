/*******************************************************************************
 *  Copyright (c)2010 REMAIN B.V. The Netherlands. (http://www.remainsoftware.com).
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Ahmed Aadel - initial API and implementation     
 *******************************************************************************/
package org.eclipse.ecf.provider.zookeeper.core;

import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.provider.zookeeper.core.internal.Configurator;

public class ZooDiscoveryTargetID extends BaseID {

    private static final long serialVersionUID = 8563343828552001351L;

    private String configString = "";

    public  ZooDiscoveryTargetID(ZooDiscoveryNamespace zkDiscoveryNamespace, String[] parameters) {
        super(zkDiscoveryNamespace);
        Configurator.validateFlavor(parameters[0]);
        for (String s : parameters) {
            this.configString += s + ",";
        }
        this.configString = this.configString.substring(0, this.configString.length() - 1);
    }

    protected int namespaceCompareTo(BaseID o) {
        return getName().compareTo(o.getName());
    }

    protected boolean namespaceEquals(BaseID o) {
        if (!(o instanceof ZooDiscoveryTargetID))
            return false;
        return this.configString.equals(o.getName());
    }

    protected String namespaceGetName() {
        return this.configString;
    }

    protected int namespaceHashCode() {
        return this.configString.hashCode() ^ getClass().hashCode();
    }
}
