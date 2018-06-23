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
import org.osgi.service.remoteserviceadmin.RemoteConstants;

/**
 * @since 3.3
 */
public class EndpointIntentsNode extends EndpointPropertyNode {

    public  EndpointIntentsNode() {
        super(RemoteConstants.REMOTE_INTENTS_SUPPORTED);
        setPropertyAlias(Messages.EndpointIntentsNode_INTENTS_PROP_NAME);
    }

    @Override
    public Object getPropertyValue() {
        return getStringArrayProperty(getPropertyName());
    }
}
