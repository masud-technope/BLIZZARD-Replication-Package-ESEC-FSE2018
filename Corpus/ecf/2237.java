/*******************************************************************************
 * Copyright (c) 2004, 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.provider.irc.identity;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.ecf.core.identity.*;
import org.eclipse.ecf.internal.provider.irc.Messages;
import org.eclipse.osgi.util.NLS;

public class IRCNamespace extends org.eclipse.ecf.core.identity.Namespace {

    private static final long serialVersionUID = 1005111581522377553L;

    //$NON-NLS-1$
    public static final String IRC_PROTOCOL = "irc";

    //$NON-NLS-1$
    public static final String IRC_SCHEME = "ecf.irc.irclib";

    public static final String IRCNAMESPACE_NAME = IRC_SCHEME;

    private String getProtocolPrefix() {
        //$NON-NLS-1$
        return IRC_PROTOCOL + "://";
    }

    private String getInitFromExternalForm(Object[] args) {
        if (args == null || args.length < 1 || args[0] == null)
            return null;
        if (args[0] instanceof String) {
            String arg = (String) args[0];
            if (arg.startsWith(getScheme() + Namespace.SCHEME_SEPARATOR)) {
                int index = arg.indexOf(Namespace.SCHEME_SEPARATOR);
                if (index >= arg.length())
                    return null;
                return arg.substring(index + 1);
            }
        }
        return null;
    }

    public ID createInstance(Object[] args) throws IDCreateException {
        try {
            String init = getInitFromExternalForm(args);
            String s = (init == null) ? (String) args[0] : init;
            if (!s.startsWith(getProtocolPrefix()))
                s = getProtocolPrefix() + s;
            URI newURI = createURI(s);
            String uriScheme = newURI.getScheme();
            if (uriScheme == null || !uriScheme.equalsIgnoreCase(IRC_PROTOCOL)) {
                throw new IDCreateException(NLS.bind(Messages.IRCNamespace_EXCEPTION_INVALID_PROTOCOL, newURI, IRC_PROTOCOL));
            }
            return new IRCID(this, newURI);
        } catch (Exception e) {
            throw new IDCreateException(NLS.bind("{0} createInstance()", getName()), e);
        }
    }

    private URI createURI(String s) throws IDCreateException, URISyntaxException {
        URI ret = null;
        //$NON-NLS-1$
        String uname = s.substring(getProtocolPrefix().length(), s.indexOf("@"));
        //$NON-NLS-1$
        int hostend = s.lastIndexOf("/");
        //$NON-NLS-1$
        int hoststart = s.indexOf("@");
        if (hoststart > hostend || hostend == -1) {
            hostend = s.length();
        }
        String host = s.substring(hoststart + 1, hostend);
        int port = -1;
        //$NON-NLS-1$
        int portidx = host.indexOf(":");
        if (portidx >= 0) {
            port = Integer.parseInt(host.substring(portidx + 1, host.length()));
            host = host.substring(0, portidx);
        }
        String path = s.substring(hostend, s.length());
        ret = new URI(IRC_PROTOCOL, uname, host, port, path, null, null);
        return ret;
    }

    public String getScheme() {
        return IRC_SCHEME;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ecf.core.identity.Namespace#getSupportedParameterTypesForCreateInstance()
	 */
    public Class[][] getSupportedParameterTypes() {
        return new Class[][] { { String.class } };
    }
}
