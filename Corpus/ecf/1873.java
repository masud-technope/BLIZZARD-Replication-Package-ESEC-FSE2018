/*******************************************************************************
 * Copyright (c) 2010-2011 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.remoteserviceadmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.discovery.IServiceProperties;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.DebugOptions;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.LogUtility;
import org.eclipse.ecf.internal.osgi.services.remoteserviceadmin.PropertiesUtil;
import org.osgi.framework.Version;

/**
 * Abstract superclass for metadata factories...i.e. implementers of
 * {@link IServiceInfoFactory}.
 * 
 * 
 */
public abstract class AbstractMetadataFactory {

    //$NON-NLS-1$
    protected static final String LIST_SEPARATOR = " ";

    protected void encodeString(IServiceProperties props, String name, String value) {
        props.setPropertyString(name, value);
    }

    protected String decodeString(IServiceProperties props, String name) {
        return props.getPropertyString(name);
    }

    protected void encodeLong(IServiceProperties result, String name, Long value) {
        result.setPropertyString(name, value.toString());
    }

    private final Long DEFAULT_LONG = new Long(0);

    protected Long decodeLong(IServiceProperties props, String name) {
        Object o = props.getProperty(name);
        if (o == null)
            return DEFAULT_LONG;
        if (o instanceof Long)
            return (Long) o;
        if (o instanceof Integer)
            return new Long(((Integer) o).longValue());
        if (o instanceof String)
            try {
                return new Long((String) o);
            } catch (NumberFormatException e) {
                LogUtility.logError("decodeLong", DebugOptions.METADATA_FACTORY, this.getClass(), "Exception decoding long with name=" + name + " and value=" + o);
            }
        return DEFAULT_LONG;
    }

    protected void encodeList(IServiceProperties props, String name, List<String> list) {
        if (list == null)
            return;
        if (list.size() == 1) {
            props.setPropertyString(name, list.get(0));
        } else {
            final StringBuffer result = new StringBuffer();
            for (Iterator<String> i = list.iterator(); i.hasNext(); ) {
                result.append(i.next());
                if (i.hasNext())
                    result.append(LIST_SEPARATOR);
            }
            // Now add to props
            props.setPropertyString(name, result.toString());
        }
    }

    protected List<String> decodeList(IServiceProperties props, String name) {
        String value = props.getPropertyString(name);
        if (value == null)
            return Collections.EMPTY_LIST;
        List<String> result = new ArrayList<String>();
        final StringTokenizer t = new StringTokenizer(value, LIST_SEPARATOR);
        while (t.hasMoreTokens()) result.add(t.nextToken());
        return result;
    }

