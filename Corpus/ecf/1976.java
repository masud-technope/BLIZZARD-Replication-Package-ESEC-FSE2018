/*******************************************************************************
 * Copyright (c) 2008 Versant Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe (mkuppe <at> versant <dot> com) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.jslp;

import java.io.UnsupportedEncodingException;
import java.util.*;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.util.Base64;
import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.core.util.Trace;
import org.eclipse.ecf.discovery.*;
import org.eclipse.ecf.discovery.identity.IServiceID;

/**
 * Adapts SLP's service properties to ECF's ServiceProperties and vice versa
 * @see "http://www.ietf.org/rfc/rfc2608.txt page. 10ff"
 */
public class ServicePropertiesAdapter {

    //$NON-NLS-1$
    private static final String ENCODING = "ascii";

    // http://www.iana.org/assignments/enterprise-numbers
    //$NON-NLS-1$
    private static final String ECLIPSE_ENTERPRISE_NUMBER = "28392";

    /**
	 * SLP attribute key for org.eclipse.ecf.discovery.identity.IServiceID.getServiceName()
	 */
    //$NON-NLS-1$ //$NON-NLS-2$
    private static final String SERVICE_ID_NAME = "x-" + ECLIPSE_ENTERPRISE_NUMBER + "-SERVICEIDNAME";

    /**
	 * SLP attribute key for org.eclipse.ecf.discovery.IServiceInfo.getPriority()
	 */
    //$NON-NLS-1$ //$NON-NLS-2$
    private static final String PRIORITY = "x-" + ECLIPSE_ENTERPRISE_NUMBER + "-PRIORITY";

    /**
	 * SLP attribute key for org.eclipse.ecf.discovery.IServiceInfo.getWeight()
	 */
    //$NON-NLS-1$ //$NON-NLS-2$
    private static final String WEIGHT = "x-" + ECLIPSE_ENTERPRISE_NUMBER + "-WEIGHT";

    //$NON-NLS-1$
    private static final String SLP_BYTE_PREFIX = "\\FF";

    private final IServiceProperties serviceProperties;

    private String serviceName;

    private int priority = ServiceInfo.DEFAULT_PRIORITY;

    private int weight = ServiceInfo.DEFAULT_WEIGHT;

    public  ServicePropertiesAdapter(final List aList) {
        Assert.isNotNull(aList);
        serviceProperties = new ServiceProperties();
        for (final Iterator itr = aList.iterator(); itr.hasNext(); ) {
            //$NON-NLS-1$
            final String[] str = StringUtils.split((String) itr.next(), "=");
            if (str.length != 2) {
                //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                Trace.trace(Activator.PLUGIN_ID, "/debug/methods/tracing", this.getClass(), "ServicePropertiesAdapter(List)", "Skipped broken service attribute " + str);
                continue;
            }
            // remove the brackets "( )" from the string value which are added by jSLP for the LDAP style string representation
            final String key = str[0].substring(1);
            final String value = str[1].substring(0, str[1].length() - 1);
            // keep this for wire backward compatibility 
            if (key.equalsIgnoreCase(SERVICE_ID_NAME) && !value.startsWith(SLP_BYTE_PREFIX)) {
                serviceName = value;
            } else if (key.equalsIgnoreCase(PRIORITY)) {
                priority = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase(WEIGHT)) {
                weight = Integer.parseInt(value);
            } else if (value.startsWith(SLP_BYTE_PREFIX)) {
                final String[] strs = StringUtils.split(value.substring(4), "\\");
                final byte[] b = new byte[strs.length];
                for (int i = 0; i < strs.length; i++) {
                    final byte parseInt = (byte) Integer.parseInt(strs[i], 16);
                    b[i] = parseInt;
                }
                if (key.equalsIgnoreCase(SERVICE_ID_NAME)) {
                    try {
                        serviceName = new String(Base64.decodeFromCharArray(b), ENCODING);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    serviceProperties.setPropertyBytes(key, Base64.decodeFromCharArray(b));
                }
            } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                serviceProperties.setProperty(key, Boolean.valueOf(value));
            } else if (isInteger(value)) {
                serviceProperties.setProperty(key, Integer.valueOf(value));
            } else {
                serviceProperties.setProperty(key, value);
            }
        }
    }

    public  ServicePropertiesAdapter(final IServiceInfo sInfo) {
        Assert.isNotNull(sInfo);
        final IServiceID sID = sInfo.getServiceID();
        Assert.isNotNull(sID);
        final IServiceProperties sp = sInfo.getServiceProperties();
        Assert.isNotNull(sp);
        serviceProperties = new ServiceProperties(sp);
        final int sPrio = sInfo.getPriority();
        if (sPrio >= 0) {
            priority = sPrio;
            serviceProperties.setPropertyString(PRIORITY, Integer.toString(sPrio));
        }
        final int sWeight = sInfo.getWeight();
        if (sWeight >= 0) {
            weight = sWeight;
            serviceProperties.setPropertyString(WEIGHT, Integer.toString(sWeight));
        }
        final String sName = sID.getServiceName();
        if (sName != null) {
            serviceName = sName;
            try {
                serviceProperties.setPropertyBytes(SERVICE_ID_NAME, sName.getBytes(ENCODING));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isInteger(final String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public IServiceProperties toServiceProperties() {
        return serviceProperties;
    }

    public Dictionary toProperties() {
        final Dictionary dict = new Properties();
        final Enumeration propertyNames = serviceProperties.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            final String key = (String) propertyNames.nextElement();
            final byte[] propertyBytes = serviceProperties.getPropertyBytes(key);
            if (propertyBytes != null) {
                final byte[] encode = Base64.encodeToCharArray(propertyBytes);
                final StringBuffer buf = new StringBuffer();
                buf.append(SLP_BYTE_PREFIX);
                for (int i = 0; i < encode.length; i++) {
                    buf.append('\\');
                    buf.append(Integer.toHexString(encode[i]));
                }
                dict.put(key, buf.toString());
            } else {
                dict.put(key, serviceProperties.getProperty(key).toString());
            }
        }
        return dict;
    }

    public int getWeight() {
        return weight;
    }

    public int getPriority() {
        return priority;
    }

    public String getServiceName() {
        return serviceName;
    }
}
