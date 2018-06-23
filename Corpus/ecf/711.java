/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointNodeWorkbenchAdapter;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @since 3.3
 */
public class EndpointDescriptionRSANodeWorkbenchAdapter extends EndpointNodeWorkbenchAdapter {

    @Override
    public String getLabel(Object object) {
        EndpointDescription ed = ((EndpointDescriptionRSANode) object).getEndpointNode().getEndpointDescription();
        //$NON-NLS-1$
        return ed.getContainerID().getName() + ":" + ed.getRemoteServiceId();
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return ((EndpointDescriptionRSANode) object).getEndpointNode().isImported() ? importedEndpointDesc : edImageDesc;
    }

    @Override
    public Object getParent(Object object) {
        return ((AbstractRSANode) object).getParent();
    }

    @Override
    public Object[] getChildren(Object object) {
        return ((EndpointDescriptionRSANode) object).getEndpointNode().getChildren();
    }
}
