/****************************************************************************
 * Copyright (c) 2005, 2010 Jan S. Rellermeyer, Systems Group,
 * Department of Computer Science, ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jan S. Rellermeyer - initial API and implementation
 *    Markus Alexander Kuppe - enhancements and bug fixes
 *
*****************************************************************************/
package ch.ethz.iks.slp;

import java.io.Serializable;

/**
 * Implementation of the SLP ServiceType class defined in RFC 2614.
 * 
 * @author Jan S. Rellermeyer, Systems Group, ETH Zurich
 * @since 1.0
 */
public final class ServiceType implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2821061127250972623L;

    /**
	 * the type.
	 */
    private String type = new String();

    /**
	 * is it abstract ?
	 */
    private final boolean isAbstract;

    /**
	 * the concrete type.
	 */
    private final String concreteType;

    /**
	 * the principle type.
	 */
    private final String principleType;

    /**
	 * the abstract type.
	 */
    private final String abstractType;

    /**
	 * the naming authority.
	 */
    private final String namingAuthority;

    /**
	 * creates a new ServiceType instance.
	 * 
	 * @param serviceType
	 *            the string representation of a ServiceType, e.g.
	 * 
	 *            <pre>
	 *      service:osgi:remote
	 * </pre>
	 */
    public  ServiceType(final String serviceType) {
        type = serviceType;
        if (!type.startsWith("service:")) {
            throw new IllegalArgumentException("Invalid service type: " + serviceType);
        }
        final int principleStart = 8;
        final int principleEnd = type.indexOf(":", principleStart);
        if (principleEnd != -1) {
            isAbstract = true;
            principleType = type.substring(principleStart, principleEnd);
            abstractType = type.substring(0, principleEnd);
            concreteType = type.substring(principleEnd + 1);
        } else {
            isAbstract = false;
            principleType = type.substring(principleStart);
            abstractType = "";
            concreteType = "";
        }
        final int namingStart = type.indexOf(".") + 1;
        if (namingStart != 0) {
            final int namingEnd = type.indexOf(":", namingStart);
            String na = "";
            if (namingEnd == -1) {
                na = type.substring(namingStart);
            } else {
                na = type.substring(namingStart, namingEnd);
            }
            // 1954772: isNADefault returns false for "IANA"
            if ("IANA".equalsIgnoreCase(na)) {
                namingAuthority = "";
                // remove "iana" from type so toString() is consistent
                type = type.substring(0, namingStart - 1) + type.substring(namingStart + 4, type.length());
            } else {
                namingAuthority = na;
            }
        } else {
            namingAuthority = "";
        }
    }

    /**
	 * Always returns true as invalid service types cannot be created any longer.
	 * 
	 * @return always true.
	 */
    public boolean isServiceURL() {
        //Remove when moved to next major
        return true;
    }

    /**
	 * is the ServiceType instance an abstract type ?
	 * 
	 * @return true if thie is the case.
	 */
    public boolean isAbstractType() {
        return isAbstract;
    }

    /**
	 * is the naming authority default (IANA) ?
	 * 
	 * @return true if this is the case.
	 */
    public boolean isNADefault() {
        return "".equals(namingAuthority);
    }

    /**
	 * get the concrete type part of this ServiceType instance.
	 * 
	 * @return a String representing the concrete type.
	 */
    public String getConcreteTypeName() {
        return concreteType;
    }

    /**
	 * get the principle type part of this ServiceType instance.
	 * 
	 * @return a String representing the principle part.
	 */
    public String getPrincipleTypeName() {
        return principleType;
    }

    /**
	 * get the name of the abstract type of this ServiceType instance.
	 * 
	 * @return a String representing the abstract type.
	 */
    public String getAbstractTypeName() {
        return abstractType;
    }

    /**
	 * get the naming authority.
	 * 
	 * @return the naming authority.
	 */
    public String getNamingAuthority() {
        return namingAuthority;
    }

    /**
	 * check if two ServiceTypes are equal.
	 * 
	 * @param obj
	 *            another ServiceType.
	 * @return true if they equal.
	 */
    public boolean equals(final Object obj) {
        if (!(obj instanceof ServiceType)) {
            return false;
        }
        final ServiceType t = (ServiceType) obj;
        return (isAbstract == t.isAbstract && concreteType.equals(t.concreteType) && principleType.equals(t.principleType) && abstractType.equals(t.abstractType) && namingAuthority.equals(t.namingAuthority));
    }

    /**
	 * check if a ServiceType matches a ServiceURL or another ServiceType.
	 * 
	 * @param obj
	 *            the object to be compared to.
	 * @return true if this type matches the other object.
	 */
    public boolean matches(final Object obj) {
        if (!(obj instanceof ServiceType)) {
            return false;
        }
        final ServiceType t = (ServiceType) obj;
        return isAbstract ? equals(t) || t.toString().equals(getAbstractTypeName()) : equals(t);
    }

    /**
	 * get a String representation of this ServiceType instance.
	 * 
	 * @return the String representation.
	 */
    public String toString() {
        return type;
    }

    /**
	 * get the hashCode of this ServiceType instance.
	 * 
	 * @return the int value of the hashCode.
	 */
    public int hashCode() {
        return (concreteType.hashCode()) ^ (principleType.hashCode() << 24) ^ (abstractType.hashCode() << 16) ^ (namingAuthority.hashCode() << 8);
    }
}
