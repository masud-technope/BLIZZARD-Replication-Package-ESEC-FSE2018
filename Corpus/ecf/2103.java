/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.jmdns.identity;

import java.net.URI;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceID;

public class JMDNSServiceID extends ServiceID {

    private static final long serialVersionUID = 8389531866888790264L;

    /**
	 * @param namespace namespace for this ID
	 * @param type service type ID
	 * @param anURI uri for the service id
	 * @since 3.0
	 */
    public  JMDNSServiceID(final Namespace namespace, final IServiceTypeID type, final URI anURI) {
        super(namespace, type, anURI);
    }
}
