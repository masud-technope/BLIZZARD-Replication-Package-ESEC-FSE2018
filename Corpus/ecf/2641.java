/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

import org.eclipse.ui.IViewSite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

/**
 * @since 3.3
 */
public class EndpointContentProvider extends BaseWorkbenchContentProvider {

    private IViewSite viewSite;

    private String topNodeLabel;

    private EndpointGroupNode invisibleRoot;

    private EndpointGroupNode root;

    public  EndpointContentProvider(IViewSite viewSite, String topNodeLabel) {
        this.viewSite = viewSite;
        this.topNodeLabel = topNodeLabel;
    }

    public EndpointGroupNode getRootNode() {
        return root;
    }

    public Object[] getElements(Object parent) {
        if (parent.equals(viewSite)) {
            if (invisibleRoot == null) {
                invisibleRoot = new //$NON-NLS-1$
                EndpointGroupNode(//$NON-NLS-1$
                "");
                root = new EndpointGroupNode(topNodeLabel);
                invisibleRoot.addChild(root);
            }
            return getChildren(invisibleRoot);
        }
        return getChildren(parent);
    }
}
