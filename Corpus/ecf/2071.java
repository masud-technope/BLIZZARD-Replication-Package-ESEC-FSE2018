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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.identity.BaseID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.osgi.util.NLS;

/**
 * XMPPFileID for use with the XMPP outgoing file transfer.
 */
public class XMPPFileID extends BaseID implements IFileID {

    private static final long serialVersionUID = 9052434567658554404L;

    //$NON-NLS-1$
    public static final String FILENAME_KEY = "file";

    //$NON-NLS-1$
    public static final String XMPPIDNAMESPACE_KEY = "ns";

    //$NON-NLS-1$
    private static final String UTF_8 = "utf-8";

    private final XMPPID xmppid;

    private final String filename;

    private final URL url;

    public  XMPPFileID(XMPPID id, String fn) throws MalformedURLException {
        Assert.isNotNull(id);
        Assert.isNotNull(fn);
        this.xmppid = id;
        this.filename = fn;
        this.url = createURL(xmppid, filename);
    }

     XMPPFileID(URL url) throws IDCreateException, MalformedURLException {
        Assert.isNotNull(url);
        this.url = url;
        this.xmppid = (XMPPID) IDFactory.getDefault().createID(getPropertyFromURL(XMPPIDNAMESPACE_KEY, url), getIDStringFromFileURL(url));
        this.filename = getPropertyFromURL(FILENAME_KEY, url);
    }

    private static String getIDStringFromFileURL(URL url) throws MalformedURLException {
        final String result = url.getAuthority();
        String path = url.getPath();
        //$NON-NLS-1$ //$NON-NLS-2$
        path = (path.startsWith("/") ? path : "/" + path);
        return result + path;
    }

    private static String getPropertyFromURL(String propKey, URL url) throws MalformedURLException {
        final String query = url.getQuery();
        if (//$NON-NLS-1$
        query == null || query.equals(""))
            throw new MalformedURLException(NLS.bind(//$NON-NLS-1$
            "Cannot have empty query for URL {0}", //$NON-NLS-1$
            url));
        //$NON-NLS-1$
        final StringTokenizer st = new StringTokenizer(query, "&");
        while (st.hasMoreTokens()) {
            final String tok = st.nextToken();
            if (//$NON-NLS-1$
            tok.startsWith(propKey + "=")) {
                try {
                    return URLDecoder.decode(tok.substring(propKey.length() + //$NON-NLS-1$
                    "=".length()), UTF_8);
                } catch (final UnsupportedEncodingException e) {
                    throw new MalformedURLException(NLS.bind("Could not decode {0} in URL {1}", propKey, url));
                }
            }
        }
        throw new MalformedURLException(NLS.bind("Key {0} not found in URL {1}", propKey, //$NON-NLS-1$
        url));
    }

    public static URL createURL(XMPPID xmppid2, String filename2) throws MalformedURLException {
        final StringBuffer buf = new StringBuffer(XMPPFileNamespace.SCHEME);
        //$NON-NLS-1$
        buf.append("://");
        buf.append(xmppid2.getFQName()).append(createQuery(filename2, xmppid2.getNamespace().getName()));
        return new URL(buf.toString());
    }

    public static String createQuery(String filename, String xmppidScheme) throws MalformedURLException {
        //$NON-NLS-1$
        final StringBuffer sb = new StringBuffer("?");
        try {
            sb.append(FILENAME_KEY).append("=").append(//$NON-NLS-1$
            URLEncoder.encode(//$NON-NLS-1$
            filename, //$NON-NLS-1$
            UTF_8));
            //$NON-NLS-1$
            sb.append("&");
            sb.append(XMPPIDNAMESPACE_KEY).append("=").append(//$NON-NLS-1$
            URLEncoder.encode(//$NON-NLS-1$
            xmppidScheme, //$NON-NLS-1$
            UTF_8));
            return sb.toString();
        } catch (final UnsupportedEncodingException e) {
            throw new MalformedURLException("filename encoding exception");
        }
    }

    public XMPPID getXMPPID() {
        return xmppid;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#toExternalForm()
	 */
    public String toExternalForm() {
        return url.toExternalForm();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.BaseID#namespaceCompareTo(org.eclipse.ecf
	 * .core.identity.BaseID)
	 */
    protected int namespaceCompareTo(BaseID o) {
        return getName().compareTo(o.getName());
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ecf.core.identity.BaseID#namespaceEquals(org.eclipse.ecf.
	 * core.identity.BaseID)
	 */
    protected boolean namespaceEquals(BaseID o) {
        if (!(o instanceof XMPPFileID))
            return false;
        final XMPPFileID other = (XMPPFileID) o;
        return this.xmppid.equals(other.xmppid) && this.filename.equals(other.filename);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceGetName()
	 */
    protected String namespaceGetName() {
        return toExternalForm();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.core.identity.BaseID#namespaceHashCode()
	 */
    protected int namespaceHashCode() {
        return this.xmppid.hashCode() ^ this.filename.hashCode();
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.identity.IFileID#getFilename()
	 */
    public String getFilename() {
        return filename;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.filetransfer.identity.IFileID#getURL()
	 */
    public URL getURL() throws MalformedURLException {
        return url;
    }

    /**
	 * @since 3.2
	 */
    public URI getURI() throws URISyntaxException {
        return new URI(url.toExternalForm());
    }
}
