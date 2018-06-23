/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.osgi.services.discovery;

import org.osgi.service.discovery.ServicePublication;
import java.util.*;
import org.osgi.framework.ServiceReference;

public class ServicePropertyUtils {

    //$NON-NLS-1$
    public static final String PROTOCOL_SEPARATOR = ":";

    public static final String ENDPOINT_INTERFACE_NAME_SEPARATOR = PROTOCOL_SEPARATOR;

    public static final String INTERFACE_VERSION_SEPARATOR = ServicePublication.SEPARATOR;

    //$NON-NLS-1$
    private static final String COLLECTION_SEPARATOR = ",";

    public static Collection getCollectionProperty(ServiceReference sr, String propName) {
        if (sr == null || propName == null) {
            return null;
        }
        final Object val = sr.getProperty(propName);
        if (val == null || !(val instanceof Collection)) {
            return null;
        }
        return (Collection) val;
    }

    public static String getStringProperty(ServiceReference reference, String propKey) {
        if (reference == null || propKey == null) {
            return null;
        }
        final Object val = reference.getProperty(propKey);
        if (val == null || !(val instanceof String)) {
            return null;
        }
        return (String) val;
    }

    public static Map getMapProperty(ServiceReference reference, String propKeyServiceProperties) {
        if (reference == null || propKeyServiceProperties == null) {
            return null;
        }
        final Object val = reference.getProperty(propKeyServiceProperties);
        if (val == null || !(val instanceof Map)) {
            return null;
        }
        return (Map) val;
    }

    public static String createStringFromCollection(Collection svcInterfaces) {
        if (svcInterfaces == null) {
            return null;
        }
        final StringBuffer result = new StringBuffer();
        for (final Iterator i = svcInterfaces.iterator(); i.hasNext(); ) {
            final String item = (String) i.next();
            result.append(item);
            if (i.hasNext()) {
                result.append(COLLECTION_SEPARATOR);
            }
        }
        return result.toString();
    }

    public static Collection createCollectionFromString(String value) {
        if (value == null) {
            return null;
        }
        final StringTokenizer t = new StringTokenizer(value, COLLECTION_SEPARATOR);
        final List result = new ArrayList();
        while (t.hasMoreTokens()) {
            result.add(t.nextToken());
        }
        return result;
    }
}
