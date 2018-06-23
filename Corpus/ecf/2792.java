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
import java.util.Map;
import java.util.UUID;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.SystemLogService;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.discovery.ServiceInfo;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceIDFactory;
import org.eclipse.ecf.provider.zookeeper.core.internal.IService;
import org.eclipse.ecf.provider.zookeeper.core.internal.Localizer;
import org.eclipse.ecf.provider.zookeeper.core.internal.Notification;
import org.eclipse.ecf.provider.zookeeper.node.internal.INode;
import org.eclipse.ecf.provider.zookeeper.util.Geo;
import org.eclipse.ecf.provider.zookeeper.util.Logger;
import org.eclipse.ecf.provider.zookeeper.util.PrettyPrinter;

public class DiscoverdService extends ServiceInfo implements IService, INode {

    private static final long serialVersionUID = 3072424087109599612L;

    private String uuid;

    private URI location;

    private IServiceTypeID serviceTypeID;

    public  DiscoverdService(String path, Map<String, Object> serviceData) {
        Assert.isNotNull(serviceData);
        this.uuid = path.split(INode._URI_)[0];
        this.location = (URI) serviceData.remove(IService.LOCATION);
        super.priority = (Integer) serviceData.remove(IService.PRIORITY);
        super.weight = (Integer) serviceData.remove(IService.WEIGHT);
        super.serviceName = (String) serviceData.get(IService.SERVICE_NAME);
        super.properties = (IServiceProperties) serviceData.remove(INode.NODE_SERVICE_PROPERTIES);
        String[] services = (String[]) serviceData.remove(INode.NODE_PROPERTY_SERVICES);
        String na = (String) serviceData.remove(INode.NODE_PROPERTY_NAME_NA);
        String[] protocols = (String[]) serviceData.remove(INode.NODE_PROPERTY_NAME_PROTOCOLS);
        String[] scopes = (String[]) serviceData.remove(INode.NODE_PROPERTY_NAME_SCOPE);
        this.serviceTypeID = ServiceIDFactory.getDefault().createServiceTypeID(ZooDiscoveryContainer.getSingleton().getConnectNamespace(), services, scopes, protocols, na);
        super.serviceID = new ZooDiscoveryServiceID(ZooDiscoveryContainer.getSingleton().getConnectNamespace(), this, serviceTypeID, this.location);
    }

    public void dispose() {
        Logger.log(SystemLogService.LOG_DEBUG, PrettyPrinter.prompt(PrettyPrinter.REMOTE_UNAVAILABLE, this), null);
        Localizer.getSingleton().localize(new Notification(this, Notification.UNAVAILABLE));
    }

    public String getNodeId() {
        return this.uuid;
    }

    public void regenerateNodeId() {
        this.uuid = UUID.randomUUID().toString();
    }

    public String getName() {
        return this.uuid;
    }

    public Namespace getNamespace() {
        return ZooDiscoveryContainer.getSingleton().getConnectNamespace();
    }

    public String toExternalForm() {
        return this.uuid;
    }

    public int compareTo(Object o) {
        //$NON-NLS-1$
        Assert.isTrue(o != null && o instanceof DiscoverdService, "incompatible types for compare");
        return this.getServiceID().getName().compareTo(((DiscoverdService) o).getServiceID().getName());
    }

    public byte[] getPropertiesAsBytes() {
        throw new UnsupportedOperationException();
    }

    public String getPath() {
        return getServiceID().getName() + INode._URI_ + getLocation();
    }

    public String getAbsolutePath() {
        return INode.ROOT_SLASH + getPath();
    }

    public boolean isLocalNode() {
        return Geo.isLocal(getAbsolutePath());
    }

    public IService getWrappedService() {
        return this;
    }
}
