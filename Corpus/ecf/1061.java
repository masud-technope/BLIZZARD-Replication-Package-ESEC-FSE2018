/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.provider.dnssd;

import java.net.URI;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.StringID;
import org.eclipse.ecf.discovery.identity.IServiceID;
import org.eclipse.ecf.discovery.identity.IServiceTypeID;

public class DnsSdNamespace extends Namespace {

    private static final long serialVersionUID = 7902507535188221743L;

    //$NON-NLS-1$
    public static final String SCHEME = "dnssd";

    //$NON-NLS-1$
    public static final String NAME = "ecf.namespace.dnssd";

    public  DnsSdNamespace() {
        super(NAME, "Dns SD Namespace");
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
    public ID createInstance(Object[] parameters) throws IDCreateException {
        if (parameters != null && parameters.length == 1 && parameters[0] instanceof String) {
            String str = (String) parameters[0];
            return new DnsSdServiceTypeID(this, str);
        } else if (parameters != null && parameters.length == 1 && parameters[0] instanceof IServiceTypeID) {
            IServiceTypeID serviceTypeID = (IServiceTypeID) parameters[0];
            return new DnsSdServiceTypeID(this, serviceTypeID);
        } else if (parameters != null && parameters.length == 1 && parameters[0] instanceof IServiceID) {
            IServiceID serviceID = (IServiceID) parameters[0];
            return new DnsSdServiceTypeID(this, serviceID.getServiceTypeID());
        } else if (parameters != null && parameters.length == 1 && parameters[0] instanceof StringID) {
            StringID stringID = (StringID) parameters[0];
            return new DnsSdServiceTypeID(this, stringID.getName());
        } else if (parameters != null && parameters.length == 2 && parameters[0] instanceof IServiceTypeID && parameters[1] instanceof URI) {
            IServiceTypeID serviceTypeID = (IServiceTypeID) parameters[0];
            URI uri = (URI) parameters[1];
            return new DnsSdServiceID(this, new DnsSdServiceTypeID(this, serviceTypeID), uri);
        } else if (parameters != null && parameters.length == 2 && parameters[0] instanceof String && parameters[1] instanceof URI) {
            String serviceType = (String) parameters[0];
            URI uri = (URI) parameters[1];
            return new DnsSdServiceID(this, new DnsSdServiceTypeID(this, serviceType), uri);
        } else {
            throw new IDCreateException(Messages.DnsSdNamespace_Wrong_Parameters);
        }
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getScheme()
	 */
    public String getScheme() {
        return SCHEME;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypes()
	 */
    public Class[][] getSupportedParameterTypes() {
        return new Class[][] { { String.class }, { IServiceTypeID.class }, { IServiceTypeID.class, URI.class }, { String.class, URI.class } };
    }
}