    protected void decodeOSGiProperties(IServiceProperties props, Map osgiProperties) {
        // org.osgi.framework.Constants.OBJECTCLASS
        List<String> interfaces = decodeList(props, org.osgi.framework.Constants.OBJECTCLASS);
        osgiProperties.put(org.osgi.framework.Constants.OBJECTCLASS, (String[]) interfaces.toArray(new String[interfaces.size()]));
        // org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_
        for (String intf : interfaces) {
            String packageKey = org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_ + getPackageName(intf);
            String intfVersion = decodeString(props, packageKey);
            if (intfVersion != null)
                osgiProperties.put(packageKey, intfVersion);
        }
        // org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID
        String endpointId = decodeString(props, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID);
        osgiProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, endpointId);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_SERVICE_ID
        Long endpointServiceId = decodeLong(props, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_SERVICE_ID);
        osgiProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_SERVICE_ID, endpointServiceId);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID
        String fwkuuid = decodeString(props, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID);
        osgiProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID, fwkuuid);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS
        List<String> configTypes = decodeList(props, org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS);
        if (configTypes != null && configTypes.size() > 0)
            osgiProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, (String[]) configTypes.toArray(new String[configTypes.size()]));
        // org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS
        List<String> intents = decodeList(props, org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS);
        if (intents != null && intents.size() > 0)
            osgiProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS, (String[]) intents.toArray(new String[intents.size()]));
        // org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED
        List<String> remoteConfigsSupported = decodeList(props, org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED);
        if (remoteConfigsSupported != null && remoteConfigsSupported.size() > 0)
            osgiProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED, (String[]) remoteConfigsSupported.toArray(new String[remoteConfigsSupported.size()]));
        // org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED
        List<String> remoteIntentsSupported = decodeList(props, org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED);
        if (remoteIntentsSupported != null && remoteIntentsSupported.size() > 0)
            osgiProperties.put(org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED, (String[]) remoteIntentsSupported.toArray(new String[remoteIntentsSupported.size()]));
    }

    /**
	 * @param discoveredServiceProperties discovered service properties
	 * @return org.osgi.service.remoteserviceadmin.EndpointDescription endpoint description decoded from service p
	 * properties
	 * @since 3.0
	 */
    protected org.osgi.service.remoteserviceadmin.EndpointDescription decodeEndpointDescription(IServiceProperties discoveredServiceProperties) {
        Map<String, Object> endpointDescriptionProperties = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        decodeOSGiProperties(discoveredServiceProperties, endpointDescriptionProperties);
        // remote service id
        String containerIDNamespace = decodeString(discoveredServiceProperties, RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE);
        if (containerIDNamespace != null) {
            // remote service id
            Long remoteServiceId = decodeLong(discoveredServiceProperties, org.eclipse.ecf.remoteservice.Constants.SERVICE_ID);
            if (remoteServiceId != null)
                endpointDescriptionProperties.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, remoteServiceId);
            // container id namespace
            endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, containerIDNamespace);
            // ecf endpoint id
            String ecfEndpointId = decodeString(discoveredServiceProperties, RemoteConstants.ENDPOINT_ID);
            if (ecfEndpointId != null)
                endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_ID, ecfEndpointId);
            // timestamp
            Long timestamp = decodeLong(discoveredServiceProperties, RemoteConstants.ENDPOINT_TIMESTAMP);
            if (timestamp != null)
                endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_TIMESTAMP, timestamp);
            // connect target ID
            String connectTargetIDName = decodeString(discoveredServiceProperties, RemoteConstants.ENDPOINT_CONNECTTARGET_ID);
            if (connectTargetIDName != null)
                endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_CONNECTTARGET_ID, connectTargetIDName);
            // ID filter
            List<String> idFilterNames = decodeList(discoveredServiceProperties, RemoteConstants.ENDPOINT_IDFILTER_IDS);
            Object idFilterNamesval = PropertiesUtil.convertToStringPlusValue(idFilterNames);
            if (idFilterNamesval != null)
                endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_IDFILTER_IDS, idFilterNamesval);
            // remote service filter
            String remoteServiceFilter = decodeString(discoveredServiceProperties, RemoteConstants.ENDPOINT_REMOTESERVICE_FILTER);
            if (remoteServiceFilter != null)
                endpointDescriptionProperties.put(RemoteConstants.ENDPOINT_REMOTESERVICE_FILTER, remoteServiceFilter);
            List<String> asyncInterfaces = decodeList(discoveredServiceProperties, RemoteConstants.SERVICE_EXPORTED_ASYNC_INTERFACES);
            if (asyncInterfaces != null && asyncInterfaces.size() > 0)
                endpointDescriptionProperties.put(RemoteConstants.SERVICE_EXPORTED_ASYNC_INTERFACES, asyncInterfaces.toArray(new String[asyncInterfaces.size()]));
        }
        // Finally, fill out other properties
        decodeNonStandardServiceProperties(discoveredServiceProperties, endpointDescriptionProperties);
        return (containerIDNamespace == null) ? new org.osgi.service.remoteserviceadmin.EndpointDescription(endpointDescriptionProperties) : new EndpointDescription(endpointDescriptionProperties);
    }

    private String getPackageName(String className) {
        //$NON-NLS-1$
        int lastDotIndex = className.lastIndexOf(".");
        if (lastDotIndex == -1)
            //$NON-NLS-1$
            return "";
        return className.substring(0, lastDotIndex);
    }

    /**
	 * @param endpointDescription endpoint description to encode
	 * @param result service properties to add encoded endpoint description properties to
	 * @since 3.0
	 */
    protected void encodeOSGiServiceProperties(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, IServiceProperties result) {
        // org.osgi.framework.Constants.OBJECTCLASS =
        // endpointDescription.getInterfaces();
        List<String> interfaces = endpointDescription.getInterfaces();
        encodeList(result, org.osgi.framework.Constants.OBJECTCLASS, interfaces);
        // (if specified)
        for (String intf : interfaces) {
            String intfPackageName = getPackageName(intf);
            Version intfVersion = endpointDescription.getPackageVersion(intfPackageName);
            if (intfVersion != null && !Version.emptyVersion.equals(intfVersion))
                encodeString(result, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_PACKAGE_VERSION_ + intfPackageName, intfVersion.toString());
        }
        // org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID ==
        // endpointDescription.getId()
        String endpointId = endpointDescription.getId();
        encodeString(result, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_ID, endpointId);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_SERVICE_ID
        // = endpointDescription.getServiceId()
        long endpointServiceId = endpointDescription.getServiceId();
        encodeLong(result, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_SERVICE_ID, new Long(endpointServiceId));
        // org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID
        // = endpointDescription.getFrameworkUUID()
        String frameworkUUID = endpointDescription.getFrameworkUUID();
        if (frameworkUUID != null)
            encodeString(result, org.osgi.service.remoteserviceadmin.RemoteConstants.ENDPOINT_FRAMEWORK_UUID, frameworkUUID);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS
        // = endpointDescription.getConfigurationTypes();
        List<String> configurationTypes = endpointDescription.getConfigurationTypes();
        if (configurationTypes.size() > 0)
            encodeList(result, org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_IMPORTED_CONFIGS, configurationTypes);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS =
        // endpointDescription.getIntents()
        List<String> serviceIntents = endpointDescription.getIntents();
        if (serviceIntents.size() > 0)
            encodeList(result, org.osgi.service.remoteserviceadmin.RemoteConstants.SERVICE_INTENTS, serviceIntents);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED
        Map endpointDescriptionProperties = endpointDescription.getProperties();
        List<String> remoteConfigsSupported = PropertiesUtil.getStringPlusProperty(endpointDescriptionProperties, org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED);
        if (remoteConfigsSupported.size() > 0)
            encodeList(result, org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_CONFIGS_SUPPORTED, remoteConfigsSupported);
        // org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED
        List<String> remoteIntentsSupported = PropertiesUtil.getStringPlusProperty(endpointDescriptionProperties, org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED);
        if (remoteIntentsSupported.size() > 0)
            encodeList(result, org.osgi.service.remoteserviceadmin.RemoteConstants.REMOTE_INTENTS_SUPPORTED, remoteIntentsSupported);
    }

    /**
	 * @param endpointDescription endpoint description to encode
	 * @param result service properties to add encoded endpoint description properties to
	 * 
	 * @since 3.0
	 */
    protected void encodeServiceProperties(org.osgi.service.remoteserviceadmin.EndpointDescription endpointDescription, IServiceProperties result) {
        encodeOSGiServiceProperties(endpointDescription, result);
        if (endpointDescription instanceof org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription) {
            EndpointDescription ecfEd = (EndpointDescription) endpointDescription;
            Long rsId = (Long) endpointDescription.getProperties().get(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID);
            if (rsId != null)
                encodeLong(result, org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, rsId);
            String ecfEndpointId = (String) ecfEd.getProperties().get(RemoteConstants.ENDPOINT_ID);
            if (ecfEndpointId != null)
                encodeString(result, RemoteConstants.ENDPOINT_ID, ecfEndpointId);
            // org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE
            String containerIDNamespace = ecfEd.getIdNamespace();
            if (containerIDNamespace != null)
                encodeString(result, RemoteConstants.ENDPOINT_CONTAINER_ID_NAMESPACE, containerIDNamespace);
            Long timestamp = (Long) ecfEd.getProperties().get(RemoteConstants.ENDPOINT_TIMESTAMP);
            if (timestamp != null)
                encodeLong(result, RemoteConstants.ENDPOINT_TIMESTAMP, timestamp);
            // org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_CONNECTTARGET_ID
            ID connectTargetID = ecfEd.getConnectTargetID();
            if (connectTargetID != null)
                encodeString(result, RemoteConstants.ENDPOINT_CONNECTTARGET_ID, connectTargetID.getName());
            // org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_IDFILTER_IDS
            ID[] idFilter = ecfEd.getIDFilter();
            if (idFilter != null && idFilter.length > 0) {
                List<String> idNames = new ArrayList<String>();
                for (int i = 0; i < idFilter.length; i++) idNames.add(idFilter[i].getName());
                encodeList(result, RemoteConstants.ENDPOINT_IDFILTER_IDS, idNames);
            }
            // org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteConstants.ENDPOINT_REMOTESERVICE_FILTER
            String remoteFilter = ecfEd.getRemoteServiceFilter();
            if (remoteFilter != null)
                encodeString(result, RemoteConstants.ENDPOINT_REMOTESERVICE_FILTER, remoteFilter);
            List<String> asyncTypes = ecfEd.getAsyncInterfaces();
            if (asyncTypes != null && asyncTypes.size() > 0)
                encodeList(result, RemoteConstants.SERVICE_EXPORTED_ASYNC_INTERFACES, asyncTypes);
        }
        // encode non standard properties
        encodeNonStandardServiceProperties(endpointDescription.getProperties(), result);
    }

    protected void encodeNonStandardServiceProperties(Map<String, Object> properties, IServiceProperties result) {
        for (String key : properties.keySet()) {
            if (!PropertiesUtil.isReservedProperty(key)) {
                Object val = properties.get(key);
                if (val instanceof byte[]) {
                    result.setPropertyBytes(key, (byte[]) val);
                } else if (val instanceof String) {
                    result.setPropertyString(key, (String) val);
                } else {
                    result.setProperty(key, val);
                }
            }
        }
    }

    protected void decodeNonStandardServiceProperties(IServiceProperties props, Map<String, Object> result) {
        for (Enumeration keys = props.getPropertyNames(); keys.hasMoreElements(); ) {
            String key = (String) keys.nextElement();
            if (!PropertiesUtil.isReservedProperty(key)) {
                byte[] bytes = props.getPropertyBytes(key);
                if (bytes != null) {
                    result.put(key, bytes);
                    continue;
                }
                String str = props.getPropertyString(key);
                if (str != null) {
                    result.put(key, str);
                    continue;
                }
                Object obj = props.getProperty(key);
                if (obj != null) {
                    result.put(key, obj);
                    continue;
                }
            }
        }
    }

    protected void logWarning(String methodName, String message, Throwable t) {
        LogUtility.logWarning(methodName, DebugOptions.METADATA_FACTORY, this.getClass(), message, t);
    }

    protected void logError(String methodName, String message, Throwable t) {
        LogUtility.logError(methodName, DebugOptions.METADATA_FACTORY, this.getClass(), message, t);
    }

    public void close() {
    // nothing to do
    }
}
