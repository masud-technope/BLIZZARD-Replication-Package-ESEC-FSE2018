/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.osgi.services.discovery;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.internal.osgi.services.discovery.ServicePropertyUtils;
import org.osgi.service.discovery.ServiceEndpointDescription;
import org.osgi.service.discovery.ServicePublication;

public abstract class ECFServiceEndpointDescription implements ServiceEndpointDescription {

    private final Map serviceProperties;

    public  ECFServiceEndpointDescription(Map properties) {
        this.serviceProperties = properties;
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getEndpointID()
	 */
    public String getEndpointID() {
        Object o = serviceProperties.get(ServicePublication.PROP_KEY_ENDPOINT_ID);
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getEndpointInterfaceName(java.lang.String)
	 */
    public String getEndpointInterfaceName(String interfaceName) {
        if (interfaceName == null)
            return null;
        Object o = serviceProperties.get(ServicePublication.PROP_KEY_ENDPOINT_INTERFACE_NAME);
        if (o == null || !(o instanceof String)) {
            return null;
        }
        String intfNames = (String) o;
        Collection c = ServicePropertyUtils.createCollectionFromString(intfNames);
        if (c == null)
            return null;
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            String intfName = (String) i.next();
            if (intfName != null && intfName.startsWith(interfaceName)) {
                // return just endpointInterfaceName
                return intfName.substring(intfName.length() + ServicePropertyUtils.ENDPOINT_INTERFACE_NAME_SEPARATOR.length()).trim();
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getLocation()
	 */
    public URL getLocation() {
        Object o = serviceProperties.get(ServicePublication.PROP_KEY_ENDPOINT_LOCATION);
        if (o == null || !(o instanceof String)) {
            return null;
        }
        String urlExternalForm = (String) o;
        URL url = null;
        try {
            url = new URL(urlExternalForm);
        } catch (MalformedURLException e) {
        }
        return url;
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProperties()
	 */
    public Map getProperties() {
        return serviceProperties;
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProperty(java.lang.String)
	 */
    public Object getProperty(String key) {
        return serviceProperties.get(key);
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getPropertyKeys()
	 */
    public Collection getPropertyKeys() {
        return serviceProperties.keySet();
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getProvidedInterfaces()
	 */
    public Collection getProvidedInterfaces() {
        Object o = serviceProperties.get(ServicePublication.PROP_KEY_SERVICE_INTERFACE_NAME);
        if (o == null || !(o instanceof String)) {
            throw new NullPointerException();
        }
        final String providedInterfacesStr = (String) o;
        return ServicePropertyUtils.createCollectionFromString(providedInterfacesStr);
    }

    /* (non-Javadoc)
	 * @see org.osgi.service.discovery.ServiceEndpointDescription#getVersion(java.lang.String)
	 */
    public String getVersion(String interfaceName) {
        Collection c = getProvidedInterfaces();
        if (c == null) {
            return null;
        }
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            String intfName = (String) i.next();
            if (intfName != null && intfName.startsWith(interfaceName)) {
                // return just version string
                return intfName.substring(intfName.length() + ServicePropertyUtils.INTERFACE_VERSION_SEPARATOR.length()).trim();
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        //$NON-NLS-1$
        StringBuffer sb = new StringBuffer("ServiceEndpointDescriptionImpl[");
        //$NON-NLS-1$
        sb.append(";providedinterfaces=").append(getProvidedInterfaces());
        //$NON-NLS-1$
        sb.append(";location=").append(getLocation());
        //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(";props=").append(getProperties()).append("]");
        return sb.toString();
    }

    public abstract ID getECFEndpointID();

    public long getFutureTimeout() {
        return 30000L;
    }
}
