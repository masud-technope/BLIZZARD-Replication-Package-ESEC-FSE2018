/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @since 3.3
 */
public class EndpointGroupNodeWorkbenchAdapter extends AbstractEndpointNodeWorkbenchAdapter {

    @Override
    public String getLabel(Object object) {
        return ((EndpointGroupNode) object).getGroupName();
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
    }
}
