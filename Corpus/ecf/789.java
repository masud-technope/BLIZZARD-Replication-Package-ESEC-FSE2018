/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.identity;

import java.net.URI;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.IServiceInfo;

/**
 * Service identity type. ServiceIDs are IDs that uniquely identify a remote
 * service. Subclasses may be created as appropriate.
 */
public class ServiceID extends BaseID implements IServiceID {

    private static final long serialVersionUID = 4362768703249025783L;

    /**
	 * @since 3.0
	 */
    protected IServiceInfo serviceInfo;

    protected IServiceTypeID type;

    /**
	 * @since 3.0
	 */
    protected URI location;

    /**
	 * @param namespace
	 *            namespace should not be <code>null</code>
	 * @param type
	 *            service type ID should not be <code>null</code>
	 * @param anURI
	 *            uri for service location should not be <code>null</code>
	 * @since 3.0
	 */
    protected  ServiceID(Namespace namespace, IServiceTypeID type, URI anURI) {
        super(namespace);
        Assert.isNotNull(type);
        Assert.isNotNull(anURI);
        this.type = type;
        this.location = anURI;
    }

    protected String getFullyQualifiedName() {
        //$NON-NLS-1$
        return type.getName() + "@" + location;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.BaseID#namespaceCompareTo(org.eclipse.ecf
	 * .core.identity.BaseID)
	 */
    protected int namespaceCompareTo(BaseID o) {
        if (o instanceof ServiceID) {
            final ServiceID other = (ServiceID) o;
            final String typename = other.getFullyQualifiedName();
            return getFullyQualifiedName().compareTo(typename);
        }
        return 1;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.BaseID#namespaceEquals(org.eclipse.ecf.
	 * core.identity.BaseID)
	 */
    protected boolean namespaceEquals(BaseID o) {
        if (o == null)
            return false;
        if (o instanceof ServiceID) {
            final ServiceID other = (ServiceID) o;
            if (other.getName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceGetName()
	 */
    protected String namespaceGetName() {
        return getFullyQualifiedName();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceHashCode()
	 */
    protected int namespaceHashCode() {
        return getFullyQualifiedName().hashCode();
    }

    /**
	 * Get service type for this ID.
	 * 
	 * @return String service type. Will not be <code>null</code>.
	 */
    public String getServiceType() {
        return type.getName();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.identity.IServiceID#getServiceTypeID()
	 */
    public IServiceTypeID getServiceTypeID() {
        return type;
    }

    /**
	 * Get service name for this ID.
	 * 
	 * @return String service name. May be <code>null</code>.
	 */
    public String getServiceName() {
        return serviceInfo.getServiceName();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("ServiceID[");
        //$NON-NLS-1$ //$NON-NLS-2$
        buf.append("type=").append(type).append(";location=").append(getLocation()).append(";full=" + getFullyQualifiedName()).append(//$NON-NLS-1$ //$NON-NLS-2$
        "]");
        return buf.toString();
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.identity.IServiceID#getLocation()
	 * @since 3.0
	 */
    public URI getLocation() {
        return location;
    }

    /**
	 * @return the serviceInfo
	 * @since 3.0
	 */
    public IServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    /**
	 * @param serviceInfo
	 *            the serviceInfo to set
	 * @since 3.0
	 */
    public void setServiceInfo(IServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
	 * @see java.lang.Object#hashCode()
	 * 
	 * @since 3.0
	 */
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * @since 3.0
	 */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceID other = (ServiceID) obj;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
