/*******************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.discovery;

import java.io.Serializable;
import java.util.*;

/**
 * Service properties implementation class for {@link IServiceProperties}.
 * Subclasses may be created as appropriate.
 */
public class ServiceProperties implements IServiceProperties {

    private static final long serialVersionUID = -1033681952881598670L;

    private final Properties props;

    public  ServiceProperties() {
        super();
        props = new Properties();
    }

    public  ServiceProperties(Properties props) {
        super();
        this.props = (props == null) ? new Properties() : props;
    }

    /**
	 * Creates a copy of the given {@link IServiceProperties}
	 * 
	 * @param sp
	 *            service properties
	 * @since 2.1
	 */
    public  ServiceProperties(IServiceProperties sp) {
        props = new Properties();
        Enumeration names = sp.getPropertyNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            Object value = sp.getProperty(key);
            props.put(key, value);
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceProperties#getPropertyNames()
	 */
    public Enumeration getPropertyNames() {
        return props.keys();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IServiceProperties#getPropertyString(java.lang
	 * .String)
	 */
    public String getPropertyString(String name) {
        final Object val = props.get(name);
        if (val instanceof String) {
            return (String) val;
        }
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IServiceProperties#getPropertyBytes(java.lang
	 * .String)
	 */
    public byte[] getPropertyBytes(String name) {
        final Object val = props.get(name);
        if (val instanceof ByteArrayWrapper) {
            ByteArrayWrapper baw = (ByteArrayWrapper) val;
            return baw.getByte();
        }
        return null;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IServiceProperties#getProperty(java.lang.String
	 * )
	 */
    public Object getProperty(String name) {
        return props.get(name);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IServiceProperties#setProperty(java.lang.String
	 * , java.lang.Object)
	 */
    public Object setProperty(String name, Object value) {
        return props.put(name, value);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IServiceProperties#setPropertyBytes(java.lang
	 * .String, byte[])
	 */
    public Object setPropertyBytes(String name, byte[] value) {
        return props.put(name, new ByteArrayWrapper(value));
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.discovery.IServiceProperties#setPropertyString(java.lang
	 * .String, java.lang.String)
	 */
    public Object setPropertyString(String name, String value) {
        return props.put(name, value);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(Object obj) {
        if (obj instanceof ServiceProperties) {
            ServiceProperties sp = (ServiceProperties) obj;
            return props.equals(sp.props);
        }
        return false;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode() {
        return props.hashCode();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer buf = new StringBuffer("ServiceProperties[");
        //$NON-NLS-1$
        buf.append(props).append("]");
        return buf.toString();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.discovery.IServiceProperties#size()
	 */
    public int size() {
        return props.size();
    }

    /**
	 * @return Properties
	 * @since 3.0
	 */
    public Properties asProperties() {
        Properties result = new Properties();
        Set keySet = props.keySet();
        for (Iterator iterator = keySet.iterator(); iterator.hasNext(); ) {
            Object key = (Object) iterator.next();
            Object value = props.get(key);
            if (value instanceof ByteArrayWrapper) {
                ByteArrayWrapper baw = (ByteArrayWrapper) value;
                result.put(key, baw.getByte());
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    // proper equals/hashcode for byte[]
    private static class ByteArrayWrapper implements Serializable {

        private static final long serialVersionUID = -8528836675536956297L;

        private final byte[] value;

        public  ByteArrayWrapper(byte[] value) {
            this.value = value;
        }

        public byte[] getByte() {
            return value;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
        public boolean equals(Object obj) {
            if (obj instanceof ByteArrayWrapper) {
                ByteArrayWrapper baw = (ByteArrayWrapper) obj;
                return Arrays.equals(value, baw.value);
            }
            return false;
        }

        /*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
        public int hashCode() {
            return value.hashCode();
        }
    }
}
