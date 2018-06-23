/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.ecf.provider.xmpp.identity;

import java.net.URL;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class XMPPFileNamespace extends Namespace {

    private static final long serialVersionUID = 629370079122562988L;

    //$NON-NLS-1$
    public static final String SCHEME = "xmppfile";

    //$NON-NLS-1$
    public static final String NAME = "ecf.provider.filetransfer.xmpp";

    private String getInitFromExternalForm(Object[] args) {
        if (args == null || args.length < 1 || args[0] == null)
            return null;
        if (args[0] instanceof String) {
            final String arg = (String) args[0];
            if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
                final int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
                if (index >= arg.length())
                    return null;
                return arg.substring(index + 1);
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.ecf.core.identity.Namespace#createInstance(java.lang.Object[])
	 */
    public ID createInstance(Object[] parameters) throws IDCreateException {
        try {
            final String init = getInitFromExternalForm(parameters);
            if (init != null)
                return new XMPPFileID(new URL(init));
            if (parameters[0] instanceof URL)
                return new XMPPFileID((URL) parameters[0]);
            return new XMPPFileID((XMPPID) parameters[0], (String) parameters[1]);
        } catch (final Exception e) {
            throw new IDCreateException(NLS.bind("{0} createInstance()", getName()), e);
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
        return new Class[][] { { String.class }, { URL.class }, { XMPPID.class, String.class } };
    }
}
