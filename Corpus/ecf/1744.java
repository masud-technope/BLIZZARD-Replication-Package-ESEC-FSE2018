/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

import java.io.Serializable;
import java.net.URI;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.*;
import org.eclipse.ecf.internal.discovery.DiscoveryPlugin;

/**
 * Base implementation of {@link IServiceInfo}. Subclasses may be created as
 * appropriate.
 */
public class ServiceInfo implements IServiceInfo, Serializable {

    private static final long serialVersionUID = -5651115550295457142L;

    // 1h
    public static final long DEFAULT_TTL = 3600;

    public static final int DEFAULT_PRIORITY = 0;

    public static final int DEFAULT_WEIGHT = 0;

    //$NON-NLS-1$
    public static final String UNKNOWN_PROTOCOL = "unknown";

    /**
	 * @since 3.0
	 */
    protected String serviceName;

    protected IServiceID serviceID;

    protected int priority;

    protected int weight;

    protected IServiceProperties properties;

    /**
	 * @since 4.0
	 */
    protected long timeToLive;

    protected  ServiceInfo() {
    // null constructor for subclasses
    }

    /**
	 * Create an IServiceInfo instance.
	 * 
	 * @param anURI
	 *            The (absolute) location of the service.
	 * @param aServiceName
	 *            a user chosen service name. Only ASCII characters are allowed.
	 * @param aServiceTypeID
	 *            the service type identifier.
	 * @since 3.0
	 */
    public  ServiceInfo(URI anURI, String aServiceName, IServiceTypeID aServiceTypeID) {
        this(anURI, aServiceName, aServiceTypeID, DEFAULT_PRIORITY, DEFAULT_WEIGHT, null);
    }

    /**
	 * Create an IServiceInfo instance.
	 * 
	 * @param anURI
	 *            The (absolute) location of the service.
	 * @param aServiceName
	 *            a user chosen service name. Only ASCII characters are allowed.
	 * @param aServiceTypeID
	 *            the service type identifier.
	 * @param props
	 *            generic service properties.
	 * @since 3.0
	 */
    public  ServiceInfo(URI anURI, String aServiceName, IServiceTypeID aServiceTypeID, IServiceProperties props) {
        this(anURI, aServiceName, aServiceTypeID, DEFAULT_PRIORITY, DEFAULT_WEIGHT, props);
    }

    /**
	 * Create an IServiceInfo instance.
	 * 
	 * @param anURI
	 *            The (absolute) location of the service.
	 * @param aServiceName
	 *            a user chosen service name. Only ASCII characters are allowed.
	 * @param aServiceTypeID
	 *            the service type identifier.
	 * @param priority
	 *            the service priority. The priority of this target host. A
	 *            client MUST attempt to contact the target host with the
	 *            lowest-numbered priority it can reach; target hosts with the
	 *            same priority SHOULD be tried in an order defined by the
	 *            weight field.
	 *
	 * @param weight
	 *            the service weight. A server selection mechanism. The weight
	 *            field specifies a relative weight for entries with the same
	 *            priority. Larger weights SHOULD be given a proportionately
	 *            higher probability of being selected. Domain administrators
	 *            SHOULD use Weight 0 when there isn't any server selection to
	 *            do. In the presence of records containing weights greater than
	 *            0, records with weight 0 should have a very small chance of
	 *            being selected.
	 * @param props
	 *            generic service properties.
	 * @since 3.0
	 */
    public  ServiceInfo(URI anURI, String aServiceName, IServiceTypeID aServiceTypeID, int priority, int weight, IServiceProperties props) {
        this(anURI, aServiceName, aServiceTypeID, priority, weight, props, DEFAULT_TTL);
    }

