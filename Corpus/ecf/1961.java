/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

import org.eclipse.ecf.remoteservices.ui.RSAImageRegistry;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @since 3.3
 */
public class EndpointPropertyNodeWorkbenchAdapter extends AbstractEndpointNodeWorkbenchAdapter {

    @Override
    public String getLabel(Object object) {
        EndpointPropertyNode epn = (EndpointPropertyNode) object;
        String propertyName = epn.getPropertyAlias();
        if (propertyName == null)
            propertyName = epn.getPropertyName();
        return propertyName + epn.getNameValueSeparator() + epn.getPropertyValue();
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return RSAImageRegistry.DESC_PROPERTY_OBJ;
    }
}
