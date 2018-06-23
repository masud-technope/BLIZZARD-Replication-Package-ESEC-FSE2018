/*******************************************************************************
 * Copyright (c) 2007 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery.identity;

import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.core.util.StringUtils;

/**
 * ServiceTypeID base class.
 */
public class ServiceTypeID extends BaseID implements IServiceTypeID {

    private static final long serialVersionUID = 2546630451825262145L;

    //$NON-NLS-1$
    protected static final String DELIM = "._";

    //$NON-NLS-1$
    protected String typeName = "";

    protected String namingAuthority;

    protected String[] protocols;

    protected String[] scopes;

    protected String[] services;

    protected  ServiceTypeID(Namespace namespace) {
        super(namespace);
    }

    protected  ServiceTypeID(Namespace namespace, String[] services, String[] scopes, String[] protocols, String namingAuthority) {
        super(namespace);
        Assert.isNotNull(services);
        this.services = services;
        Assert.isNotNull(scopes);
        this.scopes = scopes;
        Assert.isNotNull(protocols);
        this.protocols = protocols;
        Assert.isNotNull(namingAuthority);
        this.namingAuthority = namingAuthority;
        createType();
        Assert.isNotNull(typeName);
    }

    protected  ServiceTypeID(Namespace ns, IServiceTypeID id) {
        this(ns, id.getServices(), id.getScopes(), id.getProtocols(), id.getNamingAuthority());
    }

    /**
	 * Clients should not call this method directly. Use the {@link Namespace}
	 * and/or {@link ServiceIDFactory} instead.
	 * 
	 * @param namespace
	 *            namespace should not be <code>null</code>
	 * @param aType
	 *            type should not be <code>null</code>
	 */
    public  ServiceTypeID(Namespace namespace, String aType) {
        this(namespace);
        if (aType != null) {
            try {
                // sanitize (remove the leading _, dangling . or white spaces
                aType = aType.trim();
                if (//$NON-NLS-1$
                aType.endsWith(".")) {
                    aType = aType.substring(0, aType.length() - 1);
                }
                // attach naming authority to simplify parsing if not present
                int lastDot = aType.lastIndexOf('.');
                int lastUnderscore = aType.lastIndexOf('_');
                if (lastDot + 1 != lastUnderscore) {
                    aType = //$NON-NLS-1$
                    aType + "._" + //$NON-NLS-1$
                    DEFAULT_NA;
                }
                String type = aType.substring(1);
                String[] split = //$NON-NLS-1$
                StringUtils.split(//$NON-NLS-1$
                type, //$NON-NLS-1$
                "._");
                // naming authority
                int offset = split.length - 1;
                this.namingAuthority = split[offset];
                // protocol and scope
                String string = split[--offset];
                //$NON-NLS-1$ //$NON-NLS-2$
                String[] protoAndScope = StringUtils.split(string, ".", string.indexOf(".") - 1);
                this.protocols = new String[] { protoAndScope[0] };
                this.scopes = new String[] { protoAndScope[1] };
                // services are the remaining strings in the array
                List subList = Arrays.asList(split).subList(0, offset);
                this.services = (String[]) subList.toArray(new String[0]);
                createType();
                Assert.isTrue(aType.equals(typeName));
            } catch (Exception e) {
                throw new IDCreateException("service type not parseable", e);
            }
        } else {
            //$NON-NLS-1$
            throw new IDCreateException("service type cannot be null");
        }
    }

    protected void createType() {
        final StringBuffer buf = new StringBuffer();
        // services
        //$NON-NLS-1$
        buf.append("_");
        for (int i = 0; i < services.length; i++) {
            buf.append(services[i]);
            buf.append(DELIM);
        }
        // protocols
        for (int i = 0; i < protocols.length; i++) {
            buf.append(protocols[i]);
            if (i != protocols.length - 1) {
                buf.append(DELIM);
            } else {
                //$NON-NLS-1$
                buf.append(//$NON-NLS-1$
                ".");
            }
        }
        // scope
        for (int i = 0; i < scopes.length; i++) {
            buf.append(scopes[i]);
            buf.append(DELIM);
        }
        // naming authority
        buf.append(namingAuthority);
        typeName = buf.toString();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#getName()
	 */
    public String getName() {
        return typeName;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.BaseID#namespaceCompareTo(org.eclipse.ecf
	 * .core.identity.BaseID)
	 */
    protected int namespaceCompareTo(BaseID o) {
        if (o instanceof ServiceTypeID) {
            final ServiceTypeID other = (ServiceTypeID) o;
            final String typename = other.getName();
            return getName().compareTo(typename);
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
        if (o instanceof ServiceTypeID) {
            final ServiceTypeID other = (ServiceTypeID) o;
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
        return typeName;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceHashCode()
	 */
    protected int namespaceHashCode() {
        return getName().hashCode();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        final StringBuffer buf = new StringBuffer("ServiceTypeID[");
        //$NON-NLS-1$//$NON-NLS-2$
        buf.append("typeName=").append(typeName).append("]");
        return buf.toString();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.identity.IServiceTypeID#getNamingAuthority()
	 */
    public String getNamingAuthority() {
        return namingAuthority;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.identity.IServiceTypeID#getProtocols()
	 */
    public String[] getProtocols() {
        return protocols;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.identity.IServiceTypeID#getScopes()
	 */
    public String[] getScopes() {
        return scopes;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.identity.IServiceTypeID#getServices()
	 */
    public String[] getServices() {
        return services;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof IServiceTypeID)) {
            return false;
        }
        final IServiceTypeID stid = (ServiceTypeID) o;
        return stid.getName().equals(getName());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#hashCode()
	 */
    public int hashCode() {
        return getName().hashCode();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.identity.IServiceTypeID#getInternal()
	 */
    public String getInternal() {
        return typeName;
    }
}
