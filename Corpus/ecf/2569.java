/* Copyright (c) 2006-2009 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.r_osgi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * URI is, well, an URI, as described in RFC 2396. Since
 * <code>java.net.URI</code> exists only since version 1.4, R-OSGi uses its own
 * URI class. This is a lightweight implementation, it does only as much as is
 * needed for R-OSGi. Furthermore, certain protocol schemes do address
 * resolution to avoid URI schizophrenia.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * 
 */
public final class URI implements Serializable {

    /**
	 * the scheme.
	 */
    private String scheme;

    /**
	 * the host.
	 */
    private InetAddress host;

    /**
	 * the host name.
	 */
    private String hostString;

    /**
	 * the port.
	 */
    private int port;

    /**
	 * the fragment. This describes the service ID of a service in R-OSGi.
	 */
    private String fragment;

    /**
	 * create a new URI.
	 * 
	 * @param uriString
	 *            the string.
	 */
    public  URI(final String uriString) {
        parse(uriString);
    }

    /**
	 * create a new URI
	 * 
	 * @param uriString
	 *            the string
	 * @param hostLookup
	 *            do a host lookup?
	 */
    public  URI(final String uriString, final boolean hostLookup) {
        parse(uriString);
        if (hostLookup) {
            try {
                host = InetAddress.getByName(hostString);
            } catch (final UnknownHostException uhe) {
                host = null;
            }
        }
    }

    /**
	 * convenience method for creating a new URI instance. Never throws and
	 * exception, even if the input is not well-formed. Should be used only in
	 * controlled environments when it can be assured that the input is valid.
	 * 
	 * @param uriString
	 *            the uri string.
	 * @return an URI.
	 */
    public static URI create(final String uriString) {
        try {
            return new URI(uriString);
        } catch (final Throwable t) {
            return null;
        }
    }

    /**
	 * parse an URI.
	 */
    private void parse(final String uriString) {
        try {
            int cs = 0;
            int ce = uriString.length();
            //$NON-NLS-1$
            final int p1 = uriString.indexOf("://");
            if (p1 > -1) {
                scheme = uriString.substring(0, p1);
                cs = p1 + 3;
            } else {
                //$NON-NLS-1$
                scheme = //$NON-NLS-1$
                "r-osgi";
            }
            //$NON-NLS-1$
            final int p2 = uriString.lastIndexOf("#");
            if (p2 > -1) {
                fragment = uriString.substring(p2 + 1);
                ce = p2;
            }
            //$NON-NLS-1$
            final int p4 = uriString.indexOf("/", cs);
            //$NON-NLS-1$
            final int p3 = uriString.indexOf(":", cs);
            if (p3 > -1 && p4 > p3) {
                port = Integer.parseInt(uriString.substring(p3 + 1, p4));
                ce = p3;
            } else if (p3 > -1) {
                port = Integer.parseInt(uriString.substring(p3 + 1, ce));
                ce = p3;
            } else {
                if (//$NON-NLS-1$
                "r-osgi".equals(scheme)) {
                    // FIXME: this should be the actual port of this instance
                    // !?!
                    port = 9278;
                } else if (//$NON-NLS-1$
                "http".equals(scheme)) {
                    port = 80;
                } else if (//$NON-NLS-1$
                "https".equals(scheme)) {
                    port = 443;
                }
            }
            hostString = uriString.substring(cs, ce);
        } catch (final IndexOutOfBoundsException i) {
            throw new IllegalArgumentException(uriString + " caused " + i.getMessage());
        }
    }

    /**
	 * get the protocol scheme.
	 * 
	 * @return the scheme.
	 */
    public String getScheme() {
        return scheme;
    }

    /**
	 * get the host name.
	 * 
	 * @return the host name.
	 */
    public String getHost() {
        return host == null ? hostString : host.getHostAddress();
    }

    /**
	 * get the port.
	 * 
	 * @return the port.
	 */
    public int getPort() {
        return port;
    }

    /**
	 * get the fragment.
	 * 
	 * @return the fragment (without the <i>#</i>).
	 */
    public String getFragment() {
        return fragment;
    }

    /**
	 * resolve a relative fragment against this absolute base URI.
	 * 
	 * @param add
	 *            the fragment.
	 * @return the resolved URI
	 */
    public URI resolve(final String add) {
        return URI.create(toString() + add);
    }

    /**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
    public int hashCode() {
        return scheme.hashCode() + (host == null ? hostString.hashCode() : host.hashCode()) + port + (fragment != null ? fragment.hashCode() : 0);
    }

    /**
	 * 
	 * @see java.lang.Object#toString()
	 */
    public String toString() {
        return //$NON-NLS-1$ //$NON-NLS-2$
        scheme + "://" + getHost() + ":" + port + //$NON-NLS-1$ //$NON-NLS-2$
        (fragment == null ? "" : "#" + fragment);
    }

    /**
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    public boolean equals(final Object other) {
        if (other instanceof String) {
            return equals(new URI((String) other, true));
        } else if (other instanceof URI) {
            final URI otherURI = (URI) other;
            return scheme.equals(otherURI.scheme) && (host == null ? hostString.equals(otherURI.hostString) : host.equals(otherURI.host)) && port == otherURI.port && ((fragment == null && otherURI.fragment == null) || fragment != null && fragment.equals(otherURI.fragment));
        } else {
            return false;
        }
    }

    /**
	 * write and object.
	 * 
	 * @param out
	 *            the output.
	 * @throws IOException
	 *             if something goes wrong.
	 */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeUTF(toString());
    }

    /**
	 * read an object.
	 * 
	 * @param in
	 *            the input.
	 * @throws IOException
	 *             if something goes wrong.
	 */
    private void readObject(final ObjectInputStream in) throws IOException {
        parse(in.readUTF());
    }
}
