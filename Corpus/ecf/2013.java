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
import org.osgi.framework.Version;

/**
 * @since 3.3
 */
public class EndpointPackageVersionNodeWorkbenchAdapter extends AbstractEndpointNodeWorkbenchAdapter {

    @Override
    public String getLabel(Object object) {
        EndpointPackageVersionNode pvn = (EndpointPackageVersionNode) object;
        Version v = (Version) pvn.getPropertyValue();
        if (v == null)
            v = Version.emptyVersion;
        return //$NON-NLS-1$
        pvn.getPropertyName() + " " + pvn.getPropertyAlias() + pvn.getNameValueSeparator() + v.toString();
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return RSAImageRegistry.DESC_PACKAGE_OBJ;
    }
}
