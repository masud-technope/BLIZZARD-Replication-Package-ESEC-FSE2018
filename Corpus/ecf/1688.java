/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

import java.util.List;
import java.util.Map;
import org.eclipse.ecf.internal.remoteservices.ui.Messages;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.EndpointDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportReference;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.RemoteServiceAdmin.ImportRegistration;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointAsyncInterfacesNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointConfigTypesNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointConnectTargetIDNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointFrameworkIDNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointHostGroupNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointIDNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointIntentsNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointInterfacesNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointNamespaceNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointPackageVersionNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointRemoteServiceFilterNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointRemoteServiceIDNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointServiceIDNode;
import org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model.EndpointTimestampNode;
import org.eclipse.ecf.remoteservices.ui.util.PropertyUtils;

/**
 * @since 3.3
 */
public class EndpointDescriptionRSANode extends AbstractRSANode {

    private final EndpointNode endpointNode;

    public  EndpointDescriptionRSANode(Map<String, Object> props) {
        this(new EndpointDescription(props));
    }

    public  EndpointDescriptionRSANode(EndpointDescription ed) {
        this(ed, null);
    }

    public  EndpointDescriptionRSANode(EndpointDescription ed, ImportRegistration ir) {
        this.endpointNode = (ir == null) ? new EndpointNode(ed) : new EndpointNode(ed, (ImportReference) ir.getImportReference());
        // Interfaces
        EndpointInterfacesNode ein = new EndpointInterfacesNode();
        for (String intf : ed.getInterfaces()) ein.addChild(new EndpointPackageVersionNode(PropertyUtils.getPackageName(intf)));
        this.endpointNode.addChild(ein);
        // Async Interfaces (if present)
        List<String> aintfs = ed.getAsyncInterfaces();
        if (aintfs.size() > 0) {
            EndpointAsyncInterfacesNode ain = new EndpointAsyncInterfacesNode();
            for (String intf : ed.getAsyncInterfaces()) ain.addChild(new EndpointPackageVersionNode(PropertyUtils.getPackageName(intf)));
            this.endpointNode.addChild(ain);
        }
        // ID
        this.endpointNode.addChild(new EndpointIDNode());
        // Remote Service Host
        EndpointHostGroupNode idp = new EndpointHostGroupNode(Messages.EndpointDiscoveryView_REMOTE_HOST_NAME);
        // Host children
        idp.addChild(new EndpointNamespaceNode());
        idp.addChild(new EndpointRemoteServiceIDNode());
        org.eclipse.ecf.core.identity.ID connectTarget = ed.getConnectTargetID();
        if (connectTarget != null)
            idp.addChild(new EndpointConnectTargetIDNode());
        idp.addChild(new EndpointServiceIDNode());
        idp.addChild(new EndpointIntentsNode());
        idp.addChild(new EndpointConfigTypesNode());
        idp.addChild(new EndpointFrameworkIDNode());
        idp.addChild(new EndpointTimestampNode());
        String filter = ed.getRemoteServiceFilter();
        if (filter != null)
            idp.addChild(new EndpointRemoteServiceFilterNode());
        this.endpointNode.addChild(idp);
    }

    public EndpointNode getEndpointNode() {
        return this.endpointNode;
    }
}