    /**
	 * Create an IServiceInfo instance.
	 * 
	 * @param anURI
	 *            The (absolute) location of the service.
	 * @param aServiceName
	 *            a user chosen service name. Only ASCII characters are allowed.
	 * @param aServiceTypeID
	 *            the service type identifier.
	 * @param priority
	 *            the service priority. The priority of this target host. A
	 *            client MUST attempt to contact the target host with the
	 *            lowest-numbered priority it can reach; target hosts with the
	 *            same priority SHOULD be tried in an order defined by the
	 *            weight field.
	 *
	 * @param weight
	 *            the service weight. A server selection mechanism. The weight
	 *            field specifies a relative weight for entries with the same
	 *            priority. Larger weights SHOULD be given a proportionately
	 *            higher probability of being selected. Domain administrators
	 *            SHOULD use Weight 0 when there isn't any server selection to
	 *            do. In the presence of records containing weights greater than
	 *            0, records with weight 0 should have a very small chance of
	 *            being selected.
	 * @param props
	 *            generic service properties.
	 * @param ttl
	 *            time to live
	 * 
	 * @since 4.0
	 */
    public  ServiceInfo(URI anURI, String aServiceName, IServiceTypeID aServiceTypeID, int priority, int weight, IServiceProperties props, long ttl) {
        Assert.isNotNull(anURI);
        Assert.isNotNull(aServiceName);
        Assert.isNotNull(aServiceTypeID);
        // Assert.isLegal(anURI.isOpaque(), "Opaque URI is not supported");
        // Assert.isLegal(!anURI.isAbsolute(),
        // "Non absolute URI is not supported");
        // [scheme:][//authority][path][?query][#fragment]
        // Scheme -> Protocol
        String scheme = anURI.getScheme();
        if (scheme == null) {
            scheme = UNKNOWN_PROTOCOL;
        }
        // UserInfo
        String userInfo = anURI.getUserInfo();
        if (userInfo == null) {
            userInfo = "";
        } else {
            userInfo += "@";
        }
        // Host
        String host = anURI.getHost();
        Assert.isNotNull(host);
        // Port
        int port = anURI.getPort();
        if (port == -1) {
            port = 0;
        }
        // Path
        String path = anURI.getPath();
        if (path == null) {
            path = "/";
        }
        // query
        String query = anURI.getQuery();
        if (query == null) {
            query = "";
        } else {
            query = "?" + query;
        }
        // fragment
        String fragment = anURI.getFragment();
        if (fragment == null) {
            fragment = "";
        } else {
            fragment = "#" + fragment;
        }
        URI uri = URI.create(scheme + "://" + userInfo + host + ":" + port + path + query + fragment);
        // service id
        Namespace ns = aServiceTypeID.getNamespace();
        this.serviceID = (IServiceID) ns.createInstance(new Object[] { aServiceTypeID, uri });
        ((ServiceID) serviceID).setServiceInfo(this);
        this.serviceName = aServiceName;
        this.weight = weight;
        this.priority = priority;
        properties = (props == null) ? new ServiceProperties() : props;
        this.timeToLive = ttl;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceInfo#getAddress()
	 */
    public URI getLocation() {
        return serviceID.getLocation();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceInfo#getServiceID()
	 */
    public IServiceID getServiceID() {
        return serviceID;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceInfo#getPriority()
	 */
    public int getPriority() {
        return priority;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceInfo#getWeight()
	 */
    public int getWeight() {
        return weight;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceInfo#getServiceProperties()
	 */
    public IServiceProperties getServiceProperties() {
        return properties;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("ServiceInfo[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("uri=").append(getLocation()).append(";id=").append(serviceID).append(//$NON-NLS-1$
        ";priority=").append(priority).append(//$NON-NLS-1$
        ";weight=").append(//$NON-NLS-1$
        weight).append(";props=").append(//$NON-NLS-1$
        properties).append(//$NON-NLS-1$
        "]");
        return buf.toString();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
    public Object getAdapter(Class adapter) {
        if (adapter.isInstance(this)) {
            return this;
        }
        final IAdapterManager adapterManager = DiscoveryPlugin.getDefault().getAdapterManager();
        if (adapterManager == null)
            return null;
        return adapterManager.loadAdapter(this, adapter.getName());
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceInfo#getServiceName()
	 * @since 3.0
	 */
    public String getServiceName() {
        return serviceName;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceInfo#getTTL()
	 * @since 4.0
	 */
    public long getTTL() {
        return timeToLive;
    }
}
