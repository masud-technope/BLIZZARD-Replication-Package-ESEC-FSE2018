/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteserviceadmin.ui.rsa.model;

import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * @since 3.3
 */
public class ServiceIdNode extends NameValuePropertyNode {

    public  ServiceIdNode(ServiceReference sr, String labelPrefix) {
        super(Constants.SERVICE_ID, sr.getProperty(Constants.SERVICE_ID));
        setPropertyAlias(labelPrefix);
    }

    public  ServiceIdNode(long sid, String labelPrefix) {
        super(Constants.SERVICE_ID, sid);
        setPropertyAlias(labelPrefix);
    }
}
