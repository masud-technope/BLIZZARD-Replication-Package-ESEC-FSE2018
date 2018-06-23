/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservices.ui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.ecf.internal.remoteservices.ui.Messages;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * @since 3.3
 */
public class PropertyUtils {

    public static String convertStringArrayToString(String[] strings) {
        StringBuffer buf = new StringBuffer(Messages.PropertyUtils_0);
        for (int i = 0; i < strings.length; i++) {
            buf.append(strings[i]);
            if (i < strings.length - 1)
                buf.append(Messages.PropertyUtils_1);
        }
        return buf.append(Messages.PropertyUtils_2).toString();
    }

    public static Map<String, Object> convertServicePropsToMap(ServiceReference sr) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (sr == null)
            return result;
        for (String key : sr.getPropertyKeys()) {
            Object val = sr.getProperty(key);
            if (val != null)
                result.put(key, val);
        }
        return result;
    }

    public static String convertObjectClassToString(ServiceReference sr) {
        return (sr == null) ? Messages.PropertyUtils_3 : convertStringArrayToString((String[]) sr.getProperty(Constants.OBJECTCLASS));
    }

    public static final String getPackageName(String fqClassName) {
        int lastDot = fqClassName.lastIndexOf('.');
        return (lastDot == -1) ? fqClassName : fqClassName.substring(0, lastDot);
    }

    public static final List<String> getStringArrayProperty(Map<String, Object> props, String propName) {
        Object r = props.get(propName);
        List<String> results = new ArrayList<String>();
        if (r == null)
            return results;
        else if (r instanceof String[]) {
            String[] vals = (String[]) r;
            for (String v : vals) results.add(v);
        } else if (r instanceof String)
            results.add((String) r);
        else
            results.add(r.toString());
        return results;
    }
}
