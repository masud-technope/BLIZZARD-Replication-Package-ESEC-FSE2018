/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 * 
 * @since 3.3
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @since 3.3
 */
public class EndpointPropertySource implements IPropertySource {

    private final Map<String, Object> props;

    private final List<IPropertyDescriptor> descriptors;

    public  EndpointPropertySource(Map<String, Object> props) {
        this.props = props;
        descriptors = new ArrayList<IPropertyDescriptor>();
        for (String k : props.keySet()) descriptors.add(new PropertyDescriptor(k, k));
    }

    @Override
    public Object getEditableValue() {
        return null;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

    @Override
    public Object getPropertyValue(Object id) {
        Object val = props.get(id);
        if (val != null) {
            if (val instanceof String)
                return val;
            else if (val instanceof String[])
                return convertArrayToString((String[]) val);
            else if (val != null)
                return val.toString();
        }
        return null;
    }

    private String convertArrayToString(String[] val) {
        List<String> results = new ArrayList<String>();
        for (String s : val) results.add(s);
        return results.toString();
    }

    @Override
    public boolean isPropertySet(Object id) {
        return false;
    }

    @Override
    public void resetPropertyValue(Object id) {
    }

    @Override
    public void setPropertyValue(Object id, Object value) {
    }
}
