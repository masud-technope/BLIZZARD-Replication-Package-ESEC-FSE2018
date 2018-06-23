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
package org.eclipse.ecf.provider.xmpp.identity;

import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.StringUtils;
import org.eclipse.ecf.internal.provider.xmpp.Messages;
import org.eclipse.ecf.presence.IFQID;
import org.eclipse.ecf.presence.im.IChatID;

public class XMPPID extends BaseID implements IChatID, IFQID {

    private static final long serialVersionUID = 3257289140701049140L;

    public static final char USER_HOST_DELIMITER = '@';

    public static final char PORT_DELIMITER = ':';

    public static final String PATH_DELIMITER = "/";

    static class XMPPEscape {

        StringBuffer buf = new StringBuffer();

        public  XMPPEscape(char[] chars) {
            if (chars != null) {
                for (int i = 0; i < chars.length; i++) {
                    buf.append(chars[i]);
                }
            }
        }

        public String getAsString() {
            return buf.toString();
        }
    }

    protected static Hashtable escapeTable;

    static {
        escapeTable = new Hashtable(10);
        escapeTable.put("@", new XMPPEscape(new char[] { '\\', '4', '0' }));
        escapeTable.put("\"", new XMPPEscape(new char[] { '\\', '2', '2' }));
        escapeTable.put("&", new XMPPEscape(new char[] { '\\', '2', '6' }));
        escapeTable.put("'", new XMPPEscape(new char[] { '\\', '2', '7' }));
        escapeTable.put("/", new XMPPEscape(new char[] { '\\', '2', 'f' }));
        escapeTable.put(":", new XMPPEscape(new char[] { '\\', '3', 'a' }));
        escapeTable.put("<", new XMPPEscape(new char[] { '\\', '3', 'c' }));
        escapeTable.put(">", new XMPPEscape(new char[] { '\\', '3', 'e' }));
        escapeTable.put("\\", new XMPPEscape(new char[] { '\\', '5', 'c' }));
    }

    static String fixEscapeInNode(String node) {
        if (node == null)
            return null;
        for (final Iterator i = escapeTable.keySet().iterator(); i.hasNext(); ) {
            final String key = (String) i.next();
            final XMPPEscape escape = (XMPPEscape) escapeTable.get(key);
            node = StringUtils.replaceAll(node, key, escape.getAsString());
        }
        return node;
    }

    static String fixPercentEscape(String src) {
        if (src == null)
            return null;
        return StringUtils.replaceAll(src, "%", "%25");
    }

    public static String unfixEscapeInNode(String node) {
        if (node == null)
            return null;
        for (final Iterator i = escapeTable.keySet().iterator(); i.hasNext(); ) {
            final String key = (String) i.next();
            final XMPPEscape escape = (XMPPEscape) escapeTable.get(key);
            node = StringUtils.replaceAll(node, escape.getAsString(), key);
        }
        return node;
    }

    protected String username;

    protected String hostname;

    protected String resourcename;

    protected int port = -1;

    public  XMPPID(Namespace namespace, String unamehost) throws URISyntaxException {
        super(namespace);
        unamehost = fixPercentEscape(unamehost);
        if (unamehost == null)
            throw new URISyntaxException(unamehost, Messages.XMPPID_EXCEPTION_XMPPID_USERNAME_NOT_NULL);
        int atIndex = unamehost.lastIndexOf(USER_HOST_DELIMITER);
        if (atIndex == -1)
            throw new URISyntaxException(unamehost, Messages.XMPPID_EXCEPTION_HOST_PORT_NOT_VALID);
        username = fixEscapeInNode(unamehost.substring(0, atIndex));
        final String remainder = unamehost.substring(atIndex + 1);
        atIndex = remainder.lastIndexOf(PORT_DELIMITER);
        if (atIndex != -1) {
            try {
                final int slashLoc = remainder.indexOf(PATH_DELIMITER);
                if (slashLoc != -1)
                    port = Integer.parseInt(remainder.substring(atIndex + 1, slashLoc));
                else
                    port = Integer.parseInt(remainder.substring(atIndex + 1));
            } catch (final NumberFormatException e) {
                throw new URISyntaxException(unamehost, Messages.XMPPID_EXCEPTION_INVALID_PORT);
            }
            hostname = remainder.substring(0, atIndex);
        }
        atIndex = remainder.indexOf(PATH_DELIMITER);
        if (atIndex != -1) {
            if (hostname == null)
                hostname = remainder.substring(0, atIndex);
            setResourceName(remainder.substring(atIndex + 1));
        } else {
            setResourceName(null);
        }
        if (hostname == null)
            hostname = remainder;
    }

    protected int namespaceCompareTo(BaseID o) {
        return getName().compareTo(o.getName());
    }

    protected boolean namespaceEquals(BaseID o) {
        if (!(o instanceof XMPPID)) {
            return false;
        }
        final XMPPID other = (XMPPID) o;
        String thisResourceName = getResourceName();
        String otherResourceName = other.getResourceName();
        boolean resourceEquals = false;
        if (thisResourceName == null)
            resourceEquals = (otherResourceName == null) ? true : false;
        else if (otherResourceName == null)
            resourceEquals = (thisResourceName == null) ? true : false;
        else
            resourceEquals = thisResourceName.equals(otherResourceName) || thisResourceName.endsWith(otherResourceName) || otherResourceName.endsWith(thisResourceName);
        return resourceEquals && getUsernameAtHost().equals(other.getUsernameAtHost());
    }

    protected String namespaceGetName() {
        return getUsernameAtHost();
    }

    protected int namespaceHashCode() {
        return getUsernameAtHost().hashCode();
    }

    public String getNodename() {
        return username;
    }

    protected String namespaceToExternalForm() {
        return //" + getFQName();
        getNamespace().getScheme() + "://" + //" + getFQName();
        getFQName();
    }

    public String getUsername() {
        return unfixEscapeInNode(username);
    }

    public String getHostname() {
        return hostname;
    }

    public String getResourceName() {
        return resourcename;
    }

    public void setResourceName(String resourceName) {
        this.resourcename = resourceName;
    }

    public int getPort() {
        return port;
    }

    public String getUsernameAtHost() {
        String username = getUsername();
        String hostname = getHostname();
        int semiColonIdx = hostname.indexOf(';');
        if (semiColonIdx != -1) {
            hostname = hostname.substring(0, semiColonIdx);
        }
        return username + USER_HOST_DELIMITER + hostname + ((getPort() == -1) ? "" : ":" + getPort());
    }

    public String getFQName() {
        String rn = getResourceName();
        return getUsernameAtHost() + PATH_DELIMITER + ((rn == null) ? "" : rn);
    }

    public String toString() {
        //$NON-NLS-1$
        final StringBuffer sb = new StringBuffer("XMPPID[");
        sb.append(toExternalForm()).append("]");
        return sb.toString();
    }

    public Object getAdapter(Class clazz) {
        if (clazz.isInstance(this)) {
            return this;
        } else
            return super.getAdapter(clazz);
    }
}
