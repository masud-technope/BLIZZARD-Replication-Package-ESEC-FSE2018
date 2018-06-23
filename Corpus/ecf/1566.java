/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.endpoint.model;

import org.eclipse.ecf.internal.remoteservices.ui.Messages;
import org.eclipse.ecf.remoteservice.Constants;

/**
 * @since 3.3
 */
public class EndpointRemoteServiceIDNode extends EndpointECFNode {

    public  EndpointRemoteServiceIDNode() {
        super(Constants.SERVICE_ID);
        setPropertyAlias(Messages.EndpointRemoteServiceIDNode_REMOTE_SERVICE_ID_PROP_NAME);
    }

    @Override
    public Object getPropertyValue() {
        return getEndpointDescription().getRemoteServiceId();
    }
}
