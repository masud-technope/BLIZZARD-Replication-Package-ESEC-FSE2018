/*******************************************************************************
 * Copyright (c) 2007 Markus Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Kuppe
 ******************************************************************************/
package org.eclipse.ecf.provider.jslp.identity;

import java.net.URI;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;
import org.eclipse.ecf.discovery.identity.ServiceID;

public class JSLPServiceID extends ServiceID {

    private static final long serialVersionUID = -8211896244921087422L;

     JSLPServiceID(Namespace namespace, IServiceTypeID type, URI anURI) {
        super(namespace, type, anURI);
    }
}
